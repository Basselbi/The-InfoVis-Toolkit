/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Table;
import cern.colt.list.DoubleArrayList;

/**
 * A column of double values, implemented by Colt
 * DoubleArrayList.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 * @infovis.factory ColumnFactory "double" DENSE
 * @infovis.factory ColumnFactory "real" DENSE
 *  */
public class DoubleColumn extends AbstractDoubleColumn {
    private static final long serialVersionUID = 5011207600666768637L;
    protected DoubleArrayList value;

    /**
     * Creates a new DoubleColumn object.
     *
     * @param name the column name.
     */
    public DoubleColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new DoubleColumn object backed
     * on a Colt DoubleArrayList.
     *
     * @param name the column name.
     * @param value the value list which will be used, not copied.
     */
    public DoubleColumn(String name, DoubleArrayList value) {
        super(name);
        this.value = value;
    }

    /**
     * Creates a new DoubleColumn object.
     *
     * @param name the column name.
     * @param reserve the initial capacity.
     */
    public DoubleColumn(String name, int reserve) {
        super(name);
        value = new DoubleArrayList(reserve);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        value.clear();
        super.clear();
    }

    /**
     * Returns the element at the specified position in this column.
     *
     * @param index index of element to return.
     *
     * @return the element at the specified position in this column.
     */
    public double get(int index) {
        assert((index >= 0) && (index < size()))
        : "index: " + index  + " size: " + size();
        return value.getQuick(index);
    }

    /**
     * Replaces the element at the specified position in this column
     * with the specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(int index, double element) {
        assert((index >= 0) && (index < size()))
        : "index: " + index  + " size: " + size();
        value.setQuick(index, element);
        set(index);
    }

    /**
     * Replaces the element at the specified position in this column
     * with the specified element, growing the column if necessary.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void setExtend(int index, double element) {
        assert(index >= 0)
        : index;
        if (index >= size()) {
            if (index == size()) {
                value.setSize(index+1);
            }
            else {
                setSize(index + 1);
            }
        }
        
        set(index, element);
    }

    /**
     * Returns the array of double values copied from the column.
     *
     * @param a an array of double with at least the column size
     *  or <code>null</code>.
     *
     * @return the array of doubles values copied from the column.
     */
    public double[] toArray(double[] a) {
        if (a == null) {
            a = new double[size()];
        }
        System.arraycopy(value.elements(), 0, a, 0, size());
        return a;
    }
    
    /**
     * Returns the array of double values used by the column.
     * BEWARE! no copy is done for performance reasons and the
     * array should only be read. It may become out of sync with
     * the column if the column is resized.
     * 
     * @return the array of double values used by the column.
     */
    public double[] toArray() {
        return value.elements();
    }
    
    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return value.elements().length;
    }
    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
        value.ensureCapacity(minCapacity);
    }
    /**
     * {@inheritDoc}
     */
    public int size() {
        return value.size();
    }
    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        try {
            disableNotify();
            super.setSize(newSize);
            value.setSize(newSize);
        }
        finally {
            enableNotify();
        }
    }
    /**
     * Returns the DoubleArrayList backing the
     * implementation of the column (USE WITH CARE).
     * @return the DoubleArrayList backing the
     * implementation of the column.
     */
    public DoubleArrayList getValueReference() {
        return value;
    }

    /**
     * Finds a double column of the specified name in the table,
     * creating it if necessary.
     * @param table the table
     * @param name the name
     * @return the double column
     */
    public static AbstractDoubleColumn findColumn(Table table, String name) {
        AbstractDoubleColumn c = AbstractDoubleColumn.getColumn(table, name);
        if (c == null) {
            c = new DoubleColumn(name);
            table.addColumn(c);
        }
        return c;
    }
}
