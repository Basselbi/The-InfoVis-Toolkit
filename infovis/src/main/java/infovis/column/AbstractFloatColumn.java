/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.column.format.DoubleFormat;

/**
 * Class AbstractFloatColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFloatColumn extends LiteralColumn {

    /**
     * Constructor.
     * @param name the column name
     */
    public AbstractFloatColumn(String name) {
        super(name);
        format = DoubleFormat.INSTANCE;
    }

    
    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    abstract public float get(int index);
    
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
    abstract public void set(int index, float element);
    
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
    abstract public void setExtend(int index, float element);
    
    /**
     * Adds the value of the column at the specified
     * row with the specified value.
     * @param index the row index
     * @param v the value to add
     * @return the new value.
     */
    public float addExtend(int index, float v) {
        assert(index>=0);
        float ret;
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
    public void add(float element) {
        setExtend(size(), element);
    }



    /**
     * {@inheritDoc}
     */
    public Object getObjectAt(int index) {
        if (isValueUndefined(index)) {
            return null;
        }
        return new Float(get(index));
    }
    
    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object o) {
        if (o == null) {
            setValueUndefined(index, true);
        }
        else {
            set(index, ((Number)o).floatValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Float.class;
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
        return (long)get(row);
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        setExtend(row, (float)v);
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
        setExtend(row, v);
    }

    /**
     * {@inheritDoc}
     */
    public double coerce(double value) {
        return (float)value;
    }

    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        return format(new Float((float)value));
    }
    
    /**
     * Returns the minum value of this column.
     * @return the minum value of this column.
     */
    public float getMin() {
        return get(getMinIndex());
    }
    
    /**
     * Returns the maximum value of this column.
     * @return the maximum value of this column.
     */
    public float getMax() {
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
    public static AbstractFloatColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof AbstractFloatColumn) {
            return (AbstractFloatColumn) c;
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
    public static AbstractFloatColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof AbstractFloatColumn) {
            return (AbstractFloatColumn) c;
        } else {
            return null;
        }
    }
}
