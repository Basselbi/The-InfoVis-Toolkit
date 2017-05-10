/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import java.util.Iterator;


/**
 * <b>DoubleSet</b> is a set of double values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface ValueSet {
    boolean isEmpty();
    int size();
    boolean contains(Object v);
    void add(Object v);
    void add(ValueSet s);
    Iterator<Object> iterator();
}
