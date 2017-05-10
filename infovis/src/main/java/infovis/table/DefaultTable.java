/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import infovis.Column;
import infovis.Table;
import infovis.column.ColumnColumn;
import infovis.column.ObjectColumn;
import infovis.column.event.ColumnChangeEvent;
import infovis.utils.RowIterator;
import infovis.utils.RowObject;
import infovis.utils.TableIterator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


/**
 * Concrete Table.
 *
 * Implements all the methods of <code>Table</code> managing a
 * Column of Columns.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.48 $
 */
public class DefaultTable extends ColumnColumn 
    implements Table, ChangeListener, Serializable {
    private static final long                  serialVersionUID      = 972957682588411271L;
    /** Identifies the addtion of new rows or columns. */
    protected static final int                 INSERT                = TableModelEvent.INSERT;
    /** Identifies a change to existing data. */
    protected static final int                 UPDATE                = TableModelEvent.UPDATE;
    /** Identifies the removal of rows or columns. */
    protected static final int                 DELETE                = TableModelEvent.DELETE;

    protected ObjectColumn                     itemColumn;
    protected HashMap                          columns;
    protected transient EventListenerList      listenerList;
    protected transient boolean                hasTableModelListener = false;
    protected transient MutableTableModelEvent tmpEvent              = new MutableTableModelEvent(
                                                                             this);
    /**
     * Creates a new DefaultTable object.
     */
    public DefaultTable() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return size();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        try {
            disableNotify();
            for (int i = 0; i < getColumnCount(); i++) {
                getColumnAt(i).clear();
            }
            if (metadata != null) {
                metadata.removeAttributes(metadata.getAttributeNames());
            }
            if (clientProperty != null) {
                clientProperty.removeAttributes(clientProperty.getAttributeNames());
            }
            if (itemColumn != null) {
    //            for (RowIterator iter = itemColumn.iterator(); iter.hasNext(); ) {
    //                DefaultItem item = (DefaultItem)iter.next();
    //                item.invalidate();
    //            }
                itemColumn.clear();
            }
        }
        finally {
            enableNotify();
            fireTableStructureChanged();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Item getItem(int row) {
        if (! isRowValid(row)) return null;
        DefaultItem item;
        if (itemColumn == null) {
            return createItem(row);
//            itemColumn = new ObjectColumn("#items");
//            addColumn(itemColumn);
        }
        else {
            item = (DefaultItem)itemColumn.get(row);
            if (item != null) {
                return item;
            }
        }
        item = createItem(row);
        itemColumn.setExtend(row, item);
        return item;
    }
    
    protected DefaultItem createItem(int row) {
        return new DefaultItem(row, this);
    }
    
    /**
     * @return true if the table maintains an item column
     * for accelerating and optimizing operations using
     * items.
     */
    public boolean hasItemColumn() {
        return itemColumn != null;
    }

    /**
     * Creates an item column.
     */
    public void createItemColumn() {
        if (itemColumn == null) {
            itemColumn = new ObjectColumn("#items");
        }
    }

    /**
     * Removes the item column.
     */
    public void removeItemColumn() {
        if (itemColumn != null) {
            itemColumn.clear();
            itemColumn = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addColumn(Column c) {
        add(c);
        if (hasTableModelListener()) {
            c.addChangeListener(this);
        }
        fireTableStructureChanged();
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(Column c) {
        if (columns != null)
            columns.put(c.getName(), new Integer(size()));
        super.add(c);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumnAt(int index) {
        return get(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setColumnAt(int i, Column c) {
        Column old = getColumnAt(i);
        if (old == c) {
            return;
        }
        if (hasTableModelListener()) {
            old.removeChangeListener(this);
        }
        set(i, c);
        if (hasTableModelListener()) {
            c.addChangeListener(this);
        }
        fireTableStructureChanged();
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(int index, Column c) {
        columns = null;
        super.set(index, c);
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(String name) {
        if (columns == null) {
            columns = new HashMap();
            for (int i = 0; i < size(); i++) {
                Column c = getColumnAt(i);
                if (c.getName() != null)
                    columns.put(c.getName(), new Integer(i));
            }
        }
        Integer i = (Integer)columns.get(name);
        if (i == null) return -1;
        return i.intValue();
//        for (int i = 0; i < getColumnCount(); i++) {
//            Column col = getColumnAt(i);
//            if (col.getName().equals(name))
//                return i;
//        }
//        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(Column column) {
        return indexOf(column.getName());
//        for (int i = 0; i < getColumnCount(); i++) {
//            Column col = getColumnAt(i);
//            if (col == column)
//                return i;
//        }
//        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public void disableNotify() {
        super.disableNotify();
        for (int i = 0; i < size(); i++) {
            if (! isValueUndefined(i)) {
                get(i).disableNotify();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enableNotify() {
        for (int i = 0; i < size(); i++) {
            if (! isValueUndefined(i)) {
                get(i).enableNotify();
            }
        }
        super.enableNotify();
    }
    
    /**
     * {@inheritDoc}
     */
    public Column getColumn(String name) {
        int i = indexOf(name);
        if (i == -1) return null;
        return getColumnAt(i);
//        for (int i = 0; i < getColumnCount(); i++) {
//            Column col = getColumnAt(i);
//            if (col.getName().equals(name))
//                return col;
//        }
//        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeColumn(Column c) {
        if (hasTableModelListener()) {
            c.removeChangeListener(this);
        }
        int i = indexOf(c);
        if (i == -1) return false;
        remove(i);
        fireTableStructureChanged();
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(int row) {
        columns = null;
        super.remove(row);
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new TableIterator(0, getLastRow()+1, true);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return new TableIterator(getLastRow(), -1, false);
    }

    /**
     * {@inheritDoc}
     */
    public Table getTable() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRowValid(int row) {
        return row >= 0 && row < getRowCount();
    }
    
    /**
     * Throws an exception if the row is invalid.
     * @param row the row
     * @thows IllegalArgumentException if the row is not valid
     */
    public void checkRowValid( int row ) {
    	if( isRowValid( row ) )
    		return;
    	throw new IllegalArgumentException( "DefaultTable: row " + row 
    			+ " invalid in range 0-" + getRowCount() ); 
    }


    // interface TableModel

    /**
     * {@inheritDoc}
     */
    public String getColumnName(int columnIndex) {
        String name = getColumnAt(columnIndex).getName();
        if( name == null )
        	name = String.valueOf("");
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        int max = 0;
        for (int index = 0; index < getColumnCount(); index++) {
            int r = getColumnAt(index).size();
            if (r > max)
                max = r;
        }

        return max;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getLastRow() {
        return getRowCount()-1;
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return getColumnAt(columnIndex).getValueAt(rowIndex);
    }

    /**
     *  <code>TableModel</code> method for editable tables.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     *
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            getColumnAt(columnIndex).setValueAt(rowIndex, (String)aValue);
        } catch (ParseException e) {
            ; // ignore
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class getColumnClass(int columnIndex) {
        // The values are always coerced into a String using the format.
//        return getColumnAt(columnIndex).getValueClass();
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Column col = getColumnAt(columnIndex);
        if (col.isInternal()) return false;
        return true;
    }

    // Implementation specific methods

    /**
     * Test if the column is internal, i&dot;e&dot; the first character of its name is a '#'.
     *
     * @param col the column.
     *
     * @return <code>true</code>
     *  if the column is internal, i.e. the first character of its name is a '#'.
     */
    public static boolean isColumnInternal(Column col) {
        return col.getName().charAt(0) == INTERNAL_PREFIX;
    }

    // TableModel implementation
    /**
     * Returns true if the table has any registered <code>TableModelListener</code>s.
     * @return true if the table has any registered <code>TableModelListener</code>s.
     */
    public boolean hasTableModelListener() {
        return hasTableModelListener;
    }
    
    /**
     * Returns an object from a row.
     * 
     * @param row the row
     * @return an object 
     */
    public Object getObjectFromRow(int row) {
        return new RowObject(this, row);
    }
    
    /**
     * Returns a row from an object returned by
     * getObjectFromRow.
     * 
     * @param obj the object
     * @return the row
     */
    public int getRowFromObject(Object obj) {
        return RowObject.getRow(this, obj);
    }

    protected EventListenerList getListenerList() {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        return listenerList;
    }
    
    /**
     * {@inheritDoc}
     */
    public void addTableModelListener(TableModelListener l) {
        if (!hasTableModelListener()) {
            registerColumnListeners();
        }
        hasTableModelListener = true;
        getListenerList().add(TableModelListener.class, l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTableModelListener(TableModelListener l) {
        if (! hasTableModelListener()) return;
        getListenerList().remove(TableModelListener.class, l);
        if (listenerList.getListenerCount(TableModelListener.class) == 0) {
            unregisterColumnListeners();
        }
    }

    /**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @param firstRow the first row modified or NIL
     * @param lastRow the last row modified or NIL
     * @param type the event type
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged(int firstRow, int lastRow, int type) {
        if (! hasTableModelListener()) return;
        tmpEvent.setValues(firstRow, 
                        lastRow, 
                        TableModelEvent.ALL_COLUMNS, 
                        type);
        fireTableChanged(tmpEvent);
    }
    
    /**
     * Notifies all listeners that all the table has changed.
     */
    public void fireTableDataChanged() {
        fireTableDataChanged(0, getRowCount(), UPDATE);
    }

    /**
     * Notifies all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the <code>JTable</code> receives this event and its
     * <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     */
    public void fireTableStructureChanged() {
        if (! hasTableModelListener()) return;
        tmpEvent.setValues(TableModelEvent.HEADER_ROW);
        fireTableChanged(tmpEvent);
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
        if (! hasTableModelListener()) return;
        Object[] ll = listenerList.getListenerList();
        for (int i = ll.length - 2; i >= 0; i -= 2) {
            if (ll[i]==TableModelListener.class) {
                TableModelListener l = (TableModelListener)ll[i+1];
                l.tableChanged(e);
            }
        }
    }
    
    /**
     * Called when one of the column has changed to
     * propagate the TableChanged notification.
     * 
     * @param e the change event.
     */
    public void stateChanged(ChangeEvent e) {
        if (! hasTableModelListener()) return;
        if (e.getSource() instanceof Column) {
            Column c = (Column) e.getSource();
            int col = indexOf(c);
            if (col != -1) {
                int start = 0;
                int end = 0;
                if (e instanceof ColumnChangeEvent) {
                    ColumnChangeEvent ce = (ColumnChangeEvent) e;
                    if (ce.getDetail() != null) {
                        start = ce.getDetail().nextSet(0);
                        end = ce.getDetail().length();
                    }
                }
                if (end == 0) {
                    end = c.size()-1;
                }
                tmpEvent.setValues(start, end, col, UPDATE);
                fireTableChanged(tmpEvent);
                return;
            }
        }
    }

    protected void registerColumnListeners() {
        int s = size();
        for (int i = 0; i < s; i++) {
            Column c = getColumnAt(i);
            c.addChangeListener(this);
        }
    }

    protected void unregisterColumnListeners() {
        int s = size();
        for (int i = 0; i < s; i++) {
            Column c = getColumnAt(i);
            c.removeChangeListener(this);
        }
    }
}
