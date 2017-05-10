/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Format for Unix Time Dates.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class UTCDateFormat extends SimpleDateFormat {
	private static UTCDateFormat instance;

    /**
     * Returns the instance of that format.
     * @return the instance of that format
     */
	public static UTCDateFormat getSharedInstance() {
		if (instance == null) {
			instance = new UTCDateFormat();
		}
		return instance;
	}

    /**
     * Sets the instance of that format.
     * @param format the format
     */
	public static void setSharedInstance(UTCDateFormat format) {
		instance = format;
	}

	/**
	 * Constructor for UTCDateFormat.
	 */
	public UTCDateFormat() {
		super("dd MM yyyy HH:mm:ss", Locale.US);
	}

    /**
     * Constructor for UTCDateFormat.
     * @param format the simple format
     */
    public UTCDateFormat(String format) {
        super(format, Locale.US);
    }

    /**
     * {@inheritDoc}
	 */
	public Object parseObject(String source, ParsePosition pos) {
        Date date = parse(source, pos);
        if (date == null) {
            return null;
        }
		return new Long(date.getTime());
    }
    
//    private Object readResolve() throws ObjectStreamException {
//        return getSharedInstance();
//    }
}
