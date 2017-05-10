/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.utils.RowComparator;
import cern.colt.Sorting;
import cern.colt.list.IntArrayList;

/**
 * A Column of integer values.
 * 
 * @version $Revision: 1.58 $
 * @author fekete
 * @infovis.factory ColumnFactory "integer" DENSE
 * @infovis.factory ColumnFactory "int" DENSE
 */
public class IntColumn extends AbstractIntColumn {
    private static final long serialVersionUID = 3599591292497105366L;
    protected IntArrayList value;

    /**
     * Creates a new IntColumn object.
     * 
     * @param name
     *            the column name.
     */
    public IntColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new IntColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial allocated size.
     */
    public IntColumn(String name, int reserve) {
        super(name);

        value = new IntArrayList(reserve);
    }

    /**
     * Creates a new IntColumn object backed on a
     * colt IntArrayList.
     * 
     * @param name
     *            the column name.
     * @param value
     *            the colt IntArrayList holding the values (not copied)
     */
    public IntColumn(String name, IntArrayList value) {
        super(name);
        this.value = value;
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
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public int get(int index) {
        assert ((index >= 0) && (index < size()))
        : "index: " + index  + " size(): " + size();

        return value.getQuick(index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     *  
     */
    public void set(int index, int element) {
        assert ((index >= 0) && (index < size()))
    	: "index: " + index  + " size(): " + size();

        value.setQuick(index, element);
        set(index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element, growing the column if necessary.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     *  
     */
    public void setExtend(int index, int element) {
        assert (index >= 0);
        if (index >= size()) {
            if (index == size()) 
                value.setSize(index+1);
            else
                setSize(index + 1);
        }
        set(index, element);
    }
    
    /**
     * Fills the column with the specified value.
     * 
     * @param val
     *            the value
     */
    public void fill(int val) {
        undefined = null;
        value.fillFromToWith(0, size() - 1, val);
        super.fill();
        modified(0, size()-1);
    }
    /**
     * Returns the array of integer values copied from the column.
     * 
     * @param a
     *            an array of int with at least the column size or
     *            <code>null</code>.
     * 
     * @return the array of integer values copied from the column.
     */
    public int[] toArray(int[] a) {
        if (a == null) {
            a = new int[size()];
        }
        System.arraycopy(value.elements(), 0, a, 0, size());
        return a;
    }
    

    /**
     * Returns a column as a <code>IntColumn</code> from a table, creating it
     * if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>IntColumn</code> from a table,
     */
    public static IntColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new IntColumn(name);
            t.addColumn(c);
        }
        return (IntColumn) c;
    }


    /**
     * Returns the array of integer values used by the column. BEWARE! no copy
     * is done for performance reasons and the array should only be read. It may
     * become out of sync with the column if the column is resized.
     * 
     * @return the array of integer values used by the column.
     */
    public int[] toArray() {
        return value.elements();
    }

    /**
     * Sort the values of this column according to a comparator.
     * @param comp the comparator
     */
    public void sort(RowComparator comp) {
        if (comp == null)
            return;
        Sorting.quickSort(value.elements(), 0, size(), comp);
        modified(0, size()-1);
    }
    
    /**
     * Sort the values of this column according to a comparator using
     * a stable sort algorithm.
     * @param comp the comparator
     */
    public void stableSort(RowComparator comp) {
        if (comp == null)
            return;
        Sorting.mergeSort(value.elements(), 0, size(), comp);
        modified(0, size()-1);
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
    public IntArrayList getValueReference() {
        return value;
    }
    
}