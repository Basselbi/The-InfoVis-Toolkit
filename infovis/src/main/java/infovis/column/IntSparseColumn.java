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
import infovis.column.format.IntFormat;
import infovis.utils.IntIntSortedMap;
import infovis.utils.RowIterator;
import cern.colt.function.IntIntProcedure;

/**
 * <b>IntSparseColumn</b> is a spare IntColumn.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 * @infovis.factory ColumnFactory "integer" SPARSE
 */
public class IntSparseColumn extends AbstractIntColumn {
    private static final long serialVersionUID = -5930295679242132449L;
    protected IntIntSortedMap value;
    protected int size;

    /**
     * Creates a sparse IntColumn with the specified name.
     * @param name the name
     */
    public IntSparseColumn(String name) {
        super(name);
        value = new IntIntSortedMap();
        size = 0;
        format = IntFormat.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        size = newSize;
        for (RowIterator iter = value.keyIterator(newSize); iter.hasNext(); ) {
            iter.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int i) {
        return i < 0 || i >= size || !value.containsKey(i);
    }

    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
        if (undef == isValueUndefined(i)) return;
        if (undef) {
            value.remove(i);
        } else {
            value.put(i, 0);
        }
        min_max_updated = false;
//        modified(i);
        modified(i);
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
    public boolean hasUndefinedValue() {
        return value.size() != size();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        value.clear();
        size = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean compareValues(Column c) {
        if (! (c instanceof NumberColumn)) {
            return false;
        }
        NumberColumn other = (NumberColumn)c;
        for (int i = 0; i < size(); i++) {
            if (isValueUndefined(i)) {
                if (! c.isValueUndefined(i)) {
                    return false;
                }
            }
            else {
                if (getDoubleAt(i) != other.getDoubleAt(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the value at the specified index.
     * @param index the index
     * @return the value at the specified index
     */
    public int get(int index) {
        assert(!isValueUndefined(index));
        return value.get(index);
    }

    /**
     * Sets the value at the specified index.
     * @param index the index
     * @param element the value
     */
    public void set(int index, int element) {
        assert(index >= 0 && index < size);
        value.put(index, element);
        min_max_updated = false;
        modified(index);
//        modified();
    }

    /**
     * Sets the element at the specified index, extending
     * the column if necessary.
     * @param index the index
     * @param element the element
     */
    public void setExtend(int index, int element) {
        size = Math.max(index+1, size);
        set(index, element);
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
    public static IntSparseColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new IntSparseColumn(name);
            t.addColumn(c);
        }
        return (IntSparseColumn) c;
    }
    
    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
    }
    
    /**
     * @return Returns the backing IntIntSortedMap.
     */
    public IntIntSortedMap getValueReference() {
        return value;
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
        final int[] array = a;
        value.forEachPair(new IntIntProcedure(){
        
            public boolean apply(int key, int value) {
                array[key] = value;
                return true;
            }
        
        });
        return a;
    }

    /**
     * Returns the array of integer values used by the column. BEWARE! no copy
     * is done for performance reasons and the array should only be read. It may
     * become out of sync with the column if the column is resized.
     * 
     * @return the array of integer values used by the column.
     */
    public int[] toArray() {
        return toArray(null);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return value.keyIterator();
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {
        if (row1 == row2)
            return 0;
        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
        double d = (get(row1) - get(row2));
        if (d == 0)
            return 0;
        else if (d < 0)
            return -1;
        else
            return 1;
    }   
}