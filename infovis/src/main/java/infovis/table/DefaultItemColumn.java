/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.table;

import infovis.Column;
import infovis.Table;

/**
 * Class DefaultItemColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultItemColumn implements Item {
    protected Item item;
    protected Column column;
    
    /**
     * Creates a DefaultItemColumn from a specified item and a column
     * @param item the item
     * @param column the column
     */
    public DefaultItemColumn(Item item, Column column) {
        this.item = item;
        this.column = column;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (! (obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        return getId()==other.getId()
            && getTable()==other.getTable()
            && getColumn()==other.getColumn();
    }

    /**
     * @see infovis.table.Item#getId()
     */
    public int getId() {
        return item.getId();
    }
    
    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return column;
    }

    /**
     * @see infovis.table.Item#getTable()
     */
    public Table getTable() {
        return item.getTable();
    }
    
    
    
    
}
