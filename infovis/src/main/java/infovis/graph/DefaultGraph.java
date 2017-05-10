/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import infovis.DynamicTable;
import infovis.Graph;
import infovis.Table;
import infovis.column.AbstractIntColumn;
import infovis.column.IntColumn;
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.table.DefaultTable;
import infovis.utils.RowIterator;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.47 $
 */
public class DefaultGraph implements Graph, TableModelListener {
    /** Name of the column containing the index of the first outgoing edge. */
    public static final String    FIRSTEDGE_COLUMN     = "#FirstEdge";

    /** Name of the column containing the index of the last outgoing edge. */
    public static final String    LASTEDGE_COLUMN      = "#LastEdge";

    /** Name of the column containing the first vertex in the edge table. */
    public static final String    INVERTEX_COLUMN      = "#FirstVertex";

    /** Name of the column containing the second vertex in the edge table. */
    public static final String    OUTVERTEX_COLUMN     = "#SecondVertex";

    /** Name of the column containing the next ougoing edge in the edge table. */
    public static final String    NEXTEDGE_COLUMN      = "#NextEdge";

    /** Name of the column containing the previous outgoing edge in the edge table. */
    public static final String    PREVEDGE_COLUMN      = "#PrevEdge";

    // /** The Metadata key of the vertex table in the underlying table. */
    // public static final String VERTEX_TABLE_METADATA =
    // "VERTEX_TABLE_METADATA";
    /** The Metadata key of the Graph using the Table */
    public static final String    GRAPH_METADATA       = "GRAPH_METADATA";

    /** Name of the column containing the first incoming edge. */
    public static final String    FIRSTINEDGE_COLUMN = "#FirstInEdge";

    /** Name of the column containing last incoming edge. */
    public static final String    LASTINEDGE_COLUMN  = "#LastInEdge";

    /** Name of the column containing the next incoming edge in the edge table. */
    public static final String    NEXTINEDGE_COLUMN  = "#NextInEdge";

    /** Name of the column containing the previous incoming edge in the edge table. */
    public static final String    PREVINEDGE_COLUMN  = "#PrevInEdge";

    /** The Edge table */
    protected DynamicTable        edgeTable;
    protected MutableAttributeSet metadata;
    protected MutableAttributeSet clientPropery;
    protected String              name;

    /**
     * <b>IntColumnFactory</b> is used to allocate edge lists and vertex lists.
     * 
     * @author Jean-Daniel Fekete
     */
    public interface IntColumnFactory {
        /**
         * Factory method used to create a column to manage the edges.
         * @param name column name
         * @return an AbstractIntColumn
         */
        AbstractIntColumn createEdgeColumn(String name);
        /**
         * Factory method used to create a column to manage vertices.
         * @param name column name
         * @return an AbstractIntColumn
         */
        AbstractIntColumn createVertexColumn(String name);
    }
    
    /**
     * <b>DefaultIntColumnFactory</b> is the default implementation of
     * an <code>IntColumnFactory</code>.
     * 
     * @author Jean-Daniel Fekete
     */
    public static class DefaultIntColumnFactory implements IntColumnFactory {
        /**
         * Singleton of this class.
         */
        public static final DefaultIntColumnFactory INSTANCE = new DefaultIntColumnFactory();
        
        private DefaultIntColumnFactory() {}
        /**
         * {@inheritDoc}
         */
        public AbstractIntColumn createEdgeColumn(String name) {
            return new IntColumn(name);
        }
        
        /**
         * {@inheritDoc}
         */
        public AbstractIntColumn createVertexColumn(String name) {
            return new IntColumn(name);
        }
    }
    
    protected IntColumnFactory factory;
    
    /** The vertex table */
    protected DynamicTable        vertexTable;

    /** The first outgoing edge of each vertex. */
    protected AbstractIntColumn   vertexFirstEdge;

    /** The last outgoing edge of each vertex. */
    protected AbstractIntColumn   vertexLastEdge;

