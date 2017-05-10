import infovis.DynamicTable;
import infovis.Graph;
import infovis.Table;
import infovis.graph.DefaultGraph;
import infovis.graph.Edge;
import infovis.graph.Vertex;
import infovis.graph.DefaultGraph.DefaultIntColumnFactory;
import infovis.graph.algorithm.DijkstraShortestPath;
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.utils.RowIterator;

import java.util.BitSet;

import junit.framework.TestCase;
import cern.colt.function.IntIntDoubleProcedure;
import cern.colt.list.IntArrayList;
import cern.jet.random.Uniform;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.22 $
 */
public class GraphTest extends TestCase {
    public GraphTest(String name) {
        super(name);
    }

    public void testGraph() {
        testGraph(new DefaultGraph());
        testGraph(new DefaultGraph(false, true, DefaultIntColumnFactory.INSTANCE));
        final DefaultGraph g = new DefaultGraph(true);
        Table edgeTable = g.getEdgeTable();
        Table vertexTable = g.getVertexTable();
        assertEquals(g, DefaultGraph.getGraph(edgeTable));
        assertEquals(g, DefaultGraph.getGraph(vertexTable));
        assertEquals(g.getFactory(), DefaultIntColumnFactory.INSTANCE);
        g.setDirected(false);
        g.setDirected(true);
        g.addGraphChangedListener(new GraphChangedListener() {
            public void graphChanged(GraphChangedEvent e) {
                e.getDetail();
                assertEquals(g, e.getGraph());
                e.getType();
            }
        });
        testGraph(g);
        int v1 = g.addVertex();
        int e1 = g.addEdge(v1, v1);
        boolean exception = false;
        try {
            g.setDirected(true);
            g.setDirected(false);
        }
        catch(Exception e) {
            exception = true;
        }
        assertTrue("Exception calling setDirected", exception);
        assertEquals(g.findEdge(v1, v1), e1);
        assertEquals(g.getInDegree(v1), 1);
        assertEquals(g.getOutDegree(v1), 1);
        assertEquals(g.getDegree(v1), 2); // strange enough
        g.removeVertex(v1);
        assertEquals(g.findEdge(v1, v1), Graph.NIL);
    }
    
