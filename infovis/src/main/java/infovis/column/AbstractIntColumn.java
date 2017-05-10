/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import cern.colt.function.IntComparator;
import infovis.Column;
import infovis.Table;
import infovis.column.format.IntFormat;

/**
 * <b>AbstractIntColumn</b> is the abstract base class for
 * columns containing integer values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class AbstractIntColumn extends LiteralColumn {

    /**
     * Creates a column with the specified name.
     * @param name
     */
    public AbstractIntColumn(String name) {
        super(name);
        format = IntFormat.INSTANCE;
    }
    
    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    abstract public int get(int index);
    
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
    abstract public void set(int index, int element);
    
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
    abstract public void setExtend(int index, int element);
    
    /**
     * Adds the value of the column at the specified
     * row with the specified value.
     * @param index the row index
     * @param v the value to add
     * @return the new value.
     */
    public int addExtend(int index, int v) {
        assert(index>=0);
        int ret;
        if (isValueUndefined(index)) {
            setExtend(index, ret=v);
        }
        else {
            set(index, ret=get(index)+v);
        }
        return ret;
    }

    
    /**
     * Adds a new element in the column.
     * 
     * @param element
     *            the element.
     */
    public void add(int element) {
        setExtend(size(), element);
    }

    /**
     * {@inheritDoc}
     */
    public Object getObjectAt(int index) {
        if (isValueUndefined(index)) {
            return null;
        }
        return new Integer(get(index));
    }
    
    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object o) {
        if (o == null) {
            setValueUndefined(index, true);
        }
        else {
            set(index, ((Number)o).intValue());
        }
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
        return get(row);
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
        setExtend(row, (int) Math.round(v));
    }

    /**
     * {@inheritDoc}
     */
    public void setIntAt(int row, int v) {
        setExtend(row, v);
    }

    /**
     * {@inheritDoc}
     */
    public void setLongAt(int row, long v) {
        setExtend(row, (int) v);
    }

    /**
     * {@inheritDoc}
     */
    public double coerce(double value) {
        return (int)value;
    }

    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        return format(new Integer((int)value));
    }
    
    /**
     * Returns the minum value of this column.
     * @return the minum value of this column.
     */
    public int getMin() {
        return get(getMinIndex());
    }
    
    /**
     * Returns the maximum value of this column.
     * @return the maximum value of this column.
     */
    public int getMax() {
        return get(getMaxIndex());
    }

    /**
     * Returns a column as an <code>AbstractIntColumn</code> from an <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>AbstractIntColumn</code> or null if no such column exists or
     *         the column is not a <code>IntColumn</code>.
     */
    public static AbstractIntColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof AbstractIntColumn) {
            return (AbstractIntColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>AbstractIntColumn</code> from a <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>AbstractIntColumn</code> or null if no such column exists or
     *         the column is not a <code>IntColumn</code>.
     */
    public static AbstractIntColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof AbstractIntColumn) {
            return (AbstractIntColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Searches the specified array of ints for the specified value using the
     * binary search algorithm using the comparator in the specified range of indices.
     * The array <strong>must</strong> be sorted (as
     * by the <tt>sort</tt> method, above) prior to making this call.  If it
     * is not sorted, the results are undefined.  If the array contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found.
     *
     * @param key the value to be searched for.
     * @param comp the comparator
     * @param low the lowest index to start with
     * @param high the highest index to start with
     * @return index of the search key, if it is contained in the list, between low
     * and high inclusive;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the list: the index of the first
     *         element greater than the key, or <tt>list.size()</tt>, if all
     *         elements in the list are less than the specified key.  Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     */
    public int binarySearch(
            int key, 
            IntComparator comp, 
            int low, 
            int high) {
        while (low <= high) {
            int mid = (low + high) / 2;
            int midVal = get(mid);
            
            int cmp = comp == null ? (midVal-key) : comp.compare(midVal, key);
            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                return mid;
            }
        }
        return -(low + 1);
    }
}
