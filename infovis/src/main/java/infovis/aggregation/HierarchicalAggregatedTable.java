/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.aggregation;

import infovis.utils.RowIterator;

/**
 * <b>HierarchicalAggregatedTable</b> is for aggregated tables
 * that are purely hierarchical: so each item can have at most one
 * item above it.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface HierarchicalAggregatedTable extends AggregatedTable {
    /**
     * Return the item that aggregates the specified row.
     * @param row the row
     * @return the super row or -1
     */
    int getSuperItem(int row);
    
    /**
     * Returns the aggregated item relating to a
     * specified row in the related table.
     * @param relatedItem the row in the related table
     * @return the aggregated item
     */
    int getAggregatedItem(int relatedItem);
    
    /**
     * Returns the aggregation level for the specified row.
     * 
     * <p>Levels start at 0 up.  
     * <code>getItemLevel(row)==0</code> implies <code>getSubItems(row)==null</code>.
     * <code>getSubItems(row)!=null</code>implies <code>getItemLevel(row)!=0</code>. 
     * @param row the row
     * @return the level from 0 to a maximum.
     */
    int getItemLevel(int row);
    
    /**
     * Iterator over all the rows of the specified level.
     * @param level	the level of the zoomable index to be iterated over
     * @return		an iterator, or null if no row exists at that level
     */
    RowIterator iterator(int level);
}