    public void testGraph(Graph graph) {
        //TableTest.testInvariants(graph.getEdgeTable());
        //TableTest.testInvariants(graph.getVertexTable());
        assertEquals(graph.getMetadata().getAttributeCount(), 0);
        assertEquals(graph.getClientProperty().getAttributeCount(), 0);
        graph.setName("@@");
        assertEquals(graph.getName(), "@@");
        assertEquals(0, graph.getVerticesCount());
        
        int vertex = graph.addVertex();
        assertEquals(1, graph.getVerticesCount());
        
        int vertex2 = graph.addVertex();
        assertEquals(2, graph.getVerticesCount());
        
        int edge = graph.addEdge(vertex, vertex2);
        assertEquals(2, graph.getVerticesCount());
        assertEquals(graph.getOutEdgeAt(vertex, 0), edge);
        assertEquals(1, graph.getEdgesCount());
        assertEquals(graph.getSecondVertex(edge), vertex2);
        assertEquals(graph.getFirstVertex(edge), vertex);
        
        RowIterator iter = graph.outEdgeIterator(vertex);
        assertEquals(iter.nextRow(), edge);
        assertEquals("end of iterator", false, iter.hasNext());
        
        int vertex3 = graph.addVertex();
        assertEquals(3, graph.getVerticesCount());
        
        int edge2 = graph.addEdge(vertex, vertex3);
        assertEquals(2, graph.getEdgesCount());
        assertEquals(2, graph.getOutDegree(vertex));
        assertEquals(3, graph.getVerticesCount());
        
        iter = graph.outEdgeIterator(vertex);
        assertEquals(iter.nextRow(), edge);
        
        assertEquals(iter.nextRow(), edge2);
        
        assertEquals("end of iterator", false, iter.hasNext());
        
        assertEquals(graph.getFirstVertex(edge2), vertex);
        
        assertEquals(graph.getSecondVertex(edge2), vertex3);

        graph.removeVertex(vertex);
        assertEquals(2, graph.getVerticesCount());
        assertEquals(0, graph.getEdgesCount());
        assertEquals("invalid vertex", false, graph.getVertexTable().isRowValid(vertex));
        assertEquals("invalid edge", false, graph.getEdgeTable().isRowValid(edge));
        assertEquals("invalid edge", false, graph.getEdgeTable().isRowValid(edge2));
        
        iter = graph.vertexIterator();
        assertEquals(vertex2, iter.nextRow());
        assertEquals(vertex3, iter.nextRow());
        assertEquals("end of iterator", false, iter.hasNext());
        
        iter = graph.edgeIterator();
        assertEquals("end of iterator", false, iter.hasNext());
        
        vertex = graph.addVertex();
        assertEquals(3, graph.getVerticesCount());
        assertEquals(0, graph.getOutDegree(vertex));
        
        graph.clear();
        assertEquals("Empty graph", 0, graph.getVerticesCount());
        assertEquals("Empty graph", 0, graph.getEdgesCount());
        
        int i;
        for (i = 0; i < 100; i++) {
            assertEquals(i, graph.addVertex());
        }
        
        assertEquals(0, graph.addEdge(0, 0));
        for (i = 1; i < 100; i++) {
            assertEquals(i, graph.addEdge(i, i-1));
        }
        for (i = 0; i < 99; i++) {
            assertEquals(i+100, graph.addEdge(i, i+1));
        }
        
        for (i = 0; i < 9; i++) {
            graph.removeVertex(i*10+1);
            assertEquals(graph.getVerticesCount(), 100 - i - 1);
            // Vertex 0 has 3 edges, the others have 4 edges
            assertEquals(199 - 4*(i+1), graph.getEdgesCount());
        }
        
        graph.clear();
        assertEquals("Empty graph", 0, graph.getVerticesCount());
        assertEquals("Empty graph", 0, graph.getEdgesCount());
        
        for (i = 0; i < 1000; i++) {
            assertEquals(i, graph.addVertex());
        }
        
        IntArrayList ial = new IntArrayList(10000);
        for (i = 0; i < 10000; i++) {
            int from = Uniform.staticNextIntFromTo(0, 100);
            int to = Uniform.staticNextIntFromTo(0, 100);
            int e = graph.addEdge(from, to);
            ial.add(i);
            assertEquals(e, i);
            assertEquals(graph.getOtherVertex(e, from), to);
            assertEquals(graph.getOtherVertex(e, to), from);
            if (graph.isDirected()) {
                assertEquals(graph.getFirstVertex(e), from);
                assertEquals(graph.getSecondVertex(e), to);
            }
        }
        checkEdges(graph);
        ial.shuffle();
        for (i = 0; i < 5000; i++) { // remove half of the edges
            int e = ial.get(i);
            graph.removeEdge(e);
            assertEquals(graph.getEdgeTable().isRowValid(e), false);
        }
        ial.clear();
        for (i = 0; i < 1000; i++) {
            ial.add(i);
        }
        ial.shuffle();
        
        for (i = 0; i < 500; i++) {
            int v = ial.get(i);
            graph.getInEdgeAt(v, 3);
            graph.getOutEdgeAt(v, 3);
            graph.removeVertex(v);
            assertEquals(graph.getEdge(v, v), Graph.NIL);
        }
        DynamicTable vertexTable = graph.getVertexTable();
        for (i = 500; i < 600; i++) {
            int v = ial.get(i);
            vertexTable.removeRow(v);
        }
        for (i = 0; i < 100; i++) {
            int v = vertexTable.addRow();
            assertEquals(0, graph.getDegree(v));
        }
        checkEdges(graph);
        RowIterator allocIter = graph.newEdgeIterator();
        for (RowIterator iiter = graph.vertexIterator(); iiter.hasNext(); ) {
            int v = iiter.nextRow();
            for (RowIterator edgeIter = graph.outEdgeIterator(v, allocIter);
            edgeIter.hasNext(); ) {
                int e = edgeIter.nextRow();
                graph.removeEdge(e);
            }
        }
        checkEdges(graph);
        testGraphObjects(graph);
    }
    
