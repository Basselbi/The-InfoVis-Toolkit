/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.table;

import infovis.Column;
import infovis.Table;

/**
 * <b>Item</b> is an abstraction for the index
 * of a row.  It is invalidated when the row is
 * removed.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface Item {
    /** 
     * @return the index of the Item or -1 if
     * the item is invalid
     */
    int getId();
    
    /**
     * @return the specific Column this item refers to 
     * or <code>null</code> if the whole column is concerned.
     *  
     */
    Column getColumn();
    
    /**
     * @return the associated table
     */
    Table getTable();
}
