/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.filter;

import infovis.Column;
import infovis.column.ColumnFilter;

/**
 * <b>FilterAll</b> filters all the elements.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class FilterAll implements ColumnFilter {
    private static final FilterAll INSTANCE = new FilterAll();
    
    /**
     * @return an instance of this class
     */
    public static FilterAll getInstance() { 
        return INSTANCE;
    }
    
    protected FilterAll() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean filter(Column column) {
        return true;
    }

}