    public void testGraphObjects(Graph graph) {
        int i;
        graph.clear();
        
        Vertex[] al = new Vertex[1000];
        for (i = 0; i < 1000; i++) {
            Vertex v = graph.add();
            al[i] = v;
            assertEquals(i, v.getId());
            Graph g= v.getGraph();
            assertTrue(g==graph);
        }
        IntArrayList ial = new IntArrayList(10000);
        Edge[] edges = new Edge[10000];
        for (i = 0; i < 10000; i++) {
            Vertex from = al[Uniform.staticNextIntFromTo(0, 100)];
            Vertex to = al[Uniform.staticNextIntFromTo(0, 100)];
            Edge e = graph.addEdge(from, to);
            if (graph.isDirected()) {
                assertEquals(e.getFirstVertex(), from);
                assertEquals(e.getSecondVertex(), to);
            }
            else {
                assertEquals(graph.getOtherVertex(e, from), to);
                assertEquals(graph.getOtherVertex(e, to), from);
            }
            edges[i] = e;
            ial.add(i);
            assertEquals(e.getId(), i);
            assertEquals(e.getGraph(), graph);
            assertEquals(graph.getOtherVertex(e, from), to);
            assertEquals(graph.getOtherVertex(e, to), from);
            if (graph.isDirected()) {
                assertEquals(graph.getFirstVertex(e), from);
                assertEquals(graph.getSecondVertex(e), to);
            }
        }
        ial.shuffle();
        for (i = 0; i < 5000; i++) { // remove half of the edges
            Edge e = edges[ial.get(i)];
            graph.removeEdge(e);
//            assertEquals(graph.getEdgeTable().isRowValid(e), false);
        }
        
        ial.clear();

        for (i = 0; i < 1000; i++) {
            ial.add(i);
        }
        ial.shuffle();
        
        for (i = 0; i < 500; i++) {
            Vertex v = al[ial.get(i)];
            graph.removeVertex(v);
        }
        DynamicTable vertexTable = graph.getVertexTable();
        for (i = 500; i < 600; i++) {
            Vertex v = al[ial.get(i)];
            vertexTable.removeItem(v);
        }
        for (i = 0; i < 100; i++) {
            int v = vertexTable.addRow();
            assertEquals(0, graph.getDegree(v));
        }
        checkEdges(graph);
        RowIterator allocIter = graph.newEdgeIterator();
        for (RowIterator iiter = graph.vertexIterator(); iiter.hasNext(); ) {
            int v = iiter.nextRow();
            for (RowIterator edgeIter = graph.outEdgeIterator(v, allocIter);
            edgeIter.hasNext(); ) {
                int e = edgeIter.nextRow();
                graph.removeEdge(e);
            }
        }
        checkEdges(graph);
        i = 0;
        for (RowIterator iter = graph.edgeIterator(); iter.hasNext(); ) {
            i++;
        }
        assertEquals(i, graph.getEdgesCount());
    }

    private void checkEdges(Graph graph) {
        int i;
        Table t = graph.getVertexTable();
        for (i = 0; i < 1000; i++) {
            if (! t.isRowValid(i)) continue;
            for (int j = 0; j < 1000; j++) {
                if (! t.isRowValid(j)) continue;
                int e = graph.getEdge(i, j);
                if (e != Graph.NIL) {
                    assertEquals(graph.getOtherVertex(e, i), j);
                    assertEquals(graph.getOtherVertex(e, j), i);
                    if (graph.isDirected()) {
                        assertEquals(graph.getFirstVertex(e), i);
                        assertEquals(graph.getSecondVertex(e), j);
                    }
                }
            }
        }
    }
    
