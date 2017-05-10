/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

/**
 * Class Boolean
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public final class Logical {
    public static final Boolean TRUE = Boolean.TRUE;
    public static final Boolean FALSE = Boolean.FALSE;
    public static final Boolean UNCERTAIN = null;
    
    private Logical() { }
    
    public static Boolean valueOf(boolean b) {
        return Boolean.valueOf(b);
    }

    public static String toString(boolean b) {
        return Boolean.toString(b);
    }
    
    public static Boolean not(Boolean b) {
        if (b == TRUE) {
            return FALSE;
        }
        else if (b == FALSE) {
            return TRUE;
        }
        return UNCERTAIN;
    }
    
    public static Boolean and(Boolean a, Boolean b) {
        if (a == FALSE || b == FALSE) return FALSE;
        if (a == UNCERTAIN || b == UNCERTAIN) return UNCERTAIN;
        return TRUE;
    }
    
    public static Boolean or(Boolean a, Boolean b) {
        if (a == TRUE || b == TRUE)  return TRUE;
        if (a == UNCERTAIN || b == UNCERTAIN) return UNCERTAIN;
        return FALSE;
    }

    public static Boolean xor(Boolean a, Boolean b) {
        if (a == UNCERTAIN || b == UNCERTAIN) return UNCERTAIN;
        if (a == b) return FALSE;
        return TRUE;
    }
    
    private static Boolean toBoolean(String name) { 
        if (name == null
                || name.equalsIgnoreCase("uncertain") 
                || name.equalsIgnoreCase("null")) {
            return UNCERTAIN;
        }
        return valueOf(name.equalsIgnoreCase("true"));
    }
}
