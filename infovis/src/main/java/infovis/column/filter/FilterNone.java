/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.filter;

import infovis.Column;
import infovis.column.ColumnFilter;

/**
 * <b>FilterNone</b> is a convenience class for not filtering
 * anything.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class FilterNone implements ColumnFilter {

    private static final FilterNone INSTANCE = new FilterNone();
    
    /**
     * @return an instance of this class
     */
    public static FilterNone getSharedInstance() { 
        return INSTANCE;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean filter(Column column) {
        return false;
    }

}
