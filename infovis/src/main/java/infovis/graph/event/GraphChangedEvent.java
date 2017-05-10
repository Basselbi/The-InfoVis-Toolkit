/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.event;

import infovis.Graph;

/**
 * <b>GraphChangedEvent</b> is the event passed to the
 * GraphChangeListener when a graph is changed.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class GraphChangedEvent {
    protected Graph           graph;
    protected int             detail;
    protected short           type;

    /** A vertex has been added. */
    public static final short GRAPH_VERTEX_ADDED   = 0;
    /** A vertex has been removed. */
    public static final short GRAPH_VERTEX_REMOVED = 1;
    /** An edge has been added. */
    public static final short GRAPH_EDGE_ADDED     = 2;
    /** An edge has been removed. */
    public static final short GRAPH_EDGE_REMOVED   = 3;
    /** Too many changes to describe in details. */
    public static final short GRAPH_CHANGED        = 4;

    /**
     * Creates an event with no meaningful detail or type.
     * @param graph the graph
     */
    public GraphChangedEvent(Graph graph) {
        this.graph = graph;
        this.detail = -1;
    }
    /**
     * Creates an event for a specified graph with a detail and a type.
     * @param graph the graph
     * @param detail the added or removed item
     * @param type the type of the event
     */
    public GraphChangedEvent(Graph graph, int detail, short type) {
        this.graph = graph;
        this.detail = detail;
        this.type = type;
    }
    
    /**
     * Changes the event.
     * @param detail the new detail
     * @param type the new type
     */
    public void setValues(int detail, short type) {
        this.detail = detail;
        this.type = type;
    }

    /**
     * @return the detail
     */
    public int getDetail() {
        return detail;
    }

    /**
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * @return the type
     */
    public short getType() {
        return type;
    }

}
