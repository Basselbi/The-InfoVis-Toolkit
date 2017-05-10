/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.Table;
import infovis.column.ColumnOne;
import infovis.column.NumberColumn;
import infovis.utils.Heap;
import infovis.utils.IntLinkedList;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;
import cern.colt.function.IntIntDoubleProcedure;
import cern.colt.map.AbstractIntObjectMap;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * <b>DijkstraShortestPath</b> is used to compute
 * shortest paths using the algorithm of
 * Dijkstra.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public class DijkstraShortestPath extends Algorithm {
    protected OpenIntObjectHashMap vertexMaps;
    protected NumberColumn edgeWeights;
   
    /**
     * Creates a DijkstraShortestPath using a specified weight column and
     * a flag specifiying if the results should be cached across method calls.
     * @param graph the graph
     * @param edgeWeights the (positive) edge weights or null
     * @param cached true if the results should be kept across method calls
     */
    public DijkstraShortestPath(Graph graph, NumberColumn edgeWeights, boolean cached) {
        super(graph);
        this.edgeWeights = edgeWeights;
        if (cached) {
            vertexMaps = new OpenIntObjectHashMap();
        }
    }
  
    /**
     * Creates a DijkstraShortestPath using 
     * a flag specifiying if the results should be cached across method calls.
     * @param graph the graph
     * @param cached true if the results should be kept across method calls
     */
    public DijkstraShortestPath(Graph graph, boolean cached) {
        this(graph, null, cached);
    }
    
    /**
     * Creates a DijkstraShortestPath.
     * @param graph the graph
     */
    public DijkstraShortestPath(Graph graph) {
        this(graph, true);
    }
      
    /**
     * Computes the shortes paths 2D table for each vertices
     * of the graph.
     * @param graph the graph
     * @param edgeWeights the (positive) edge weights or null 
     * @param matrix the matrix to fill or null
     * @return the shortest path 2D table
     */
    public static DoubleMatrix2D allShortestPaths(
            Graph graph, 
            NumberColumn edgeWeights,
            DoubleMatrix2D matrix) {
        assert(!graph.isDirected());
        Permutation perm = new Permutation();
        for (RowIterator viter = graph.vertexIterator(); viter.hasNext(); ) {
            int from = viter.nextRow();
            perm.add(from);
        }
        int size = perm.size();
        RowIterator allocIter = graph.newEdgeIterator();
        
        if (matrix == null
                || matrix.columns() < size 
                || matrix.rows() < size) {
            matrix = DoubleFactory2D.dense.make(size, size, Double.POSITIVE_INFINITY);
        }
        else {
            matrix.assign(Double.POSITIVE_INFINITY);
        }
        if (edgeWeights == null) {
            edgeWeights = ColumnOne.INSTANCE;
        }
                
        for (RowIterator viter = graph.vertexIterator(); viter.hasNext(); ) {
            int from = perm.getInverse(viter.nextRow());
            Predecessor[] S = new Predecessor[size];
            Predecessor[] queued = new Predecessor[size];
            Heap queue = new Heap();
            
            Predecessor p = new Predecessor(from, 0);
            queue.insert(p);
            queued[from] = p;
//            for (int j = 0; j < size; j++) {
//                double d = matrix.getQuick(j, from);
//                if (d!=Double.POSITIVE_INFINITY) {
//                    queued[j] = new Predecessor(j, d);
//                }
//            }
            while(! queue.isEmpty()) {
                p = (Predecessor)queue.pop();
                int v = perm.getDirect(p.vertex);
                S[v] = p;
                for (RowIterator iter = graph.edgeIterator(v, allocIter); iter.hasNext(); ) {
                    int edge = iter.nextRow();
                    double d = p.weight+ edgeWeights.getDoubleAt(edge);
                    int v2 = graph.getOtherVertex(edge, v);
                    assert(v2 != Graph.NIL);
                    v2 = perm.getInverse(v2);
                    Predecessor D = queued[v2];
                    if (D == null) {
                        Predecessor p2 = new Predecessor(v2, d);
                        queue.insert(p2);
                        queued[v2] = p2;
                    }
                    else if (D.weight > d) {
                        D.weight = d;
                        D.edge = v;
                        queue.update(D);
                    }
                }
            }
            matrix.setQuick(from, from, 0);
            for (int j = 0; j < S.length; j++) {
                p = S[j];
                if (p != null) {
                    assert(p.vertex==j);
                    matrix.setQuick(from, j, p.weight);
                }
            }
        }
        return matrix;
    }
  
    /**
     * @return true if the computed results are cached across
     * calls
     */
    public boolean isCached() {
        return vertexMaps != null;
    }
    
    /**
     * Sets whether the computed results are cached across
     * calls. 
     * @param cached true if the results are cached
     */
    public void setCached(boolean cached) {
        if (cached) {
            if (vertexMaps == null) {
                vertexMaps = new OpenIntObjectHashMap();
            }
        }
        else {
            vertexMaps = null;
        }
    }
    
    /**
     * Returns the edge weight for a specified edge.
     * @param edge the edge
     * @return the edge weight
     */
    public double getEdgeWeight(int edge) {
        if (edgeWeights != null && !edgeWeights.isValueUndefined(edge)) {
            double w = edgeWeights.getDoubleAt(edge);
            assert(w>=0);
            return w;
        }
        else {
            return 1;
        }
    }
    
    /**
     * Returns the shortest path between two vertices.
     * @param from the starting vertex
     * @param to the ending vertex
     * @return the shortest path between two vertices.
     */
    public Predecessor shortestPath(int from, int to) {
        OpenIntObjectHashMap map = allShortestPaths(from);
        return shortestPath(map, to);
    }
    
    /**
     * Returns the shortest path to a given vertex in a previously-computed
     * allshortestPaths map of all shortest paths from another vertex.
     * @param map	the previously computed allShortestPaths from some other vertex
     * @param to 	the ending vertex
     * @return 		the shortest path to the given vertex
     * 
     * Note:  USE THIS, NOT shortestPath(from, to) if computing multiple vertices: O(n^2) vs. O(n^3) for all !!!!
     */
    public static Predecessor shortestPath(AbstractIntObjectMap map, int to) {
        if (map == null) return null;
        Predecessor p = (Predecessor)map.get(to);
        return p;    	
    }

    /**
     * Returns a map of all the vertices reachable from the
     * specified vertex and the path length.
     * @param from the initial vertex
     * @return a map of all the vertices reachable from the
     * specified vertex and the path length
     */
    public OpenIntObjectHashMap allShortestPaths(int from) {
        if (vertexMaps != null && vertexMaps.containsKey(from)) {
            return (OpenIntObjectHashMap)vertexMaps.get(from);
        }
        final OpenIntObjectHashMap S = new OpenIntObjectHashMap();
        
        setProgressValues(0, graph.getVerticesCount(), "Distances");
        boolean ok = allShortestPaths(
                graph, 
                from, 
                edgeWeights,
                S,
                new IntIntDoubleProcedure() {
                    int count = 0;
                    public boolean apply(int edge, int v, double dist) {
                        setProgress(count++);
                        return true;
                    }
                
                });
        terminate();
        if (!ok) {
            return null;
        }
        if (vertexMaps != null) {
            vertexMaps.put(from, S);
        }
        return S;
    }

    /**
     * Returns a map of all the vertices reachable from the
     * specified vertex and the path length.
     * @param graph the graph
     * @param from the initial vertex
     * @param weight the weight column
     * @param queued a hash map containing the predecessors or null
     * @param proc the proc which will be called with and edge, vertex and distance
     * @return true if it went to the end, false otherwise
     * specified vertex and the path length
     */
    public static boolean allShortestPaths(
            Graph graph,
            int from,
            NumberColumn weight,
            OpenIntObjectHashMap queued,
            IntIntDoubleProcedure proc) {
        if (weight == null && queued == null) {
            return allShortestPath(graph, from, proc);
        }
        Heap queue = new Heap();
        boolean keepQueued = true;
        if (queued == null) {
            queued = new OpenIntObjectHashMap();
            keepQueued = false;
        }
        if (weight == null) {
            weight = ColumnOne.INSTANCE;
        }
        Predecessor p = new Predecessor(from, 0);
        queue.insert(p);
        queued.put(from, p);
        RowIterator edgeIter = graph.newEdgeIterator();
        
        while(! queue.isEmpty()) {
            p = (Predecessor)queue.pop();
            if (! proc.apply(p.edge, p.vertex, p.weight))
                return false;
            int v = p.vertex;
            if (! keepQueued)
                queued.removeKey(v); // processed so not needed any more
            for (RowIterator iter = graph.edgeIterator(v, edgeIter); iter.hasNext(); ) {
                int edge = iter.nextRow();
                double d = p.weight + weight.getDoubleAt(edge);
                int v2 = graph.getOtherVertex(edge, v);
                assert(v2 != Graph.NIL);
                Predecessor D = (Predecessor)queued.get(v2);
                if (D == null) { // dist == infinity
                    Predecessor p2 = new Predecessor(edge, v2, d);
                    queue.insert(p2);
                    queued.put(v2, p2);
                }
                else if (D.weight > d) {
                    D.weight = d;
                    D.edge = edge;
                    queue.update(D); // update the heap
                }
            }
        }
        return true;
    }
    /**
     * Computes the distance from the specified vertex to all others in its
     * connected component in the specified graph.
     * @param g the graph
     * @param from the initial vertex
     * @return a distance table
     */
    public static OpenIntDoubleHashMap allShortestPaths(Graph g, int from) {
        final OpenIntDoubleHashMap dist = new OpenIntDoubleHashMap(g.getVerticesCount());
        BreadthFirst bf = new BreadthFirst(g);
        bf.visit(new BreadthFirst.Manager() {
                double dist_examined;
                
                public void examineVertex(int vertex) {
                    dist_examined = dist.get(vertex)+1;
                }
                public void discoverVertex(int vertex) {
                    dist.put(vertex, dist_examined);
                }
            },
            from);
        return dist;
    }
    
    /**
     * Computes the distance from the specified vertex to all others in its
     * connected component in the specified graph, using a Breadth First
     * Search.
     * 
     * <p>The specified <code>IntIntDoubleProcedure</code> will receive the
     * outgoing edge, the next vertex and the distance.  The first call will
     * contain  <code>Graph.NIL</code> for the edge, <code>from</code> for the vertex
     * and 0 for the distance.
     * 
     * @param g the graph
     * @param from the initial vertex
     * @param proc an object called with each outgoing edge, outgoing vertex
     * and distance and returning <code>false</code> to stop
     * @return the last value returned by proc
     */
    public static boolean allShortestPath(
            Graph g, 
            int from, 
            IntIntDoubleProcedure proc) {
        OpenIntIntHashMap dist = new OpenIntIntHashMap();
        RowIterator edgeIter = g.newEdgeIterator();
        IntLinkedList Q = new IntLinkedList();
        dist.put(from, 0);
        Q.addLast(from);
        if (! proc.apply(-1, from, 0))
            return false;
        
        while (!Q.isEmpty()) {
            int u = Q.getFirst();
            Q.removeFirst();
            int d = dist.get(u)+1;
            for (RowIterator iter = g.isDirected() 
                    ? g.outEdgeIterator(u, edgeIter) 
                    : g.edgeIterator(u, edgeIter);
                iter.hasNext();) {
                int e = iter.nextRow();
                int v = g.getOtherVertex(e, u);
                
                if (!dist.containsKey(v)) {
                    dist.put(v, d);
                    if (! proc.apply(e, v, d))
                        return false;
                    Q.addLast(v);
                }
            }
        }
        return true;
    }
    
    /**
     * Computes the distance from the specified vertex to all others in its
     * connected component in the specified graph.
     * @param g the graph
     * @param from the initial vertex
     * @param edges a hash table associating each vertex with the edge to
     * the previous vertex
     * @return a distance table
     */
    public static OpenIntDoubleHashMap allShortestPath(
            Graph g, 
            int from, 
            final OpenIntIntHashMap edges) {
        return allShortestPath(g, from, edges, null);
    }
    
    /**
     * Computes the distance from the specified vertex to all others in its
     * connected component in the specified graph.
     * @param g the graph
     * @param ret the hash table to reuse
     * @param from the initial vertex
     * @param edges a hash table associating each vertex with the edge to
     * the previous vertex
     * @return a distance table
     */
    public static OpenIntDoubleHashMap allShortestPath(
            Graph g,
            int from, 
            final OpenIntIntHashMap edges,
            OpenIntDoubleHashMap ret) {
        if (ret == null) {
            ret = new OpenIntDoubleHashMap();
        }
        final OpenIntDoubleHashMap dist = ret;
        edges.clear();
        dist.put(from, 0);
        BreadthFirst bf = new BreadthFirst(g);
        bf.visit(new BreadthFirst.Manager() {
                double dist_examined;
                int edge;
                
                public void examineVertex(int vertex) {
                    assert(dist.containsKey(vertex));
                    dist_examined = dist.get(vertex)+1;
                }
                public void examineEdge(int edge) {
                    this.edge = edge;
                }
                public void discoverVertex(int vertex) {
                    dist.put(vertex, dist_examined);
                    edges.put(vertex, edge);
                }
            },
            from);
//        assert(checkShortestPath(g, from, dist, edges));
        return ret;
    }
    
//    private static boolean checkShortestPath(
//            final Graph g,
//            final int from, 
//            final OpenIntDoubleHashMap dist, 
//            final OpenIntIntHashMap edges) {
//        return dist.forEachKey(new IntProcedure() {
//            public boolean apply(int key) {
//                double d = dist.get(key);
//                
//                for (int p = key; 
//                    p != from; 
//                    p = g.getOtherVertex(edges.get(p), p)) { 
//                    d--;
//                }
//                if (d != 0) {
//                    return false;
//                }
//                return true;
//            }
//        });
//    }
    
    /**
     * Returns the weight of a Predecessor object.
     * @param o the Predecessor as an Object
     * @return the weight of the Predecessor object
     */
    public static double getWeight(Object o) {
        return ((Predecessor)o).getWeight();
    }
    
    /**
     * Returns the vertex of a Predecessor object.
     * @param o the Predecessor as an Object
     * @return the vertex of the Predecessor object
     */
    public static int getVertex(Object o) {
        return ((Predecessor)o).getVertex();
    }
    
    /**
     * Returns the predecessor edge of a Predecessor object.
     * @param o the Predecessor as an Object
     * @return the predecessor vertex of the Predecessor object
     */
    public static int getEdge(Object o) {
        return ((Predecessor)o).getEdge();
    }

    /**
     * <b>Predecessor</b> contain information about
     * a vertex in a path from a vertex.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.18 $
     */
    public static class Predecessor implements Comparable {
        protected int vertex;
        protected double weight;
        protected int edge;
        Predecessor(int edge, int vertex, double weight) {
            this.edge= edge;
            this.vertex = vertex;
            this.weight = weight;
        }
        
        Predecessor(int vertex, double weight) {
            this.edge = -1;
            this.vertex = vertex;
            this.weight = weight;
        }
        
        /**
         * {@inheritDoc}
         */
        public int compareTo(Object o) {
            double cmp = (weight - ((Predecessor)o).weight);
            if (cmp < 0) return -1;
            else if (cmp > 0) return 1;
            else return 0;
        }
        
        /**
         * @return the vertex
         */
        public int getVertex() {
            return vertex;
        }
        
        /**
         * @return the weight
         */
        public double getWeight() {
            return weight;
        }
        
        /**
         * @return the edge to the predecessor vertex
         */
        public int getEdge() {
            return edge;
        }
        
        /**
         * Returns the predecessor vertex in the graph
         * @param g the graph
         * @return the predecessor vertex
         */
        public int getPrev(Graph g) {
            return g.getOtherVertex(edge, vertex);
        }
    }
}
