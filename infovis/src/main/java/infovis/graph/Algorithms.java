/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.column.AbstractIntColumn;
import infovis.column.BooleanColumn;
import infovis.column.IntColumn;
import infovis.column.StringColumn;
import infovis.graph.property.Degree;
import infovis.utils.IntStack;
import infovis.utils.InverseRowComparator;
import infovis.utils.Permutation;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;
import cern.colt.Sorting;
import cern.colt.function.IntProcedure;
import cern.colt.list.IntArrayList;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.20 $
 */
public class Algorithms {

    /**
     * Returns the edges of a specified vertex sorted by a specified order.
     * @param graph the graph
     * @param vertex the vertex
     * @param comp the comparator
     * @return the edges of the vertex, sorted
     */
    public static int[] sortEdges(Graph graph, int vertex,  RowComparator comp) {
        int size = graph.getOutDegree(vertex);
        int[] sorted = new int[size];
        int i = 0;

        for (RowIterator iter = graph.outEdgeIterator(vertex); iter
                .hasNext();) {
            int edge = iter.nextRow();
            sorted[i++] = edge;
        }
        if (sorted.length > 1)
            Sorting.mergeSort(sorted, 0, sorted.length, comp);

        return sorted;
    }

    /**
     * Copy one graph into another.
     * 
     * @param fromGraph
     *            the source graph
     * @param toGraph
     *            the destination graph
     * @return the mapping from the vertices in the source graph to the vertices
     *         in the destination graph.
     */
    public static OpenIntIntHashMap copy(Graph fromGraph, Graph toGraph) {
        OpenIntIntHashMap map = new OpenIntIntHashMap();
        for (RowIterator edge = fromGraph.edgeIterator(); edge
                .hasNext();) {
            int e = edge.nextRow();
            int v1 = fromGraph.getFirstVertex(e);
            if (map.containsKey(v1)) {
                v1 = map.get(v1);
            } else {
                int v = toGraph.addVertex();
                map.put(v1, v);
                v1 = v;
            }
            int v2 = fromGraph.getSecondVertex(e);
            if (map.containsKey(v2)) {
                v2 = map.get(v2);
            } else {
                int v = toGraph.addVertex();
                map.put(v2, v);
                v2 = v;
            }
            toGraph.addEdge(v1, v2);
        }
        return map;
    }

    /**
     * Returns a bigger, undirected test graph with a just one component. This
     * graph consists of a clique of ten edges, a partial clique (randomly
     * generated, with edges of 0.6 probability), and one series of edges
     * running from the first node to the last.
     * 
     * Adapted from JUNG (jung.sourceforge.net)
     * 
     * @return the testgraph
     */
    public static Graph getOneComponentGraph() {
        DefaultGraph g = new DefaultGraph();
        g.setDirected(false);
        StringColumn sl = StringColumn.findColumn(g.getVertexTable(),
                "label");
        IntColumn el = IntColumn.findColumn(g.getEdgeTable(), "weight");

        for (int i = 0; i < 20; i++) {
            int v = g.addVertex();
            assert (v == i);
            sl.setExtend(v, "" + v+1);
        }
        // let's throw in a clique, too
        for (int i = 1; i <= 10; i++) {
            for (int j = i + 1; j <= 10; j++) {
                int edge = g.addEdge(i - 1, j - 1);
                el.setExtend(edge, i + j);
            }
        }

        // and, last, a partial clique
        for (int i = 11; i <= 20; i++) {
            for (int j = i + 1; j <= 20; j++) {
                if (Math.random() > 0.6)
                    continue;
                int edge = g.addEdge(i - 1, j - 1);
                el.setExtend(edge, i + j);
            }
        }

        // and one edge to connect them all
        //JDF: JUNG is unpredictable in its order whereas InfoVis is
        // totaly predictable so random pemute the generated path
//        for (int i = 0; i < g.getVerticesCount() - 1; i++) {
//            g.findEdge(i, i+1);
//        }
        IntArrayList list = new IntArrayList(20);
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        list.shuffle();
        for (int i = 0; i < list.size()-1; i++) {
            g.findEdge(list.get(i), list.get(i+1));
        }
        

        return g;
    }
    
