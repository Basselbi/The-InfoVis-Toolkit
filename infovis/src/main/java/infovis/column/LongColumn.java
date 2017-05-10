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
import cern.colt.list.LongArrayList;


/**
 * A Column of longs.
 * 
 * @version $Revision: 1.45 $
 * @author fekete
 * @infovis.factory ColumnFactory "long" DENSE
 */
public class LongColumn extends LiteralColumn {
    private static final long serialVersionUID = -2924782791914655880L;
    protected LongArrayList value;

    /**
     * Creates a new LongColumn object.
     * 
     * @param name the column name.
     */
    public LongColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new LongColumn object.
     * 
     * @param name the column name.
     * @param reserve the initial capacity.
     */
    public LongColumn(String name, int reserve) {
        super(name);
        
        value = new LongArrayList(reserve);
        format = IntFormat.INSTANCE;
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
    public long get(int index) {
	assert((index >= 0) && (index < size()));
        return value.getQuick(index);
    }

    /**
     * Replaces the element at the specified position in this column
     * with the specified element.
     * 
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(int index, long element) {
	assert((index >= 0) && (index < size()));
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
  	
    public void setExtend(int index, long element) {
	assert(index >= 0);
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
     * @return the minimum value
     */
    public long getMin() {
        return get(getMinIndex());
    }
    
    /**
     * @return the maximum value
     */
    public long getMax() {
        return get(getMaxIndex());
    }
	
    /**
     * Adds a new element in the column.
     * 
     * @param element the element.
     */
    public final void add(long element) {
        setExtend(size(), element);
    }

    /**
     * Fills the column with the specified value
     * 
     * @param val the value
     */
    public void fill(long val) {
        undefined = null;
        value.fillFromToWith(0, size() - 1, val);
        super.fill();
        modified(0, size()-1);
    }
    

    /**
     * Returns the array of double values copied from the column.
     * 
     * @param a
     *            an array of double with at least the column size or
     *            <code>null</code>.
     * 
     * @return the array of double values copied from the column.
     */
    public double[] toArray(double[] a) {
        if (a == null) {
            a = new double[size()];
        }
        System.arraycopy(value.elements(), 0, a, 0, size());
        return a;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getObjectAt(int index) {
        if (isValueUndefined(index)) {
            return null;
        }
        return new Long(get(index));
    }
    
    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object o) {
        if (o == null) {
            setValueUndefined(index, true);
        }
        else {
            set(index, ((Number)o).longValue());
        }
    }

    /**
     * Returns a column as a <code>LongColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t the <code>Table</code>
     * @param index index in the <code>Table</code>
     * 
     * @return a <code>LongColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>LongColumn</code>.
     */
    public static LongColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof LongColumn) {
            return (LongColumn)c;
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns a column as a <code>LongColumn</code> from a
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a <code>LongColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>LongColumn</code>.
     */
    public static LongColumn getColumn(Table t, String name) {
	Column c = t.getColumn(name);

	if (c instanceof LongColumn) {
	    return (LongColumn)c;
	} else {
	    return null;
	}
    }
	
    /**
     * Returns a column as a <code>LongColumn</code> from a table,
     * creating it if needed.
     * 
     * @param t the <code>Table</code>
     * @param name the column name.
     * 
     * @return a column as a <code>LongColumn</code> from a table,
     */
    public static LongColumn findColumn(Table t, String name) {
    	Column c = t.getColumn(name);
    	if (c == null) {
	    c = new LongColumn(name);
	    t.addColumn(c);
    	}
    	return (LongColumn)c;
    }
    
    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Long.class;
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
    public float getFloatAt(int row) {
	return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public int getIntAt(int row) {
	return (int)get(row);
    }

    /**
     * {@inheritDoc}
     */
    public long getLongAt(int row) {
	return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
	setExtend(row, (long)Math.round(v));
    }

    /**
     * {@inheritDoc}
     */
    public void setLongAt(int row, long v) {
	setExtend(row, v);
    }
    
    /**
     * {@inheritDoc}
     */
    public double coerce(double value) {
        return (long)(value);
    }
    
    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        return format(new Long((long)value));
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
     * @return the backing colt array
     */
    public LongArrayList getValueReference() {
        return value;
    }
}
