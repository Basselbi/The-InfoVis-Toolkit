/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.format;

import java.io.ObjectStreamException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Class StringFormat
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class StringFormat extends Format {
    /**
     * Instance of the format.
     */
    public static final StringFormat INSTANCE = new StringFormat();
    
    /**
     * 
     */
    protected StringFormat() {
    }

    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        toAppendTo.insert(pos.getBeginIndex(), obj.toString());
        return toAppendTo;
    }

    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        String ret; 
        if (pos.getIndex() == 0) {
            ret = source;
        }
        else {
            ret = source.substring(pos.getIndex());
        }
        pos.setIndex(source.length()+1);
        return ret;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

}
