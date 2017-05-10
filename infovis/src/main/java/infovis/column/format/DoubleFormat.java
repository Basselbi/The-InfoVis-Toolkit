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
 * Class DoubleFormat
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class DoubleFormat extends Format {
    /** Instance of the format. */
    public static final Format INSTANCE = new DoubleFormat();

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

    /**
     * Constructor.
     * @param pattern the pattern
     * @param symbols the symbol list
     */
    public DoubleFormat() {
    }
    
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        Number num = (Number) obj;
        double d = num.doubleValue();
        if (d == (int)d) {
            toAppendTo.append(Integer.toString((int)d));
        }
        else {
            toAppendTo.append(Double.toString(d));
        }
        return toAppendTo;
    }

    /**
     * {@inheritDoc}
     */
    public Object parseObject(String s, ParsePosition pos) {
        int i = pos.getIndex();
        int sign = 1;
        double r = 0; // integer part
        double p = 1; // exponent of fractional part
        int state = 0; // 0 = int part, 1 = frac part
        boolean gotIt = false;

//        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        if (i < s.length() && s.charAt(i) == '-') { sign = -1; i++; }
        else if (i < s.length() && s.charAt(i) == '+') { i++; }
        while (i < s.length())
        {
           char ch = s.charAt(i);
           if ('0' <= ch && ch <= '9')
           {
               gotIt = true;
              if (state == 0)
                 r = r * 10 + ch - '0';
              else if (state == 1)
              {
                 p = p / 10;
                 r = r + p * (ch - '0');
              }
           }
           else if (ch == '.')
           {
              if (state == 0) state = 1;
              else {
                  pos.setErrorIndex(i);
                  pos.setIndex(i);
                  return new Double(sign * r);
              }
           }
           else if (ch == 'e' || ch == 'E')
           {
              long e = (int)Integer.parseInt(s.substring(i + 1));
              pos.setIndex(s.length());
              return new Double(sign * r * Math.pow(10, e));
           }
           else {
               pos.setIndex(i);
               pos.setErrorIndex(i);
               return new Double(sign * r);
           }
           i++;
           
        }
        pos.setIndex(i);
        if (! gotIt) {
            pos.setErrorIndex(i);
        }
        return new Double(sign * r);        
    }
}
