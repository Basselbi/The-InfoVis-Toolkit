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
 * <b>DefaultVertex</b> is the default implementation of a Vertex.
 * 
 * @author Jean-Daniel Fekete
 */
public class DefaultVertex extends DefaultItem implements Vertex {
    /**
     * Creates a DefaultVertex from a vertex and a Vertex Table it belongs to.
     * @param v the vertex
     * @param table the table
     */
    public DefaultVertex(int v, Table table) {
        super(v, table);
    }
    
    /**
     * {@inheritDoc}
     */
    public Graph getGraph() {
        return DefaultGraph.getGraph(getTable());
    }
}

