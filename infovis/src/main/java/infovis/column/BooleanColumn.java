/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.util.BitSet;

import infovis.Column;
import infovis.Table;
import infovis.column.format.BooleanFormat;
//import intset.IntegerSet;
//import intset.TreeIntegerSet;

/**
 * Column of booleans.
 * 
 * <p>Implements columns of boolean values, backed on a <code>BitSet</code>.
 * A <code>BooleanColumn</code> also implements the ListSelectionModel so it
 * can be used to control a selection in Swing.
 * 
 * <p>Creation: <code>BooleanColumn c = new BooleanColumn("yesorno");</code>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.50 $
 *
 * @infovis.factory ColumnFactory "boolean" DENSE
 * @infovis.factory ColumnFactory "bool" DENSE
 */
public class BooleanColumn extends AbstractBooleanColumn {
    private static final long serialVersionUID = -4800193761758420834L;
    protected int size;
//    protected TreeIntegerSet value;
    protected BitSet value;

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     */
    public BooleanColumn(String name) {
        super(name);

//        value = new TreeIntegerSet();
        value = new BitSet();
        format = BooleanFormat.getInstance();
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        super.clear();
        value.clear();
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean get(int index) {
        assert ((index >= 0) && (index < size()));
        return value.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public void set(int index, boolean element) {
        assert ((index >= 0) && (index < size()));
        value.set(index, element);
//        updateMinMaxModified(index);
        set(index);
    }
    
    /**
     * Returns the minimum value.
     * @return the minimum value.
     */
    public boolean getMin() {
        return get(getMinIndex());
    }

    /**
     * Returns the maximum value.
     * @return the maximum value.
     */
    public boolean getMax() {
        return get(getMaxIndex());
    }

    /**
     * {@inheritDoc}
     */
    public void setExtend(int index, boolean element) {
        assert (index >= 0);
        if (index >= size) {
            if (index == size) {
                size = index+1;
            }
            else
                setSize(index+1);
        }
        set(index, element);
    }

    /**
     * Fills the column with the specified value.
     * 
     * @param v
     *            the value
     */
    public void fill(boolean v) {
        if (v) {
            value.set(0, size());
        } else {
            value.clear();
        }
        super.fill();
//        minModified = 0;
//        maxModified = size()-1;
        modified();
    }

    /**
     * Returns a column as an <code>AbstractBooleanColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as an <code>AbstractBooleanColumn</code> from a table,
     */
    public static AbstractBooleanColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c instanceof AbstractBooleanColumn) {
            AbstractBooleanColumn abc = (AbstractBooleanColumn) c;
            return abc;
        }
        c = new BooleanColumn(name);
        t.addColumn(c);
        return (AbstractBooleanColumn) c;
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return Integer.MAX_VALUE;
    }
    
    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
    }
    

    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        if (newSize == size) return;
        try {
            disableNotify();
            super.setSize(newSize);
            size = newSize;
        }
        finally {
            enableNotify();
        }
    }
    /**
     * Returns the backing BitSet of this column (USE WITH CARE).
     * @return the backing BitSet of this column.
     */
//    public IntegerSet getValueReference() {
    public BitSet getValueReference() {
        return value;
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    public void addSelectionInterval(int index0, int index1) {
//        anchorIndex = index0;
//        leadIndex = index1;
//        value.set(index0, index1+1);
//        if (index1 >= size) {
//            size = index1+1;
//        }
//        modified();
//    }
    
    /**
     * {@inheritDoc}
     */
    public int lastValidRow() {
        return value.length()-1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int firstValidRow() {
        if (undefined == null) {
            return 0;
        }
        return undefined.nextClear(0);

    }
   
}