    /** The first incoming edge of each vertex. */
    protected AbstractIntColumn   vertexFirstInEdge;

    /** The last incoming edge of each vertex. */
    protected AbstractIntColumn           vertexLastInEdge;

    /** The first vertex of each edge. */
    protected AbstractIntColumn           edgeFirstVertex;

    /** The second vertex of each edge. */
    protected AbstractIntColumn           edgeSecondVertex;

    /** The next outgoing edge in the list linked from vertexFirstEdge(v) to vertexLastEdge(v) */
    protected AbstractIntColumn           nextEdge;

    /** The previous outgoing edge in the list linked from vertexLastEdge(v) to vertexFirstEdge(v) */
    protected AbstractIntColumn           prevEdge;

    /** The next incoming edge in the list linked from vertexFirstInEdge(v) to vertexLastInEdge(v) */
    protected AbstractIntColumn           nextInEdge;

    /** The previous incoming edge in the list linked from vertexLastInEdge(v) to vertexFirstInEdge(v) */ 
    protected AbstractIntColumn           prevInEdge;

    protected EventListenerList   listeners;

    protected boolean             directed             = true;
    protected volatile GraphChangedEvent tmpEvent = new GraphChangedEvent(this);
    
    /**
     * Contructor for DefaultGraph.
     */
    public DefaultGraph() {
        this(true);
    }

    /**
     * Constructor as directed or undirected.
     * @param directed directedness
     */
    public DefaultGraph(boolean directed) {
        this(directed, false, null);
    }
    
    /**
     * Constructor as directed or undirected with a specified factory.
     * @param directed directedness
     * @param factory the allocation factory
     */
    public DefaultGraph(boolean directed, IntColumnFactory factory) {
        this(directed, false, factory);
    }
    
    /**
     * Constructor as directed or undirected.
     * @param directed directedness
     * @param fastRemoval constant time vs. linear time removal
     * @param factory the IntColumnFactory used for this graph
     */
    public DefaultGraph(boolean directed, boolean fastRemoval, IntColumnFactory factory) {
        this(directed, 
                fastRemoval, 
                new DefaultEdgeTable(), 
                new DefaultVertexTable(),
                factory == null ? DefaultIntColumnFactory.INSTANCE : factory);
    }
    
    protected DefaultGraph(
            boolean directed, 
            boolean fastRemoval,
            DynamicTable edgeTable, 
            DynamicTable vertexTable,
            IntColumnFactory factory) {
        this.directed = directed;
        this.edgeTable = edgeTable;
        this.vertexTable = vertexTable;
        this.factory = factory;
        initializeEdgeTable(edgeTable, fastRemoval);
        initializeVertexTable(vertexTable);
    }

    /**
     * Returns a graph associated with a specified table such as a
     * vertex table or an edge table.
     * @param table the table
     * @return the associated graph or null
     */
    public static Graph getGraph(Table table) {
        return (Graph) table.getMetadata().getAttribute(GRAPH_METADATA);
    }
    
    /**
     * @return the factory
     */
    public IntColumnFactory getFactory() {
        return factory;
    }

    protected void initializeEdgeTable(DynamicTable edgeTable, boolean fastRemoval) {
        edgeFirstVertex = factory.createEdgeColumn(INVERTEX_COLUMN);
        edgeSecondVertex = factory.createEdgeColumn(OUTVERTEX_COLUMN);
        edgeTable.addColumn(edgeFirstVertex);
        edgeTable.addColumn(edgeSecondVertex);

        nextEdge = factory.createEdgeColumn(NEXTEDGE_COLUMN);
        nextInEdge = factory.createEdgeColumn(NEXTINEDGE_COLUMN);
        edgeTable.addColumn(nextEdge);
        edgeTable.addColumn(nextInEdge);
        if (fastRemoval) {
            prevEdge = factory.createEdgeColumn(PREVEDGE_COLUMN);
            prevInEdge = factory.createEdgeColumn(PREVINEDGE_COLUMN);
            edgeTable.addColumn(prevEdge);
            edgeTable.addColumn(prevInEdge);
        }
        edgeTable.getMetadata().addAttribute(GRAPH_METADATA, this);
        edgeTable.addTableModelListener(this);
    }