    /**
     * Returns a grid mesh graph with the specified width and height.
     * @param width the width
     * @param height the height
     * @return a grid mesh graph
     */
    public static Graph getGridGraph(int width, int height) {
        DefaultGraph g = new DefaultGraph();
        g.setDirected(false);
        int x, y;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                int v = g.addVertex();
                if (x != 0) {
                    g.addEdge(v-1, v);
                }
                if (y != 0) {
                    g.addEdge(v-width, v);
                }
            }
        }
        
        return g;
    }
    
    /**
     * Finds the connected component of the specified vertex: all the
     * vertices that are reachable from that vertex or that can reach that vertex. 
     * @param g the graph
     * @param vertex the vertex
     * @param map the set of vertices
     */
    public static void findComponent(Graph g, int vertex, AbstractIntIntMap map) {
        assert(g.getVertexTable().isRowValid(vertex));
        if (map.containsKey(vertex)) return;
        IntStack stack = new IntStack();
        RowIterator allocIter = g.newEdgeIterator();
        stack.push(vertex);
        while(! stack.isEmpty()) {
            int v = stack.pop();
            assert(!map.containsKey(v));
            map.put(v, map.size()+1);
            for (RowIterator iter = g.edgeIterator(v, allocIter); iter.hasNext(); ) {
                int e = iter.nextRow();
                int v2 = g.getOtherVertex(e, v);
                assert(g.getVertexTable().isRowValid(v2));
                if (map.containsKey(v2)) {
                    continue;
                }
                int pos = stack.binarySearch(v2);
                if (pos < 0) {
                    stack.beforeInsert(-pos-1, v2);
                }
            }
        }
    }
    
    /**
     * Computes the connected components of the specified graph and
     * store the component labels in the specified IntColumn and the
     * component sizes in the other IntColumn.
     * @param graph the graph
     * @param labels the label IntColumn to fill with component labels
     * @param sizes the component sizes
     * @return the number of components
     */
    public static int labelConnectedComponents(Graph graph, IntColumn labels, IntColumn sizes) {
        int comp = 0;
        OpenIntIntHashMap map = new OpenIntIntHashMap();
        final IntColumn l = (labels == null) ? new IntColumn("components") : labels;
        for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
            int vertex = iter.nextRow();
            if (l.isValueUndefined(vertex)) {
                findComponent(graph, vertex, map);
                final int label = comp;
                map.forEachKey(new IntProcedure() {
                    public boolean apply(int i) {
                        l.setExtend(i, label);
                        return true;
                    }
                });
//                for (RowIterator kiter = new IntArrayIterator(map.keys());
//                    kiter.hasNext(); ) {
//                    int i = kiter.nextRow();
//                    l.setExtend(i, label);
//                }
                if (sizes != null) {
                    sizes.setExtend(comp, map.size());
                }
                map.clear();
                comp++;
            }
        }
        return comp;
    }
    
    /**
     * Computes the connected components of the specified graph and returns
     * a table of IntArrayList containing the vertices of each component
     * in descending order of size.
     * @param graph the graph
     * @return the table of IntArrayList containing the vertices of each connected
     * component, sorted in descending order of size
     */
    public static IntArrayList[] computeConnectedComponents(Graph graph) {
        IntColumn labels = new IntColumn("#labels");
        IntColumn sizes = new IntColumn("#sizes");
        int n = Algorithms.labelConnectedComponents(graph, labels, sizes);
        IntArrayList[] comps = new IntArrayList[n];
        Permutation perm = new  Permutation(n);
        perm.sort(new InverseRowComparator(sizes));
        for (int i = 0; i < n; i++) {
            comps[i] = new IntArrayList(sizes.get(perm.getInverse(i)));
        }

        for (RowIterator iter = labels.iterator(); iter.hasNext(); ) {
            int i = iter.nextRow();
            comps[perm.getInverse(labels.get(i))].add(i);
        }
        return comps;
    }
    

    /**
     * Connects the specified graph and returns the list of added edges.
     * @param graph the graph
     * @return the list of added edges
     */
    public static IntArrayList connectGraph(Graph graph) {
        IntArrayList[] comps = Algorithms.computeConnectedComponents(graph);
        if (comps.length < 2) {
            return null;
        }
        IntArrayList addedEdges = new IntArrayList();
        AbstractIntColumn degree = Degree.getColumn(graph);
        // Sort vertices by increasing degree. We pick a small degree preferably.
        // Small degree at at the begining so we use a random distribution
        // skewed to the begining.
        RandomEngine rand = RandomEngine.makeDefault();
        for (int i = 1; i < comps.length; i++) {
            Sorting.quickSort(comps[i].elements() , 0, comps[i].size(), degree);
        }
        for (int i = 1; i < comps.length; i++) {
            Sorting.quickSort(comps[i].elements() , 0, comps[i].size(), degree);
            int i1 = (int)(Math.pow(rand.raw(),3)*comps[0].size());
            int v1 = comps[0].get(i1);
            int i2 = (int)(Math.pow(rand.raw(),3)*comps[i].size());
            int v2 = comps[i].get(i2);
            addedEdges.add(graph.addEdge(v1, v2));
        }
        return addedEdges;
    }
    
    /** Name of the boolean column containing the partition of a bipartite graph. */
    public static final String COLUMN_BIPARTITE = "[Bipartite]";

    /**
     * Computes the partition of a bipartite graph, returns a
     * BooleanColumn or null if the graph is not bipartatite.
     * @param graph the graph
     * @return the BooleanColumn containing the partition or null
     */
    public static BooleanColumn computeBipartite(Graph graph) {
        BooleanColumn type = new BooleanColumn(COLUMN_BIPARTITE);
        IntStack stack = new IntStack();
        RowIterator allocIter = graph.newEdgeIterator();
        for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
            int v = iter.nextRow();    
            int c = 0;
            
            if (! type.isValueUndefined(v)) {
                continue;
            }
            stack.push(v);
            stack.push(c);
            
            while (! stack.isEmpty()) {
                c = stack.pop();
                v = stack.pop();
                
                if (type.isValueUndefined(v)) {
                    type.setExtend(v, c==0);
                    c = 1-c;
                    for (RowIterator eIter = graph.edgeIterator(v, allocIter);
                        eIter.hasNext(); ) {
                        int edge = eIter.nextRow();
                        int other = graph.getOtherVertex(edge, v);
                        stack.push(other);
                        stack.push(c);
                    }
                }
                else if (type.get(v)!=(c==0)) {
                    return null;
                }
            }
        }
        return type;
    }
    
    /**
     * Returns the boolean partition column of the specified graph.
     * @param g the graph
     * @return the partition column or null
     */
    public static BooleanColumn findBipartite(Graph g) {
        BooleanColumn c = (BooleanColumn)g.getVertexTable().getColumn(COLUMN_BIPARTITE);
        if (c == null) {
            c = computeBipartite(g);
            if (c != null) 
            	g.getVertexTable().addColumn(c);
        }
        return c;
    }
}