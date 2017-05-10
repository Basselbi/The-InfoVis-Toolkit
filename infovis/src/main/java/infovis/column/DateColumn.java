/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.util.Date;

import infovis.column.format.UTCDateFormat;

/**
 * Specialization of <code>LongColumn</code> to manage dates.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 * @infovis.factory ColumnFactory "date" DENSE
 */
public class DateColumn extends LongColumn {
    private static final long serialVersionUID = 6548320832980534388L;


    /**
     * Creates a DateColumn with a specified name.
     * @param name the name
     */
    public DateColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a DateColumn with a specified name and reserved size.
     * @param name the name
     * @param reserve the reserved size
     */
    public DateColumn(String name, int reserve) {
        super(name, reserve);
        setFormat(UTCDateFormat.getSharedInstance());
    }


    /**
     * Creates a DateColumn with a specified name and reserved size.
     * @param name the name
     * @param format the format string
     */
    public DateColumn(String name, String format) {
        super(name);
        setFormat(new UTCDateFormat(format));
    }
    

    
    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object o) {
        if (o == null || o instanceof Number) {
            super.setObjectAt(index, o);
        }
        else if (o instanceof Date) {
            Date d = (Date)o;
            set(index, d.getTime());
        }
    }
}
