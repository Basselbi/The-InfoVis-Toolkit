/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import infovis.utils.RowIterator;

/**
 * Class SymbolicValue
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface SymbolicValue {
    void aggregate(SymbolicValue s);
    boolean isNumber();
    double doubleValue();
    boolean isCategorical();
    RowIterator categories();
}
