/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.format;

import java.io.ObjectStreamException;
import java.text.DecimalFormat;

/**
 * Class IntFormat
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class IntFormat extends DecimalFormat {
    /**
     * Instance of the format.
     */
    public static final IntFormat INSTANCE = new IntFormat();
    
    
    protected IntFormat() {
        super("#");
        setParseIntegerOnly(true);
    }
    
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

}
