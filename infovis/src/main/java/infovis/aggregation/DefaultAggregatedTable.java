/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.aggregation;

import infovis.Table;
import infovis.column.AbstractIntColumn;
import infovis.column.IntArrayColumn;
import infovis.column.IntColumn;
import infovis.table.DefaultDynamicTable;
import infovis.utils.RowIterator;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import cern.colt.list.IntArrayList;

/**
 * <b>DefaultAggregatedTable</b> implements an aggregated table that
 * stores all its refereing items and sub items.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultAggregatedTable extends DefaultDynamicTable 
    implements AggregatedTable, TableModelListener {
    protected Table relatedTable;
    protected IntArrayColumn relatedItemsColumn;
    protected IntArrayColumn subItemsColumn;
    protected AbstractIntColumn levelColumn;
    protected IntArrayColumn superItemsColumn;
    
    /**
     * Creates an instance with a specified related table.
     * @param relatedTable the related table
     */
    public DefaultAggregatedTable(Table relatedTable) {
        super(relatedTable.size());
        this.relatedTable = relatedTable;
        createColumns();
    }

    protected void createColumns() {
        relatedItemsColumn = new IntArrayColumn("#relatedItems");
        addColumn(relatedItemsColumn);
        subItemsColumn = new IntArrayColumn("#subItems");
        addColumn(subItemsColumn);
        superItemsColumn = new IntArrayColumn("#superItems");
        addColumn(superItemsColumn);
        levelColumn = new IntColumn("#level");
        add(levelColumn);
    }
    
    

    /**
     * {@inheritDoc}
     */
    public IntArrayList getRelatedItems(int row, IntArrayList set) {
        IntArrayList ret = relatedItemsColumn.get(row);
        if (ret == null) {
            set = null;
        }
        else if (set == null) {
            set = ret.copy();
        }
        else {
            set.clear();
            set.addAllOf(ret);
        }
        return set;
    }
    
    /**
     * {@inheritDoc}
     */
    public IntArrayList getAggregatedItems(int relatedRow, IntArrayList set) {
        if (set == null) {
            set = new IntArrayList();
        }
        for (RowIterator iter = iterator(0); iter.hasNext(); ) {
            
        }
        // TODO Auto-generated method stub
        return set;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getAggregatedItem(int relatedRow) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasMultipleAggregatedItems() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasMultipleSuperItems() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Table getRelatedTable() {
        return relatedTable;
    }

    /**
     * {@inheritDoc}
     */
    public IntArrayList getSubItems(int row, IntArrayList set) {
        IntArrayList ret = subItemsColumn.get(row);
        if (ret == null) {
            set = null;
        }
        else if (set == null) {
            set = ret.copy();
        }
        else {
            set.addAllOf(ret);
        }
        return set;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getItemLevel(int row) {
        if (levelColumn == null 
                || levelColumn.isValueUndefined(row)) {
            return 0;
        }
        return levelColumn.get(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public IntArrayList getSuperItems(int row, IntArrayList set) {
        if (set == null) {
            set = superItemsColumn.get(row).copy(); 
        }
        else {
            set.addAllOf(superItemsColumn.get(row));
        }
        return set;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSuperItem(int row) {
        return superItemsColumn.get(row).get(0);
    }
        
    
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator(int level) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void tableChanged(TableModelEvent e) {
        if (e.getSource()!=relatedTable) return;
        
        if (e.getType()==TableModelEvent.UPDATE) {
            
        }
    }

}
