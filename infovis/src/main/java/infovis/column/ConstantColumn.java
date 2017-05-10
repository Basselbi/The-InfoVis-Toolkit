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
import infovis.utils.RowIterator;
import infovis.utils.TableIterator;

import java.text.Format;
import java.text.ParseException;

import javax.swing.event.ChangeListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

/**
 * 
 * Column containing constant values for each rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class ConstantColumn implements NumberColumn {
    protected int size;
    protected String name;
    
    /**
     * Metadata associated with this column.
     */
    protected MutableAttributeSet metadata;

    /**
     * User's client properties.
     */
    protected transient MutableAttributeSet clientProperty;

    /**
     * Format used to convert from the internal representation to a
     * comprehensible readable user's representation.  When null, a
     * default format is used.
     */
    protected Format format;
    
    /**
     * Creates a ConstantColumn of the specified size.
     * @param size the size
     */ 
    public ConstantColumn(int size) {
        this("#constant", size);
    }
    
    /**
     * Creates a ConstantColumn of the specified size and name.
     * @param name the name
     * @param size the size
     */ 
    public ConstantColumn(String name, int size) {
        this.name = name;
        this.size = size;
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
    public String format(Object o) {
        if (format != null) {
            return format.format(o);
        }
        return o.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getObjectAt(int index) {
        return new Double(0);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object o) {
        setDoubleAt(index, 0);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object parse(String s) throws ParseException {
        if (format != null) {
            format.parseObject(s);
        }
        return s;
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
    public double getDoubleAt(int row) {
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public void copyValueFrom(int toIndex, Column c, int fromIndex) throws ParseException {
        AbstractColumn.readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setIntAt(int row, int v) {
        AbstractColumn.readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setFloatAt(int row, float v) {
        AbstractColumn.readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setLongAt(int row, long v) {
        AbstractColumn.readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        AbstractColumn.readonly();
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleMin() {
        return getDoubleAt(getMinIndex());
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleMax() {
        return getDoubleAt(getMaxIndex());
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
        if (format != null) {
            return format.format(new Double(value));
        }
        return ""+value;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInternal() {
        if (name == null || name.isEmpty()) return true;
        return name.charAt(0) == Table.INTERNAL_PREFIX;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
        AbstractColumn.readonly();
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
    public Format getFormat() {
        return format;
    }

    /**
     * {@inheritDoc}
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        this.size = newSize;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int i) {
        if (size < 0)
            return false;
        return i < 0 || i >= size;
    }

    /**
     * @return the last valid row
     */
    public int lastValidRow() {
        return size-1;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        metadata = null;
        clientProperty = null;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public void copyFrom(Column from) {
//    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
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
    public String getValueAt(int index) {
        return format(getDoubleAt(index));
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(int index, String element) throws ParseException {
        AbstractColumn.readonly();
    }

    /**
     * {@inheritDoc}
     */
    public boolean setValueOrNullAt(int index, String v) {
        AbstractColumn.readonly();
        return false;
    }

    /**
     * Adds a value specified as a String to the column.
     * 
     * @param v the value
     * @exception ParseException if the value has a wrong syntax
     */
    public void addValue(String v) throws ParseException {
        AbstractColumn.readonly();
    }

    /**
     * Adds a value specified as a String to the column or NULL
     * if the value is not in the right format or null.
     * 
     * @param v the value
     */
    public boolean addValueOrNull(String v) {
        AbstractColumn.readonly();
        return false;
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
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Double.class;
    }

    /**
     * {@inheritDoc}
     */
    public void disableNotify() {
    }

    /**
     * {@inheritDoc}
     */
    public void enableNotify() {
    }

    /**
     * {@inheritDoc}
     */
    public void addChangeListener(ChangeListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public void removeChangeListener(ChangeListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new TableIterator(0, size());
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getMetadata() {
        if (metadata == null) {
            metadata = new SimpleAttributeSet();
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getClientProperty() {
        if (clientProperty == null) {
            clientProperty = new SimpleAttributeSet();
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int arg0, int arg1) {
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public int compare(Object o1, Object o2) {
        return 0;
    }

}
