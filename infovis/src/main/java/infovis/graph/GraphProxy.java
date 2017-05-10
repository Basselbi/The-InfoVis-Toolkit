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
import infovis.graph.event.GraphChangedListener;
import infovis.utils.RowIterator;

import javax.swing.text.MutableAttributeSet;

/**
 * Class GraphProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 */
public class GraphProxy implements Graph {
    protected Graph graph;
    
    /**
     * Constructor for GraphProxy.
     * @param graph the graph
     */
    public GraphProxy(Graph graph) {
        setGraph(graph);
    }
    
    /**
     * Sets the graph
     * @param graph the graph
     */
    protected void setGraph(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    public Vertex add() {
        return graph.add();
    }

    /**
     * {@inheritDoc}
     */
    public int addEdge(int v1, int v2) {
        return graph.addEdge(v1, v2);
    }

    /**
     * {@inheritDoc}
     */
    public Edge addEdge(Vertex v1, Vertex v2) {
        return graph.addEdge(v1, v2);
    }

    /**
     * {@inheritDoc}
     */
    public void addGraphChangedListener(GraphChangedListener l) {
        graph.addGraphChangedListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public int addVertex() {
        return graph.addVertex();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        graph.clear();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator() {
        return graph.edgeIterator();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(int vertex) {
        return graph.edgeIterator(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(Vertex vertex) {
        return graph.edgeIterator(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int findEdge(int v1, int v2) {
        return graph.findEdge(v1, v2);
    }

    /**
     * {@inheritDoc}
     */
    public Edge findEdge(Vertex v1, Vertex v2) {
        return graph.findEdge(v1, v2);
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getClientProperty() {
        return graph.getClientProperty();
    }

    /**
     * {@inheritDoc}
     */
    public int getDegree(int vertex) {
        return graph.getDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getDegree(Vertex vertex) {
        return graph.getDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getEdge(int v1, int v2) {
        return graph.getEdge(v1, v2);
    }

    /**
     * {@inheritDoc}
     */
    public Edge getEdge(int e) {
        return graph.getEdge(e);
    }

    /**
     * {@inheritDoc}
     */
    public Edge getEdge(Vertex v1, Vertex v2) {
        return graph.getEdge(v1, v2);
    }

    /**
     * {@inheritDoc}
     */
    public int getEdgesCount() {
        return graph.getEdgesCount();
    }

    /**
     * {@inheritDoc}
     */
    public DynamicTable getEdgeTable() {
        return graph.getEdgeTable();
    }

    /**
     * {@inheritDoc}
     */
    public Vertex getFirstVertex(Edge e) {
        return graph.getFirstVertex(e);
    }

    /**
     * {@inheritDoc}
     */
    public int getFirstVertex(int edge) {
        return graph.getFirstVertex(edge);
    }

    /**
     * {@inheritDoc}
     */
    public int getInDegree(int vertex) {
        return graph.getInDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getInDegree(Vertex vertex) {
        return graph.getInDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getInEdgeAt(int vertex, int index) {
        return graph.getInEdgeAt(vertex, index);
    }

    /**
     * {@inheritDoc}
     */
    public Edge getInEdgeAt(Vertex vertex, int index) {
        return graph.getInEdgeAt(vertex, index);
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getMetadata() {
        return graph.getMetadata();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return graph.getName();
    }

    /**
     * {@inheritDoc}
     */
    public Vertex getOtherVertex(Edge edge, Vertex vertex) {
        return graph.getOtherVertex(edge, vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getOtherVertex(int edge, int vertex) {
        return graph.getOtherVertex(edge, vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getOutDegree(int vertex) {
        return graph.getOutDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getOutDegree(Vertex vertex) {
        return graph.getOutDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public int getOutEdgeAt(int vertex, int index) {
        return graph.getOutEdgeAt(vertex, index);
    }

    /**
     * {@inheritDoc}
     */
    public Edge getOutEdgeAt(Vertex vertex, int index) {
        return graph.getOutEdgeAt(vertex, index);
    }

    /**
     * {@inheritDoc}
     */
    public Vertex getSecondVertex(Edge edge) {
        return graph.getSecondVertex(edge);
    }

    /**
     * {@inheritDoc}
     */
    public int getSecondVertex(int edge) {
        return graph.getSecondVertex(edge);
    }

    /**
     * {@inheritDoc}
     */
    public Vertex getVertex(int v) {
        return graph.getVertex(v);
    }

    /**
     * {@inheritDoc}
     */
    public DynamicTable getVertexTable() {
        return graph.getVertexTable();
    }

    /**
     * {@inheritDoc}
     */
    public int getVerticesCount() {
        return graph.getVerticesCount();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator inEdgeIterator(int vertex) {
        return graph.inEdgeIterator(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator inEdgeIterator(Vertex vertex) {
        return graph.inEdgeIterator(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirected() {
        return graph.isDirected();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator outEdgeIterator(int vertex) {
        return graph.outEdgeIterator(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator outEdgeIterator(Vertex vertex) {
        return graph.outEdgeIterator(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEdge(Edge e) {
        graph.removeEdge(e);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEdge(int edge) {
        graph.removeEdge(edge);
    }

    /**
     * {@inheritDoc}
     */
    public void removeGraphChangedListener(GraphChangedListener l) {
        graph.removeGraphChangedListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVertex(int vertex) {
        graph.removeVertex(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVertex(Vertex vertex) {
        graph.removeVertex(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public void setDirected(boolean directed) {
        graph.setDirected(directed);
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        graph.setName(name);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator vertexIterator() {
        return graph.vertexIterator();
    }

    /**
     * @param vertex
     * @param it
     * @return
     * @see infovis.Graph#inEdgeIterator(int, infovis.utils.RowIterator)
     */
    public RowIterator inEdgeIterator(int vertex, RowIterator it) {
        return graph.inEdgeIterator(vertex, it);
    }

    /**
     * @return
     * @see infovis.Graph#newEdgeIterator()
     */
    public RowIterator newEdgeIterator() {
        return graph.newEdgeIterator();
    }

    /**
     * @param vertex
     * @param it
     * @return
     * @see infovis.Graph#outEdgeIterator(int, infovis.utils.RowIterator)
     */
    public RowIterator outEdgeIterator(int vertex, RowIterator it) {
        return graph.outEdgeIterator(vertex, it);
    }

    /**
     * @param vertex
     * @param it
     * @return
     * @see infovis.Graph#edgeIterator(int, infovis.utils.RowIterator)
     */
    public RowIterator edgeIterator(int vertex, RowIterator it) {
        return graph.edgeIterator(vertex, it);
    }
}
