/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.text.ParseException;

/**
 * <b>IdColumn</b> is a column that returns the index as its value.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.21 $
 */
public class IdColumn extends ConstantColumn {
    
    /**
     * Creates an ID Column of the specified size and name.
     * @param name the name
     * @param size the size
     */
    public IdColumn(String name, int size) {
        super(name, size);
    }
    /**
     * Creates an ID Column of the specified size.
     * @param size the size
     */
    public IdColumn(int size) {
        this("#id", size);
    }
    
    /**
     * Creates an ID Column of size 0.
     *
     */
    public IdColumn() {
        this(0);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMinIndex() {
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMaxIndex() {
        return size()-1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int get(int row) {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(int row) {
        if (row >= size) {
            size = Math.max(size, row+1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleAt(int row) {
        return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public double round(double value) {
        return Math.round(value);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasUndefinedValue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Integer.class;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(int index, String element)
        throws ParseException {
        set(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
        set(i);
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {
        return row1 - row2;
    }

}
