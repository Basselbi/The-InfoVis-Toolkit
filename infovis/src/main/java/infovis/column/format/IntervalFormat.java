/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.format;

import infovis.data.DoubleInterval;
import infovis.data.Interval;

import java.io.ObjectStreamException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * <b>IntervalFormat</b> is a format for Intervals.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class IntervalFormat extends Format {
    private static IntervalFormat instance;
    protected String separator;
    
    /**
     * Returns the instance of that format.
     * @return the instance of that format.
     */
    public static IntervalFormat getInstance() {
        if (instance == null) {
            instance = new IntervalFormat();
        }
        return instance;
    }
    
    /**
     * Creates an InveralFormat with the default ":" character as
     * separator.
     */
    public IntervalFormat() {
        this(":");
    }
    
    /**
     * Creates an IntervalFormat with the specified string as separator
     * between the min and max values.
     * @param separator
     */
    public IntervalFormat(String separator) {
        this.separator = separator;
    }

    /**
     * Sets the instance of that format.
     * @param format the format
     */
    public static void setInstance(IntervalFormat format) {
        instance = format;
    }
        
    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        if (! (obj instanceof Interval)) {
            return null;            
        }
        Interval inter = (Interval) obj;
        pos.setBeginIndex(toAppendTo.length());
        toAppendTo.append(Double.toString(inter.getMin()));
        toAppendTo.append(separator);
        toAppendTo.append(Double.toString(inter.getMax()));
        pos.setEndIndex(toAppendTo.length());
        return toAppendTo;
    }

    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        int index = pos.getIndex();
        if (index != 0) {
            source = source.substring(index);
        }
        int colonIndex = source.indexOf(separator);
        if (colonIndex == -1) {
            double v = Double.parseDouble(source);
            return new DoubleInterval(v, v);
        }
        String minStr = source.substring(0, colonIndex);
        String maxStr = source.substring(colonIndex+1);
        Interval ret = new DoubleInterval(
                Double.parseDouble(minStr), 
                Double.parseDouble(maxStr));
        pos.setIndex(source.length());
        return ret;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }
}
