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
import infovis.metadata.IO;

/**
 * <b>IOColumnFilter</b> filters out column marked as not for IO.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IOColumnFilter implements ColumnFilter {
    static IOColumnFilter instance = new IOColumnFilter();
    
    /**
     * Avoid creating several instances since we need only one.
     *
     * @return the shared instance.
     */
    public static IOColumnFilter sharedInstance() {
        return instance;
    }
    /**
     * {@inheritDoc}
     */
    public boolean filter(Column column) {
        return column.getMetadata().getAttribute(IO.IO_TRANSIENT) == Boolean.TRUE;
    }

}
