/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.Table;
import infovis.table.DefaultItem;

/**
 * <b>DefaultEdge</b> is the default implementation of an Edge.
 * 
 * @author Jean-Daniel Fekete
 */
public class DefaultEdge extends DefaultItem implements Edge {
    /**
     * Creates a DefaultEdge from an edge and an Edge Table it belongs to.
     * @param e the edge
     * @param table the table
     */
    public DefaultEdge(int e, Table table) {
        super(e, table);
    }
    
    /**
     * {@inheritDoc}
     */
    public Graph getGraph() {
        return DefaultGraph.getGraph(getTable());
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getFirstVertex() {
        return getGraph().getFirstVertex(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getSecondVertex() {
        return getGraph().getSecondVertex(this);
    }
}

