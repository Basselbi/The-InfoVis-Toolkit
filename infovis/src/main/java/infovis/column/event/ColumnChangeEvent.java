/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.event;

import infovis.Column;
import infovis.utils.IntSet;

import javax.swing.event.ChangeEvent;

/**
 * Event produced by columns containing a detail about their
 * change. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class ColumnChangeEvent extends ChangeEvent {
    /** Value returned by getDetail when the column size has changed. */
    public static final int CHANGE_SIZE = -3;
    /** Value returned by getDetail when the whole column has changed. */
    public static final int CHANGE_ALL = -2;
    /** Value returned by getDetail when no value has changed. */
    public static final int CHANGE_NONE = -1;
    private IntSet detail;
   
    /**
     * Constructor.
     * @param column the column
     * @param detail the detail.
     */
    public ColumnChangeEvent(Column column, IntSet detail) {
        super(column);
        this.detail = detail;
    }
    
    /**
     * Constructor.
     * @param column the column.
     */
    public ColumnChangeEvent(Column column) {
        this(column, null);
    }

    /**
     * Returns the detail of changes.
     * @return the detail of changes.
     */
    public IntSet getDetail() {
        return detail;
    }
}
