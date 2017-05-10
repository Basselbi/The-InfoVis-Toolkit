/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

import java.awt.Shape;

/**
 * <b>ShapeFunction</b> is an interface for function on a
 * shape.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface ShapeFunction {
    /**
     * Apply the function on the specified shape and return the result.
     * @param s the shape
     * @return the modified shape
     */
    Shape apply(Shape s);
}
