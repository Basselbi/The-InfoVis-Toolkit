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
import cern.colt.list.FloatArrayList;

/**
 * A Column of float values, implemented by Colt
 * FloatArrayList.
 * 
 * @version $Revision: 1.47 $
 * @author fekete
 * @infovis.factory ColumnFactory "float" DENSE
 */
public class FloatColumn extends AbstractFloatColumn {
    private static final long serialVersionUID = 4855676793070286390L;
    protected FloatArrayList value;

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     */
    public FloatColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public FloatColumn(String name, int reserve) {
        super(name);
        value = new FloatArrayList(reserve);
    }

    /**
     * Creates a new FloatColumn object backed
     * on a Colt FloatArrayList.
     *
     * @param name the column name.
     * @param value the value list which will be used, not copied.
     */
    public FloatColumn(String name, FloatArrayList value) {
        super(name);
        this.value = value;
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public float get(int index) {
        assert ((index >= 0) && (index < size()));
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
     */
    public void set(int index, float element) {
        assert ((index >= 0) && (index < size()));
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
     */
    public void setExtend(int index, float element) {
        assert (index >= 0);
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
     * Fills the column with the specified value.
     * 
     * @param val
     *            the value
     */
    public void fill(float val) {
        undefined = null;
        value.fillFromToWith(0, size() - 1, val);
        super.fill();
        modified();
    }

    /**
     * Returns a column as a <code>FloatColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>FloatColumn</code> from a table,
     */
    public static FloatColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new FloatColumn(name);
            t.addColumn(c);
        }
        return (FloatColumn) c;
    }
    
    /**
     * Returns the array of double values copied from the column.
     * 
     * @param a
     *            an array of double with at least the column size or
     *            <code>null</code>.
     * 
     * @return the array of doubles values copied from the column.
     */
    public float[] toArray(float[] a) {
        if (a == null) {
            a = new float[size()];
        }
        System.arraycopy(value.elements(), 0, a, 0, size());
        return a;
    }

    /**
     * Returns the array of double values used by the column. BEWARE! no copy is
     * done for performance reasons and the array should only be read. It may
     * become out of sync with the column if the column is resized.
     * 
     * @return the array of double values used by the column.
     */
    public float[] toArray() {
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
     * Returns the FloatArrayList backing the
     * implementation of the column (USE WITH CARE).
     * @return the FloatArrayList backing the
     * implementation of the column.
     */
    public FloatArrayList getValueReference() {
        return value;
    }

}