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
import infovis.utils.RowFilter;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cern.colt.list.IntArrayList;

/**
 * Class AbstractBooleanColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public abstract class AbstractBooleanColumn extends LiteralColumn 
    implements ListSelectionModel, RowFilter {

    // ListSelectionModel
//    protected int minModified = Integer.MAX_VALUE;
//    protected int maxModified = -1;
    protected int anchorIndex = -1;
    protected int leadIndex = -1;
    protected boolean isAdjusting = false;
    protected int selectedCount = -1;
    
    /**
     * @param name
     */
    public AbstractBooleanColumn(String name) {
        super(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        if (isEmpty()) return;
//        updateMinMaxModified(firstValidRow());
//        updateMinMaxModified(lastValidRow());
        super.clear();
    }

//    protected void updateMinMaxModified(int index) {
//        minModified = Math.min(minModified, index);
//        maxModified = Math.max(maxModified, index);
//    }
    
//    protected boolean modified() {
//    	selectedCount = -1;
//    	return super.modified();
//    }
    
    /**
     * @return the minModified
     */
    public int getMinModified() {
        if (modifs == null)
            return 0;
        return modifs.nextSet(0);
//        return minModified;
    }
    
    /**
     * @return the maxModified
     */
    public int getMaxModified() {
        if (modifs == null)
            return size();
        return modifs.length();
//        return maxModified;
    }
    
    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */    
    public abstract boolean get(int index);
    
    /**
     * Replaces the element at the specified position in this column with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     */
    public abstract void set(int index, boolean element);
    
    /**
     * Replaces the element at the specified position in this column with the
     * specified element, growing the column if necessary.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     */
    public abstract void setExtend(int index, boolean element);
    

    /**
     * Sets a list of indexes with a specified value.
     * 
     * @param list the indexes list
     * @param element the boolean value
     */
    public void setExtend(IntArrayList list, boolean element) {
        for (int i = 0; i < list.size(); i++) {
            int index = list.getQuick(i);
            setExtend(index, element);
        }
    }

    /**
     * Adds a new element in the column.
     * 
     * @param element
     *            the element.
     */
    public final void add(boolean element) {
        setExtend(size(), element);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        return isValueUndefined(row);
    }

    /**
     * {@inheritDoc}
     */
    public Object getObjectAt(int index) {
        if (isValueUndefined(index)) {
            return null;
        }
        return new Boolean(get(index));
    }

    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object o) {
        if (o == null) {
            setValueUndefined(index, true);
        }
        else if (o instanceof Boolean) {
            set(index, ((Boolean)o).booleanValue());
        }
        else {
            set(index, ((Number)o).intValue()!=0);
        }
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
    public int compare(int row1, int row2) {
        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
        int v1 = get(row1) ? 1 : 0;
        int v2 = get(row2) ? 1 : 0;

        return v1 - v2;
    }

    /**
     * @see javax.swing.ListSelectionModel#addListSelectionListener(ListSelectionListener)
     */
    public void addListSelectionListener(ListSelectionListener x) {
        getEventListenerList().add(ListSelectionListener.class, x);
    }

    /**
     * @see javax.swing.ListSelectionModel#addSelectionInterval(int, int)
     */
    public void addSelectionInterval(int index0, int index1) {
        anchorIndex = index0;
        leadIndex = index1;
        disableNotify();
        try {
            while (index0 <= index1) {
                setExtend(index0, true);
                index0++;
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * @see javax.swing.ListSelectionModel#clearSelection()
     */
    public void clearSelection() {
        anchorIndex = -1;
        leadIndex = -1;
        clear();
    }

    /**
     * @see javax.swing.ListSelectionModel#getAnchorSelectionIndex()
     */
    public int getAnchorSelectionIndex() {
        return anchorIndex;
    }

    /**
     * @see javax.swing.ListSelectionModel#getLeadSelectionIndex()
     */
    public int getLeadSelectionIndex() {
        return leadIndex;
    }

    /**
     * @see javax.swing.ListSelectionModel#getMaxSelectionIndex()
     */
    public int getMaxSelectionIndex() {
        return lastValidRow();
    }

    /**
     * @see javax.swing.ListSelectionModel#getMinSelectionIndex()
     */
    public int getMinSelectionIndex() {
        return firstValidRow();
    }

    /**
     * @see javax.swing.ListSelectionModel#getSelectionMode()
     */
    public int getSelectionMode() {
        return MULTIPLE_INTERVAL_SELECTION;
    }

    /**
     * @see javax.swing.ListSelectionModel#getValueIsAdjusting()
     */
    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }

    /**
     * @see javax.swing.ListSelectionModel#insertIndexInterval(int, int,
     *      boolean)
     */
    public void insertIndexInterval(int index, int length, boolean before) {
    }

    /**
     * @see javax.swing.ListSelectionModel#isSelectedIndex(int)
     */
    public boolean isSelectedIndex(int index) {
        return !isFiltered(index);
    }

    /**
     * @see javax.swing.ListSelectionModel#isSelectionEmpty()
     */
    public boolean isSelectionEmpty() {
        return isEmpty();
    }

    /**
     * Returns the number of selected items.
     * 
     * @return the number of selected items.
     */
    public int getSelectedCount() {
    	if (selectedCount != -1) 
    		return selectedCount;
        int cnt = 0;
        int max = getMaxSelectionIndex();
        
        for (int i = getMinSelectionIndex(); i <= max; i++) {
            if (isSelectedIndex(i))
                cnt++;
        }
        selectedCount = cnt;
        return cnt;
    }

    /**
     * @see javax.swing.ListSelectionModel#removeIndexInterval(int, int)
     */
    public void removeIndexInterval(int index0, int index1) {
    }

    /**
     * @see javax.swing.ListSelectionModel#removeListSelectionListener(ListSelectionListener)
     */
    public void removeListSelectionListener(ListSelectionListener x) {
        getEventListenerList().remove(ListSelectionListener.class, x);
    }

    /**
     * @see javax.swing.ListSelectionModel#removeSelectionInterval(int, int)
     */
    public void removeSelectionInterval(int index0, int index1) {
        while (index0 <= index1) {
            setValueUndefined(index0, true);
            index0++;
        }
    }

    /**
     * @see javax.swing.ListSelectionModel#setAnchorSelectionIndex(int)
     */
    public void setAnchorSelectionIndex(int index) {
        anchorIndex = index;
    }

    /**
     * @see javax.swing.ListSelectionModel#setLeadSelectionIndex(int)
     */
    public void setLeadSelectionIndex(int index) {
        leadIndex = index;
    }

    /**
     * @see javax.swing.ListSelectionModel#setSelectionInterval(int, int)
     */
    public void setSelectionInterval(int index0, int index1) {
        clear();
        addSelectionInterval(index0, index1);
    }

    /**
     * @see javax.swing.ListSelectionModel#setSelectionMode(int)
     */
    public void setSelectionMode(int selectionMode) {
    }

    /**
     * @see javax.swing.ListSelectionModel#setValueIsAdjusting(boolean)
     */
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
        this.isAdjusting = valueIsAdjusting;
    }

    private void fireValueChanged() {
//        if (maxModified == -1 || ! hasEventListener())
        if (! hasEventListener())
            return;

        int firstChanged = getMinModified();
        int lastChanged = getMaxModified();
//        minModified = Integer.MAX_VALUE;
//        maxModified = -1;
        Object[] listeners = getEventListenerList().getListenerList();
        ListSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null) {
                    e = new ListSelectionEvent(
                            this, 
                            firstChanged,
                            lastChanged, 
                            isAdjusting);
                }
                ((ListSelectionListener) listeners[i + 1])
                        .valueChanged(e);
            }
        }
    }
    
    protected void fireChanged() {
        fireValueChanged();
        super.fireChanged();
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleAt(int row) {
        return get(row) ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        set(row, v != 0);
    }

    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        boolean v = (value != 0);
        return format(v);
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Boolean.class;
    }

    /**
     * Returns the string representation of a value according to the current
     * format.
     * 
     * @param v
     *            the value
     * 
     * @return the string representation.
     */
    public String format(boolean v) {
        if (getFormat() != null) {
            return getFormat().format(new Boolean(v));
        }

        return Boolean.toString(v);
    }
    
    /**
     * {@inheritDoc}
     */
    public double coerce(double value) {
        return value != 0 ? 1 : 0;
    }
    

    /**
     * Returns a column as a <code>AbstractBooleanColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return an <code>AbstractBooleanColumn</code> or null if no such column exists or
     *         the column is not an <code>AbstractBooleanColumn</code>.
     */
    public static AbstractBooleanColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof AbstractBooleanColumn) {
            return (AbstractBooleanColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>AbstractBooleanColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return an <code>AbstractBooleanColumn</code> or null if no such column exists
     *         or the column is not a <code>AbstractBooleanColumn</code>.
     */
    public static AbstractBooleanColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof BooleanColumn) {
            return (BooleanColumn) c;
        } else {
            return null;
        }
    }

}
