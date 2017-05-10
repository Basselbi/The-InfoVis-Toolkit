/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.column.AbstractIntColumn;
import infovis.utils.AbstractRowIterator;
import infovis.utils.RowIterator;

/**
 * <b>DefaultEdgeIterator</b> is the default implementation
 * of edge iterators.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DefaultEdgeIterator extends AbstractRowIterator {
    protected Graph graph;
    protected int edge;
    protected AbstractIntColumn nextEdge;
    protected int edge2;
    protected AbstractIntColumn nextEdge2;
    
    /**
     * Creates a DefaultEdgeIterator from an edge and a nextEdge column.
     * @param graph the graph
     * @param firstEdge the first edge
     * @param nextEdge the next edge column
     * @param secondEdge the second first edge
     * @param secondNextEdge the second IntColumn containing the next edges
     */
    public DefaultEdgeIterator(
            Graph graph,
            int firstEdge, AbstractIntColumn nextEdge,
            int secondEdge, AbstractIntColumn secondNextEdge) {
        setState(graph, firstEdge, nextEdge, secondEdge, secondNextEdge);
    }
    /**
     * Creates a DefaultEdgeIterator from an edge and a nextEdge column.
     * @param graph the graph
     * @param firstEdge the first edge
     * @param nextEdge the next edge column
     */
    public DefaultEdgeIterator(Graph graph, int firstEdge, AbstractIntColumn nextEdge) {
        this(graph, firstEdge, nextEdge, -1, null);
    }
    
    /**
     * Default constructor.
     */
    public DefaultEdgeIterator() {
        this(null, -1, null);
    }
    
    /**
     * Sets the state of this iterator.
     * @param graph the graph
     * @param firstEdge the new first edge
     * @param nextEdge the IntColumn containing the next edges
     */
    public void setState(Graph graph, int firstEdge, AbstractIntColumn nextEdge) {
        setState(graph, firstEdge, nextEdge, -1, null);
    }
    
    /**
     * Sets the state of this iterator.
     * @param graph the graph
     * @param firstEdge the new first edge
     * @param nextEdge the IntColumn containing the next edges
     * @param secondEdge the second first edge
     * @param secondNextEdge the second IntColumn containing the next edges
     */
    public void setState(
            Graph graph,
            int firstEdge, AbstractIntColumn nextEdge, 
            int secondEdge, AbstractIntColumn secondNextEdge) {
        this.graph = graph;
        if (firstEdge == -1) {
            this.edge = secondEdge;
            this.nextEdge = secondNextEdge;
            this.edge2 = -1;
            this.nextEdge2 = null;
        }
        else {
            this.edge = firstEdge;
            this.nextEdge = nextEdge;
            this.edge2 = secondEdge;
            this.nextEdge2 = secondNextEdge;
        }
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator copy() {
        return new DefaultEdgeIterator(graph, edge, nextEdge, edge2, nextEdge2);
    }

    /**
     * {@inheritDoc}
     */
    public int nextRow() {
        int prev = edge;

        if (edge != -1) {
            edge = nextEdge.get(edge);
        }
        if (edge == -1) {
            edge = edge2;
            nextEdge = nextEdge2;
            edge2 = -1;
            nextEdge2 = null;
        }
        return prev;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object next() {
        return graph.getEdge(nextRow());
    }

    /**
     * {@inheritDoc}
     */
    public int peekRow() {
        return edge;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return edge != -1;
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        throw new java.lang.UnsupportedOperationException("Cannot remove edge from iterator");
    }

}
