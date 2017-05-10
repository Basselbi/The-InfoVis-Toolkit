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
 * <b>DefaultItem</b> implements the Item interface and
 * extends <code>Number</code> so that it can be used in
 * iterators in a backward compatible way.
 */
public class DefaultItem extends Number implements Item {
    protected int row;
    protected Table table;
    
    /**
     * Creates a DefaultItem associated with a specified row and table.
     * @param row the row
     * @param table the table
     */
    public DefaultItem(int row, Table table) {
        this.row = row;
        this.table = table;
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
     * {@inheritDoc}
     */
    public int hashCode() {
        return getId() 
            + getTable().hashCode() 
            + (getColumn()!=null ? getColumn().hashCode() : 0);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getId() {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Table getTable() {
        return table;
    }
    
    /**
     * {@inheritDoc}
     */
    public double doubleValue() {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public float floatValue() {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public int intValue() {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public long longValue() {
        return row;
    }
}
