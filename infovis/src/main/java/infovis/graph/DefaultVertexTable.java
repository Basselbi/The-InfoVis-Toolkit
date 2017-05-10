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
 * <b>DefaultVertexTable</b> is the default implementation of
 * a vertex table.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultVertexTable extends DefaultDynamicTable {

    /**
     * Default constructor.
     */
    public DefaultVertexTable() {
    }

    protected DefaultItem createItem(int row) {
        return new DefaultVertex(row, this);
    }
}
