/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.table.DefaultDynamicTable;
import infovis.table.DefaultItem;

/**
 * <b>DefaultEdgeTable</b> is the default implementation of
 * an edge table.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultEdgeTable extends DefaultDynamicTable {

    /**
     * Default constructor. 
     */
    public DefaultEdgeTable() {
    }
    
    protected DefaultItem createItem(int row) {
        return new DefaultEdge(row, this);
    }

}