    protected void initializeVertexTable(DynamicTable vertexTable) {
        vertexFirstEdge = factory.createVertexColumn(FIRSTEDGE_COLUMN);
        vertexLastEdge = factory.createVertexColumn(LASTEDGE_COLUMN);
        vertexTable.addColumn(vertexFirstEdge);
        vertexTable.addColumn(vertexLastEdge);
        vertexFirstInEdge = factory.createVertexColumn(FIRSTINEDGE_COLUMN);
        vertexLastInEdge = factory.createVertexColumn(LASTINEDGE_COLUMN);
        vertexTable.addColumn(vertexFirstInEdge);
        vertexTable.addColumn(vertexLastInEdge);
      
        vertexTable.getMetadata().addAttribute(GRAPH_METADATA, this);
        vertexTable.addTableModelListener(this);
    }

    // interface Metadata
    /**
     * Returns the clientPropery.
     * 
     * @return Map the clientPropery map.
     */
    public MutableAttributeSet getClientProperty() {
        if (clientPropery == null) {
            clientPropery = new SimpleAttributeSet();
        }
        return clientPropery;
    }

    /**
     * Returns the metadata.
     * 
     * @return Map the metadata map.
     */
    public MutableAttributeSet getMetadata() {
        if (metadata == null) {
            metadata = new SimpleAttributeSet();
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        this.name = name;
        getEdgeTable().setName(name);
        getVertexTable().setName(name);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        getEdgeTable().clear();
        getEdgeTable().getMetadata().addAttribute(GRAPH_METADATA, this);
        getVertexTable().clear();
        getVertexTable().getMetadata().addAttribute(GRAPH_METADATA, this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * {@inheritDoc}
     */
    public void setDirected(boolean directed) {
        if (this.directed == directed) return;
        if (! nextEdge.isEmpty()) {
            throw new UnsupportedOperationException("Cannot change directedness of a non-empty graph");
        }
        this.directed = directed;
    }

    /**
     * Returns the number of vertices in the graph
     * 
     * @return The number of vertices in the graph.
     */
    public int getVerticesCount() {
        return vertexTable.getRowCount();
    }

    /**
     * Adds a new vertex to the graph.
     * 
     * @return the vertex number.
     */
    public int addVertex() {
        int v = addVertexInternal();
        return v;
    }
    
    protected int addVertexInternal() {
        return vertexTable.addRow(); // will trigger a notification
    }

    protected void vertexInserted(int v) {
        try {
            disableNotify();
            vertexFirstEdge.setExtend(v, Graph.NIL);
            vertexLastEdge.setExtend(v, Graph.NIL);
            vertexFirstInEdge.setExtend(v, Graph.NIL);
            vertexLastInEdge.setExtend(v, Graph.NIL);
        } finally {
            enableNotify();
        }
        fireGraphChangedListeners(v, GraphChangedEvent.GRAPH_VERTEX_ADDED);
    }

    protected void verticesInserted(int v0, int v1) {
        while (v0 <= v1) {
            vertexInserted(v0);
            v0++;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeVertex(int vertex) {
        checkVertex(vertex);
        vertexTable.removeRow(vertex); // triggers notification
    }

    protected void vertexRemoved(int vertex) {
        assert (!vertexTable.isRowValid(vertex));
        try {
            disableNotify(); // prevent notification before the graph is
            // coherent
            for (int e = getFirstEdge(vertex); e != NIL; e = getFirstEdge(vertex)) {
                removeEdge(e);
            }
            for (int e = getFirstOutEdge(vertex); e != NIL; e = getFirstOutEdge(vertex)) {
                removeEdge(e);
            }
            vertexFirstEdge.setValueUndefined(vertex, true);
            vertexLastEdge.setValueUndefined(vertex, true);
            vertexFirstInEdge.setValueUndefined(vertex, true);
            vertexLastInEdge.setValueUndefined(vertex, true);
        } finally {
            enableNotify();
        }
        fireGraphChangedListeners(
                vertex,
                GraphChangedEvent.GRAPH_VERTEX_REMOVED);
    }

    protected void verticesRemoved(int v0, int v1) {
        while (v0 <= v1) {
            vertexRemoved(v0);
            v0++;
        }
    }

    protected void verticesChanged(int v0, int v1) {
        while (v0 <= v1) {
            boolean undefined = vertexFirstEdge.isValueUndefined(v0);
            if (undefined == vertexTable.isRowValid(v0)) {
                if (undefined) {
                    vertexInserted(v0);
                }
                else {
                    vertexRemoved(v0);
                }
            }
            v0++;
        }
    }

    /**
     * Returns the number of edges in the graph.
     * 
     * @return the number of edges in the graph.
     */
    public int getEdgesCount() {
        return getEdgeTable().getRowCount();
    }

    /**
     * Adds a new edge between two vertices.
     * 
     * @param v1
     *            the first vertex.
     * @param v2
     *            the second vertex.
     * 
     * @return the new edge index.
     */
    public int addEdge(int v1, int v2) {
        int edge = addEdgeImpl(v1, v2);
        fireGraphChangedListeners(edge, GraphChangedEvent.GRAPH_EDGE_ADDED);
        return edge;
    }
    
    
    protected int addEdgeImpl(int v1, int v2) {
        checkVertex(v1);
        checkVertex(v2);
        int edge;
        if (! isDirected()) {
            if (v1 > v2) {
                int tmp = v1;
                v1 = v2;
                v2 = tmp;
            }
        } 
        try {
            disableNotify(); // prevents notification before the graph is
            // coherent
            edge = getEdgeTable().addRow();
            setFirstVertex(edge, v1);
            setSecondVertex(edge, v2);

            addEdgeIn(
                    edge,
                    v1,
                    vertexFirstEdge,
                    vertexLastEdge,
                    nextEdge,
                    prevEdge);
            addEdgeIn(
                    edge,
                    v2,
                    vertexFirstInEdge,
                    vertexLastInEdge,
                    nextInEdge,
                    prevInEdge);

        } finally {
            enableNotify();
        }
        assert(checkInvariant(v1)==null);
        assert(checkInvariant(v2)==null);
        return edge;
    }

    protected void addEdgeIn(
            int edge,
            int v,
            AbstractIntColumn first,
            AbstractIntColumn last,
            AbstractIntColumn next,
            AbstractIntColumn prev) {
        assert (!first.isValueUndefined(v));
        assert (!last.isValueUndefined(v));
        int p = last.get(v);
        last.setExtend(v, edge);
        if (p == NIL) {
            first.set(v, edge);
        }
        else {
            next.setExtend(p, edge);
        }
        next.setExtend(edge, NIL);
        if (prev != null)
            prev.setExtend(edge, p);
    }

    /**
     * Check graph invariants for the specified vertex
     * @param vertex the vertex
     * @return and error code or null
     */
    public String checkInvariant(int vertex) {
    	if( !vertexTable.isRowValid(vertex) )
    		return "vertex " + vertex + " invalid";
        for (RowIterator iter = outEdgeIterator(vertex); iter.hasNext(); ) {
            int edge = iter.nextRow();
            int v = getOtherVertex(edge, vertex);
            if (v == -1) {
                return "Pb. finding the other vertex of edge "+edge+" vertex "+vertex;
            }
            if (getOtherVertex(edge, v)!=vertex) {
                return "Pb. retrieving my own vertex for edge "+edge+" vertex "+vertex+" other vertex "+v;
            }
        }
        for (RowIterator iter = inEdgeIterator(vertex); iter.hasNext(); ) {
            int edge = iter.nextRow();
            int v = getOtherVertex(edge, vertex);
            if (v == -1) {
                return "Pb. finding the other vertex of edge "+edge+" vertex "+vertex;
            }
            if (getOtherVertex(edge, v)!=vertex) {
                return "Pb. retrieving my own vertex for edge "+edge+" vertex "+vertex+" other vertex "+v;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void removeEdge(int edge) {
        getEdgeTable().removeRow(edge); // triggers notification
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeEdge(Edge e) {
        removeEdge(e.getId());
    }

    protected void edgeRemoved(int edge) {
        quickRemoveEdge(edge);
    }

    protected void edgeInserted(int edge) {

    }

    protected void edgesRemoved(int edge0, int edge1) {
        try {
            disableNotify();
            while (edge0 <= edge1) {
                edgeRemoved(edge0);
                edge0++;
            }
        } finally {
            enableNotify();
        }
    }

    protected void quickRemoveEdge(int edge) {
        int v1 = getFirstVertex(edge);
        int v2 = getSecondVertex(edge);
        removeEdgeIn(
                edge,
                v1,
                vertexFirstEdge,
                vertexLastEdge,
                nextEdge,
                prevEdge);
        removeEdgeIn(
                edge,
                v2,
                vertexFirstInEdge,
                vertexLastInEdge,
                nextInEdge,
                prevInEdge);
        edgeFirstVertex.setExtend(edge, NIL);
        edgeFirstVertex.setValueUndefined(edge, true);
        edgeSecondVertex.setExtend(edge, NIL);
        edgeSecondVertex.setValueUndefined(edge, true);

//        String s = null;
//        assert((s=checkInvariant(v1))==null) : "Error "+s;
//        assert((s=checkInvariant(v2))==null) : "Error "+s;
        fireGraphChangedListeners(edge, GraphChangedEvent.GRAPH_EDGE_REMOVED);
    }

    protected void removeEdgeIn(
            int edge,
            int v,
            AbstractIntColumn first,
            AbstractIntColumn last,
            AbstractIntColumn next,
            AbstractIntColumn prev) {
        int n = next.get(edge);
        int p;
        if (prev != null) {
            p = prev.get(edge);
        }
        else {
//          chase previous
            p = NIL;
            for (int e = first.get(v); 
                e != NIL; 
                e = next.get(e)) {
                if (e == edge)
                    break;
                p = e;
            }
        }
        next.set(edge, NIL);
        next.setValueUndefined(edge, true);
        if (prev != null) {
            prev.set(edge, NIL);
            prev.setValueUndefined(edge, true);
        }
        if (n == NIL) { // last edge
            last.set(v, p);
        }
        else if (prev != null){
            prev.set(n, p);
        }
        if (p == NIL) { // first edge
            first.set(v, n);
        }
        else {
            next.set(p, n);
        }
    }

    /**
     * Returns the "in" vertex of an edge.
     * 
     * @param edge
     *            the edge.
     * 
     * @return the "in" vertex of an edge or NIL.
     */
    public int getFirstVertex(int edge) {
        return edgeFirstVertex.get(edge);
    }

    protected void setFirstVertex(int edge, int v) {
        edgeFirstVertex.setExtend(edge, v);
    }

    /**
     * Returns the "out" vertex of an edge.
     * 
     * @param edge
     *            the edge.
     * 
     * @return the "out" vertex of an edge.
     */
    public int getSecondVertex(int edge) {
        return edgeSecondVertex.get(edge);
    }

    protected void setSecondVertex(int edge, int v) {
        edgeSecondVertex.setExtend(edge, v);
    }

    /**
     * {@inheritDoc}
     */
    public int getOutEdgeAt(int vertex, int index) {
        int e;

        for (e = getFirstEdge(vertex); 
             e != NIL && index != 0; 
             e = getNextEdge(e))
            ;

        return e;
    }

    /**
     * {@inheritDoc}
     */
    public int getInEdgeAt(int vertex, int index) {
        int e;

        for (e = vertexFirstInEdge.get(vertex); 
                e != NIL && index != 0; 
                e = nextInEdge.get(e))
            ;

        return e;
    }

    /**
     * {@inheritDoc}
     */
    public int getOtherVertex(int edge, int vertex) {
        int in = getFirstVertex(edge);
        int out = getSecondVertex(edge);
        if (in == vertex) {
            return out;
        }
        else if (out == vertex) {
            return in;
        }
        else {
            return NIL;
        }
    }

    /**
     * Returns the first edge of a specified vertex.
     * 
     * @param vertex
     *            the vertex,
     * 
     * @return the first edge of the specified vertex or NIL if none exists.
     */
    protected int getFirstEdge(int vertex) {
        return vertexFirstEdge.get(vertex);
    }

    /**
     * Returns the last edge of a specified vertex.
     * 
     * @param vertex
     *            the vertex,
     * 
     * @return the last edge of the specified vertex or NIL if none exists.
     */
    protected int getLastEdge(int vertex) {
        return vertexLastEdge.get(vertex);
    }

    /**
     * Returns the edge following a specified edge starting at a vertex.
     * 
     * @param edge
     *            the edge.
     * 
     * @return the edge following a given edge starting at the vertex or NIL if
     *         the specified edge is the last of the "in" vertex.
     */
    protected int getNextEdge(int edge) {
        return nextEdge.get(edge);
    }

    /**
     * Returns an edge between two specified vertices.
     * 
     * @param v1
     *            the first vertex.
     * @param v2
     *            the second vertex.
     * 
     * @return an edge between two specified vertices or NIL if none exists.
     */
    public int getEdge(int v1, int v2) {
        if (!(vertexTable.isRowValid(v1) && vertexTable.isRowValid(v2))) {
            return Graph.NIL;
        }
        if (!isDirected()) {
            if (v1 > v2) {
                int tmp = v1;
                v1 = v2;
                v2 = tmp;
            }
        }
        for (int e = getFirstEdge(v1); e != Graph.NIL; e = getNextEdge(e)) {
            if (getSecondVertex(e) == v2) {
                return e;
            }
        }
//        if (!isDirected()) {
//            for (int e = getFirstEdge(v2); e != Graph.NIL; e = getNextEdge(e)) {
//                if (getSecondVertex(e) == v1) {
//                    return e;
//                }
//            }
//        }
        return NIL;
    }

    /**
     * Returns an edge between two specified vertices.
     * 
     * @param v1
     *            the first vertex.
     * @param v2
     *            the second vertex.
     * 
     * @return an edge between two specified vertices creating one if none
     *         exists.
     */
    public int findEdge(int v1, int v2) {
        if (!(vertexTable.isRowValid(v1) && vertexTable.isRowValid(v2))) {
            return Graph.NIL;
        }
        int e = getEdge(v1, v2);
        if (e == NIL)
            return addEdge(v1, v2);
        return e;
    }

    protected int getFirstOutEdge(int vertex) {
        return vertexFirstInEdge.get(vertex);
    }

    protected int getLastOutEdge(int vertex) {
        return vertexLastInEdge.get(vertex);
    }

    protected int getNextOutEdge(int edge) {
        return nextInEdge.get(edge);
    }

    /**
     * Returns the out degree of the vertex, which is simply the number of edges
     * starting from the vertex.
     * 
     * @param vertex
     *            the vertex.
     * @return The out degree of the vertex.
     */
    public int getOutDegree(int vertex) {
        int cnt = 0;
        for (int edge = vertexFirstEdge.get(vertex); 
                edge != -1; 
                edge = nextEdge.get(edge)) {
            cnt++;
        }
        return cnt;
    }

    /**
     * {@inheritDoc}
     */
    public int getInDegree(int vertex) {
        int cnt = 0;
        for (int edge = vertexFirstInEdge.get(vertex); edge != -1; edge = nextInEdge
                .get(edge)) {
            cnt++;
        }
        return cnt;
    }

    /**
     * {@inheritDoc}
     */
    public int getDegree(int vertex) {
        int in = getInDegree(vertex);
        int out = getOutDegree(vertex);
        return in + out;
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator newEdgeIterator() {
        return new DefaultEdgeIterator();
    }
    
    protected DefaultEdgeIterator getEdgeIterator(
            RowIterator iter, int edge, AbstractIntColumn col) {
        if (iter != null && (iter instanceof DefaultEdgeIterator)) {
            DefaultEdgeIterator i = (DefaultEdgeIterator)iter;
            i.setState(this, edge, col);
            return i;
        }
        return new DefaultEdgeIterator(this, edge, col);
        
    }

    protected DefaultEdgeIterator getEdgeIterator(
            RowIterator iter, 
            int edge1, AbstractIntColumn col1,
            int edge2, AbstractIntColumn col2) {
        if (iter != null && (iter instanceof DefaultEdgeIterator)) {
            DefaultEdgeIterator i = (DefaultEdgeIterator)iter;
            i.setState(this, edge1, col1, edge2, col2);
            return i;
        }
        return new DefaultEdgeIterator(this, edge1, col1, edge2, col2);
        
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(int vertex, RowIterator iter) {
        return getEdgeIterator(iter, 
                vertexFirstEdge.get(vertex), nextEdge,
                vertexFirstInEdge.get(vertex), nextInEdge);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(int vertex) {
        return edgeIterator(vertex, null);
    }

    /**
     * Returns an iterator over the edges of a specified vertex.
     * 
     * @param vertex
     *            the vertex.
     * 
     * @return the iterator over the edges of the vertex.
     */
    public RowIterator outEdgeIterator(int vertex) {
        return outEdgeIterator(vertex, null);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator outEdgeIterator(int vertex, RowIterator iter) {
        return getEdgeIterator(
                iter, 
                vertexFirstEdge.get(vertex), 
                nextEdge);
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator inEdgeIterator(int vertex, RowIterator iter) {
        return getEdgeIterator(iter,
                vertexFirstInEdge.get(vertex), 
                nextInEdge);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator inEdgeIterator(int vertex) {
        return inEdgeIterator(vertex, null);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator vertexIterator() {
        return vertexTable.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator() {
        return getEdgeTable().iterator();
    }

    /**
     * Returns the edgeTable.
     * 
     * @return DefaultTable
     */
    public DynamicTable getEdgeTable() {
        return edgeTable;
    }

    /**
     * {@inheritDoc}
     */
    public DynamicTable getVertexTable() {
        return vertexTable;
    }

    /**
     * {@inheritDoc}
     */
    public void addGraphChangedListener(GraphChangedListener l) {
        getListeners().add(GraphChangedListener.class, l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeGraphChangedListener(GraphChangedListener l) {
        getListeners().remove(GraphChangedListener.class, l);
    }

    protected void fireGraphChangedListeners(GraphChangedEvent e) {
        if (listeners == null)
            return;
        Object[] ll = listeners.getListeners(GraphChangedListener.class);
        for (int i = 0; i < ll.length; i++) {
            GraphChangedListener l = (GraphChangedListener) ll[i];
            l.graphChanged(e);
        }
    }

    protected void fireGraphChangedListeners(int detail, short type) {
        if (listeners == null)
            return;
        tmpEvent.setValues(detail, type);
        fireGraphChangedListeners(tmpEvent);
    }

    protected EventListenerList getListeners() {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        return listeners;
    }

    protected void checkVertex(int v) {
        if (!vertexTable.isRowValid(v)) {
            throw new GraphException("invalid vertex", v, NIL);
        }
    }

    protected void checkEdge(int e) {
        if (!getEdgeTable().isRowValid(e)) {
            throw new GraphException("invalid edge " + e, NIL, e);
        }
    }

    protected void disableNotify() {
        vertexTable.disableNotify();
        edgeTable.disableNotify();
    }

    protected void enableNotify() {
        edgeTable.enableNotify();
        vertexTable.enableNotify();
    }

    /**
     * {@inheritDoc}
     */
    public void tableChanged(TableModelEvent e) {
        if (e.getFirstRow() == TableModelEvent.HEADER_ROW)
            return;
        if (e.getSource() == getVertexTable()
                && e.getColumn() == TableModelEvent.ALL_COLUMNS) {
            switch (e.getType()) {
            case TableModelEvent.INSERT:
                verticesInserted(e.getFirstRow(), e.getLastRow());
                break;
            case TableModelEvent.DELETE:
                verticesRemoved(e.getFirstRow(), e.getLastRow());
                break;
            case TableModelEvent.UPDATE:
                verticesChanged(e.getFirstRow(), e.getLastRow());
                break;
            }
        }
        else if (e.getSource() == getEdgeTable()
                && e.getColumn() == TableModelEvent.ALL_COLUMNS) {
            switch (e.getType()) {
            case TableModelEvent.DELETE:
                edgesRemoved(e.getFirstRow(), e.getLastRow());
                break;
            // case TableModelEvent.INSERT:
            // edgesInserted(e.getFirstRow(), e.getLastRow());
            // break;
            // case TableModelEvent.UPDATE:
            // edgesChanged(e.getFirstRow(), e.getLastRow());
            // break;

            }
        }
    }
    
    /**
     * Creates an item column to optimize the
     * speed of Vertex/Edge-based manipulation
     */
    public void createItemColumns() {
        if (vertexTable instanceof DefaultTable) {
            DefaultTable dt = (DefaultTable) vertexTable;
            dt.createItemColumn();
        }
        
        if (edgeTable instanceof DefaultTable) {
            DefaultTable dt = (DefaultTable) edgeTable;
            dt.createItemColumn();
        }
    }
    
    /**
     * Removes the item column.
     */
    public void removeItemColumn() {
        if (vertexTable instanceof DefaultTable) {
            DefaultTable dt = (DefaultTable) vertexTable;
            dt.removeItemColumn();
        }
        
        if (edgeTable instanceof DefaultTable) {
            DefaultTable dt = (DefaultTable) edgeTable;
            dt.removeItemColumn();
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public Vertex add() {
        return getVertex(addVertex());
    }

    /**
     * {@inheritDoc}
     */
    public Vertex getVertex(int v) {
        return (Vertex)getVertexTable().getItem(v);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeVertex(Vertex vertex) {
        removeVertex(vertex.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getEdge(int e) {
        return (Edge)getEdgeTable().getItem(e);
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge addEdge(Vertex v1, Vertex v2) {
        return getEdge(addEdge(v1.getId(), v2.getId()));
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getFirstVertex(Edge e) {
        return getVertex(getFirstVertex(e.getId()));
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getOtherVertex(Edge edge, Vertex vertex) {
        return getVertex(getOtherVertex(edge.getId(), vertex.getId()));
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getSecondVertex(Edge edge) {
        return getVertex(getSecondVertex(edge.getId()));
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getOutEdgeAt(Vertex vertex, int index) {
        return getEdge(getOutEdgeAt(vertex.getId(), index));
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getInEdgeAt(Vertex vertex, int index) {
        return getEdge(getInEdgeAt(vertex.getId(), index));
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getEdge(Vertex v1, Vertex v2) {
        return getEdge(getEdge(v1.getId(), v2.getId()));
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge findEdge(Vertex v1, Vertex v2) {
        return getEdge(findEdge(v1.getId(), v2.getId()));
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOutDegree(Vertex vertex) {
        return getOutDegree(vertex.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator outEdgeIterator(Vertex vertex) {
        return outEdgeIterator(vertex.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public int getInDegree(Vertex vertex) {
        return getInDegree(vertex.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator inEdgeIterator(Vertex vertex) {
        return inEdgeIterator(vertex.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(Vertex vertex) {
        return edgeIterator(vertex.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public int getDegree(Vertex vertex) {
        return getDegree(vertex.getId());
    }
    
    
}