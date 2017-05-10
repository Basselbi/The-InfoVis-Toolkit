/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import infovis.DynamicTable;
import infovis.utils.IdManager;
import infovis.utils.RowIterator;
import infovis.utils.IdManager.IdManagerIterator;

import javax.swing.event.TableModelEvent;

import org.apache.log4j.Logger;

/**
 * Default implementatio for dynamic tables.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.19 $
 */
public class DefaultDynamicTable extends DefaultTable
    implements DynamicTable {
    private static final long serialVersionUID = -3366313798343895996L;
    protected IdManager idManager;
    protected volatile MutableTableModelEvent TMP_event 
        = new MutableTableModelEvent(this);
    private static final Logger LOG = Logger.getLogger(DefaultDynamicTable.class);
    
    /**
     * Creates a DefaultDynamicTable.
     *
     */
    public DefaultDynamicTable() {
        idManager = new IdManager();
    }

    /**
     * Constructor with a given allocated rowCount.
     * @param size the number of rows already allocated.
     */
    public DefaultDynamicTable(int size) {
        idManager = new IdManager(size);
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        idManager.clear();
        super.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return idManager.getIdCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getLastRow() {
        return idManager.getMaxId();
    }
    
    class Iterator extends IdManagerIterator {
        /**
         * Creates an iterator with a specified direction.
         * @param up true if iterating up
         */
        public Iterator(boolean up) {
            super(DefaultDynamicTable.this.idManager, up);
        }
        /**
         * {@inheritDoc}
         */
        public void remove() {
            removeRow(last);
        }
        
        /**
         * {@inheritDoc}
         */
        public Object next() {
            return getItem(nextRow());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new Iterator(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return new Iterator(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRowValid(int row) {
        return row >= 0 && !idManager.isFree(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public void checkRowValid( int row ) {
    	if( isRowValid( row ) )
    		return;
    	String err = "DefaultDynamicTable: row " + row 
        + " invalid in range " + idManager.getMinId() 
        + "-" + idManager.getMaxId() + " inclusive";
    	LOG.error(err);
    	throw new IllegalArgumentException(err); 
    }
    

    /**
     * Adds the specified row
     * @param row the row to add
     * @return true if the row has been successfully added
     */
    public boolean addRow(int row) {
        int size = getRowCount();
        if(!idManager.isFree(row))
            return false;
        else
        {
            idManager.remove(row);
            if (hasTableModelListener()) {
                int op;
                if (row == size)
                    op = TableModelEvent.INSERT;
                else
                    op = TableModelEvent.UPDATE;
                TMP_event.setValues(row, row, TableModelEvent.ALL_COLUMNS, op);
                fireTableChanged(TMP_event);
            }
            return true;
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    public int addRow() {
        int size = getRowCount();
        int row = idManager.newId();
        if (hasTableModelListener()) {
            int op;
            if (row == size)
                op = TableModelEvent.INSERT;
            else
                op = TableModelEvent.UPDATE;
            TMP_event.setValues(row, row, TableModelEvent.ALL_COLUMNS, op);
            fireTableChanged(TMP_event);
        }
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeRow(int row) {
        if (! isRowValid(row)) {
            //throw new RuntimeException("Row already removed");
            //LOG.warn("Row "+row+" already removed");
            return;
        }
        int size = getRowCount();
        idManager.free(row);
        if (itemColumn != null 
                && !itemColumn.isValueUndefined(row)) {
//            DefaultItem item = (DefaultItem)itemColumn.get(row);
//            item.invalidate();
            itemColumn.setValueUndefined(row, true);
        }
        if (hasTableModelListener()) {
            int op;
            if (size != getRowCount()) 
                op = TableModelEvent.DELETE;
            else 
                op = TableModelEvent.UPDATE;
            tmpEvent.setValues(row,
                    row,
                    TableModelEvent.ALL_COLUMNS,
                    op);
            fireTableChanged(tmpEvent);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Item addItem() {
        return getItem(addRow());
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeItem(Item item) {
        removeRow(item.getId());
    }
}
