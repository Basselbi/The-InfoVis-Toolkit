/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.table;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 * Class MutableTableModelEvent
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class MutableTableModelEvent extends TableModelEvent {
    /** 
     *  All row data in the table has changed, listeners should discard any state 
     *  that was based on the rows and requery the <code>TableModel</code>
     *  to get the new row count and all the appropriate values. 
     *  The <code>JTable</code> will repaint the entire visible region on
     *  receiving this event, querying the model for the cell values that are visible. 
     *  The structure of the table ie, the column names, types and order 
     *  have not changed.  
     *  @param source the table model
     */
    public MutableTableModelEvent(TableModel source) {
        // Use Integer.MAX_VALUE instead of getRowCount() in case rows were deleted. 
        this(source, 0, Integer.MAX_VALUE, ALL_COLUMNS, UPDATE);
    }
    /**
     *  The cells from (firstRow, column) to (lastRow, column) have been changed. 
     *  The <I>column</I> refers to the column index of the cell in the model's 
     *  co-ordinate system. When <I>column</I> is ALL_COLUMNS, all cells in the 
     *  specified range of rows are considered changed. 
     *  <p>
     *  @param source the table model
     *  @param firstRow the lowest row index modified
     *  @param lastRow the hight row index modified
     *  @param column the index of the modified column
     *  @param type
     *  The <I>type</I> should be one of: INSERT, UPDATE and DELETE. 
     */
    public MutableTableModelEvent(
            TableModel source,
            int firstRow,
            int lastRow,
            int column,
            int type) {
        super(source);
        setValues(firstRow, lastRow, column, type);
    }

    
    /**
     *  The cells from (firstRow, column) to (lastRow, column) have been changed. 
     *  The <I>column</I> refers to the column index of the cell in the model's 
     *  co-ordinate system. When <I>column</I> is ALL_COLUMNS, all cells in the 
     *  specified range of rows are considered changed. 
     *  <p>
     *  @param firstRow the lowest row index modified
     *  @param lastRow the hight row index modified
     *  @param column the index of the modified column
     *  @param type
     *  The <I>type</I> should be one of: INSERT, UPDATE and DELETE. 
     */
    public void setValues(int firstRow, int lastRow, int column, int type) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.column = column;
        this.type = type;
    };
    
    /**
     *  This row of data has been updated. 
     *  To denote the arrival of a completely new table with a different structure 
     *  use <code>HEADER_ROW</code> as the value for the <code>row</code>. 
     *  When the <code>JTable</code> receives this event and its
     *  <code>autoCreateColumnsFromModel</code> 
     *  flag is set it discards any TableColumns that it had and reallocates 
     *  default ones in the order they appear in the model. This is the 
     *  same as calling <code>setModel(TableModel)</code> on the <code>JTable</code>. 
     *  @param row the row
     */
    public void setValues(int row) {
        setValues(row, row, ALL_COLUMNS, UPDATE);
    }

}
