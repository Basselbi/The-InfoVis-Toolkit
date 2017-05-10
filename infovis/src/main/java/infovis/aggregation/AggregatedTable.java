package infovis.aggregation;

import cern.colt.list.IntArrayList;
import infovis.DynamicTable;
import infovis.Table;

/**
 * 
 * <b>AggregatedTable</b> is a table that aggregates 
 * values of a related table.
 * 
 * @author Nathalie Henry and Howard Goodell and Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public interface AggregatedTable extends DynamicTable {

	/**
	 * get related table of low level elements 
	 * @return Table
	 */
	Table getRelatedTable();

	/**
	 * get related items of a specified aggregated row, 
     * <b>adding them to the specified list</b>.
	 * @param row aggregated row
	 * @param set to reuse memory space
	 * @return list of aggregated related items
	 */
	IntArrayList getRelatedItems(int row, IntArrayList set);
	
	/**
	 * Get the sub-items (list of dependent items), 
     * <b>adding them to the specified list</b>.
	 * @param row the row index 
	 * @param set IntArrayList to fill or null
	 * @return list of sub-items
	 */
	IntArrayList getSubItems(int row, IntArrayList set);
    
    /**
     * Get the super-items,
     * <b>adding them to the specified list</b>.
     * @param row the row index 
     * @param set IntArrayList to fill or null
     * @return list of super-items
     */
    IntArrayList getSuperItems(int row, IntArrayList set);
    
    /**
     * Returns the list of aggregated items relating to a
     * specified row in the related table.
     * @param relatedRow the row in the related table
     * @param set the set to fill
     * @return the list of aggregated items
     */
    IntArrayList getAggregatedItems(int relatedRow, IntArrayList set);
    
}