    protected void checkRemoved(BitSet removed, Graph graph) {
        for (RowIterator iter = graph.edgeIterator(); iter.hasNext(); ) {
            int e = iter.nextRow();
            assertTrue("Edge "+e+" invalid", graph.getEdgeTable().isRowValid(e));
            if (graph.getFirstVertex(e) < 0) {
                int v1 = graph.getFirstVertex(e);
                int v2 = graph.getSecondVertex(e);
                assertTrue("Edge removed by vertex "+(-v1)+" or "+(-v2),false);
                assertTrue(
                        "Invalid 'in' vertex: "+graph.getFirstVertex(e),
                        graph.getVertexTable().isRowValid(graph.getFirstVertex(e)));
            }
            assertTrue(
                    "Invalid 'in' vertex: "+graph.getFirstVertex(e),
                    graph.getVertexTable().isRowValid(graph.getFirstVertex(e)));
            assertTrue(
                    "Invalid 'out' vertex: "+graph.getSecondVertex(e),
                    graph.getVertexTable().isRowValid(graph.getSecondVertex(e)));
            assertTrue(
                    "Edge "+e+" references vertex "+graph.getFirstVertex(e),
                    removed.get(graph.getFirstVertex(e)));
            
            assertTrue(
                    "Edge "+e+" references vertex "+graph.getSecondVertex(e),
                    removed.get(graph.getSecondVertex(e)));
        }
    }
    
    public void testInsertRemove() {
        DefaultGraph graph = new DefaultGraph();
        int u, v;
        IntArrayList ial = new IntArrayList(100);
        BitSet bs = new BitSet();

        for (v = 0; v < 100; v++) {
            assertEquals(v, graph.addVertex());
            ial.add(v);
            bs.set(v);
        }
        
        for (v = 0; v < 90; v++) {
            for (u = v; u < v+10; u++) {
                graph.addEdge(v, u);
            }
        }
//        cern.jet.random.Uniform gen = new cern.jet.random.Uniform(new cern.jet.random.engine.DRand(100));
//        for (int i=0; i<99; i++) { 
//            int random = gen.nextIntFromTo(i, 99);
//    
//            //swap(i, random)
//            int tmpElement = ial.getQuick(random);
//            ial.setQuick(random,ial.getQuick(i)); 
//            ial.setQuick(i,tmpElement); 
//        }          
        ial.shuffle();
        for (v = 0; v < 100; v++) {
            int i = ial.get(v);
            assertTrue("Vertex already removed "+i, graph.getVertexTable().isRowValid(i));
            graph.removeVertex(i);
            assertTrue("Vertex not removed "+i, !graph.getVertexTable().isRowValid(i));
            bs.clear(i);
            checkRemoved(bs, graph);
        }
    }
    
//    public void testDenseGraph() {
//        int size = 5;
//        DenseGraph g = new DenseGraph(size);
//        assertEquals(size, g.getVerticesCount());
//        assertEquals(size*size, g.getEdgesCount());
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                assertEquals(i*size+j, g.findEdge(i, j));
//            }
//        }
//        for (RowIterator vIter = g.vertexIterator(); vIter.hasNext(); ) {
//            int v = vIter.nextRow();
//            for (RowIterator eIter = g.outEdgeIterator(v); eIter.hasNext(); ) {
//                int e = eIter.nextRow();
//                System.out.print(e+" ");
//            }
//            System.out.println();
//        }
//        for (RowIterator vIter = g.vertexIterator(); vIter.hasNext(); ) {
//            int v = vIter.nextRow();
//            for (RowIterator eIter = g.inEdgeIterator(v); eIter.hasNext(); ) {
//                int e = eIter.nextRow();
//                System.out.print(e+" ");
//            }
//            System.out.println();
//        }        
//    }
//    
    public void testDijkstra() {
        final DefaultGraph g = new DefaultGraph();
        final int v1 = g.addVertex();
        final int v2 = g.addVertex();
        final int v3 = g.addVertex();
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
//        DijkstraShortestPath dijkstra = new DijkstraShortestPath(g);
//        p = dijkstra.shortestPath(v1, v1);
//        assertEquals(0, (int)p.getWeight());
//        p = dijkstra.shortestPath(v1, v2);
//        assertEquals(1, (int)p.getWeight());
//        p = dijkstra.shortestPath(v1, v3);
//        assertEquals(2, (int)p.getWeight());
        DijkstraShortestPath.allShortestPath(
                g, 
                v1, 
                new IntIntDoubleProcedure() {
            public boolean apply(int edge, int v, double d) {
                if (v == v1)  {
                    assertEquals(0, (int)d);
                }
                else if (v == v2) {
                    assertEquals(1, (int)d);
                }
                else if (v == v3) {
                    assertEquals(2, (int)d);
                }
                else {
                    fail("Unknown vertex visited");
                }
                return true;
            }
        });
    }
}
