/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import infovis.utils.RowComparator;

import java.text.ParseException;


/**
 * <b>PropertyColumn<\b> is a column of properties computed
 * from a table/graph/tree.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class PropertyColumn extends LazyIntColumn {
    protected int readOnly = 0;
    /**
     * Creates a PropertyColumn with the specified name.
     * @param name the name
     */
    public PropertyColumn(String name) {
        super(name);
    }

    /**
     * Creates a PropertyColumn with the specified name and reserved size.
     * @param name the name
     * @param reserve the reserved size
     */
    public PropertyColumn(String name, int reserve) {
        super(name, reserve);
    }

    /**
     * {@inheritDoc}
     */
    public void set(int index, int element) {
        if (isReadOnly()) readonly();
        super.set(index, element);
    }
    
    /**
     * {@inheritDoc}
     */
    public int addExtend(int index, int v) {
        if (isReadOnly()) readonly();
        return super.addExtend(index, v);
    }
    
    /**
     * {@inheritDoc}
     */
    public void fill(int val) {
        if (isReadOnly()) readonly();
        super.fill(val);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setValueAt(int i, String v) throws ParseException {
        if (isReadOnly()) readonly();
        super.setValueAt(i, v);
    }
    
    /**
     * {@inheritDoc}
     */
    public void sort(RowComparator comp) {
        if (isReadOnly()) readonly();
        super.sort(comp);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stableSort(RowComparator comp) {
        if (isReadOnly()) readonly();
        super.stableSort(comp);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        if (isReadOnly()) readonly();
        super.setSize(newSize);
    }

    /**
     * @return true if the column is read-only
     */
    public boolean isReadOnly() {
        return readOnly==0;
    }

    /**
     * Sets the column as read-only.
     * @param readOnly the boolean value
     */
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            this.readOnly++;
        }
        else {
            this.readOnly--;
        }
    }
}
