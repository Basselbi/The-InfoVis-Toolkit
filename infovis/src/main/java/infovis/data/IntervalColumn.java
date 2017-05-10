/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import infovis.Column;
import infovis.Table;
import infovis.column.BasicObjectColumn;
import infovis.column.NumberColumn;
import infovis.column.format.IntervalFormat;
import infovis.utils.RowIterator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Comparator;

/**
 * <b>IntervalColumn</b> is a column containing interval values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 * 
 * @infovis.factory ColumnFactory "interval" DENSE
 * @infovis.factory ColumnFactory "inter" DENSE
 */
public class IntervalColumn extends BasicObjectColumn 
    implements NumberColumn {
    static class IntervalComparator implements Comparator, Serializable {
        static final IntervalComparator INSTANCE = new IntervalComparator();
        
        /**
         * Returns -1, 0 or 1 depending on the sign of d
         * @param d the value
         * @return -1 if d &lt; 0, 0 if d==0, +1 if d&gt;0
         */
        public static int sign(double d) {
            if (d < 0) return -1;
            else if (d > 0) return 1;
            return 0;
        }

        /**
         * @see infovis.Column#compare(int,int)
         */
        public int compare(Object o1, Object o2) {
            if (o1 == o2)
                return 0;
            Interval i1 = (Interval)o1;
            Interval i2 = (Interval)o2;
            double d = i1.center() - i2.center();
            return sign(d);
        }
        
        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
    static final IntervalComparator comparator = new IntervalComparator();
    /**
     * @param name
     */
    public IntervalColumn(String name) {
        super(name);
        setFormat(IntervalFormat.getInstance());
        setOrder(comparator);
    }

    /**
     * @param name
     * @param reserve
     */
    public IntervalColumn(String name, int reserve) {
        super(name, reserve);
        setFormat(IntervalFormat.getInstance());
        setOrder(comparator);
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return DoubleInterval.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Interval.class;
    }
    
    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public Interval get(int index) {
        return (Interval) getObjectAt(index);
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
    public void set(int index, Interval element) {
        setObjectAt(index, element);
    }
    
    // NumberColumn interface
    
    /**
     * {@inheritDoc}
     */
    public double getDoubleAt(int row) {
        Interval i = get(row);
        if (i == null) return Double.NaN;
        return i.center();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getIntAt(int row) {
        return (int)getDoubleAt(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public float getFloatAt(int row) {
        return (float)getDoubleAt(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public long getLongAt(int row) {
        return (long)getDoubleAt(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        Interval i = get(row);
        if (i == null) {
            i = new DoubleInterval(v, v);
            set(row, i);
        }
        else {
            i.set(v, v);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFloatAt(int row, float v) {
        setDoubleAt(row, v);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setIntAt(int row, int v) {
        setDoubleAt(row, v);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setLongAt(int row, long v) {
        setDoubleAt(row, v);
    }
    
    protected boolean isLess(int i, int j) {
        Interval i1 = get(i);
        Interval i2 = get(j);
        if (i1 == null && i2 == null) {
            return false;
        }
        if (i1 == null) {
            return true;
        }
        if (i2 == null) {
            return false;
        }
        return i1.getMin() < i2.getMin();
    }
    
    protected boolean isGreater(int i, int j) {
        Interval i1 = get(i);
        Interval i2 = get(j);
        if (i1 == null && i2 == null) {
            return false;
        }
        if (i1 == null) {
            return false;
        }
        if (i2 == null) {
            return true;
        }
        return i1.getMax() > i2.getMax();        
    }
    
    protected void updateMinMax() {
        if (min_max_updated) {
            return;
        }

        minIndex = -1;
        maxIndex = -1;

        for (RowIterator iter = iterator(); iter.hasNext();) {
            int i = iter.nextRow();
            if (minIndex == -1 || isLess(i, minIndex)) {
                minIndex = i;
            }

            if (maxIndex == -1 || isGreater(i, maxIndex)) {
                maxIndex = i;
            }
        }
        min_max_updated = true;
    }
    /**
     * {@inheritDoc}
     */
    public double getDoubleMin() {
        int index = getMinIndex();
        if (index == -1) {
            return Double.NaN;
        }
        Interval i = get(index);
        return i.getMin();
    }
    
    /**
     * {@inheritDoc}
     */
    public double getDoubleMax() {
        int index = getMaxIndex();
        if (index == -1) {
            return Double.NaN;
        }
        Interval i = get(index);
        return i.getMax();
    }
    
    /**
     * {@inheritDoc}
     */
    public double coerce(double value) {
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        return Double.toString(value);
    }


    /**
     * Returns a column as an <code>IntervalColumn</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return an <code>IntervalColumn</code> or null if no such column exists or
     *         the column is not a <code>IntervalColumn</code>.
     */
    public static IntervalColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof IntervalColumn) {
            return (IntervalColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>IntervalColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return an <code>IntervalColumn</code> or null if no such column exists or
     *         the column is not a <code>IntervalColumn</code>.
     */
    public static IntervalColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof IntervalColumn) {
            return (IntervalColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>IntervalColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>IntervalColumn</code> from a table,
     */
    public static IntervalColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c == null) {
            c = new IntervalColumn(name);
            t.addColumn(c);
        }

        return (IntervalColumn) c;
    }
}
