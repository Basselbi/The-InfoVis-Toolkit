/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.table.Item;


/**
 * <b>Edge</b> is a object that references an
 * edge index in a Graph.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface Edge extends Item {
    /**
     * @return the associated graph
     */
    Graph getGraph();
    
    /**
     * @return the first vertex of this edge.
     */
    Vertex getFirstVertex();
    
    /**
     * @return the second vertex of this edge.
     */
    Vertex getSecondVertex();
}