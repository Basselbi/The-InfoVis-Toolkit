/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
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

import cern.colt.list.IntArrayList;

/**
 * <b>IntArrayFormat</b> is the default format for IntArrayList.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IntArrayFormat extends Format {
    /** Singleton for this class. */
    public static final IntArrayFormat INSTANCE = new IntArrayFormat();
    
    /**
     * @return the instance.
     */
    public static IntArrayFormat getInstance() {
        return INSTANCE;
    }
    
    static int nextBreak(String str, int index) {
        for (;index < str.length(); index++) {
            char c = str.charAt(index);
            if (! Character.isDigit(c)) {
                return index;
            }
        }
        return str.length();
    }
    
    static int skipBreak(String str, int index) {
        for (;index < str.length(); index++) {
            char c = str.charAt(index);
            if (Character.isDigit(c)) {
                return index;
            }
        }
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        IntArrayList list = new IntArrayList();
        if (source.length()==0) return list;
        try {
            int start = 0;
            for (int end = nextBreak(source, 0); 
                start != -1; 
                start = skipBreak(source, end), 
                end = nextBreak(source, start+1)) {
                int v = Integer.parseInt(source.substring(start, end));
                list.add(v);
                pos.setIndex(end);
            }
        }
        catch(NumberFormatException e) {
            ; // just return
        }
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        IntArrayList array = (IntArrayList)obj;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.size(); i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(array.get(i));
        }
        
        toAppendTo.append(sb.toString());
        return toAppendTo;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }
}