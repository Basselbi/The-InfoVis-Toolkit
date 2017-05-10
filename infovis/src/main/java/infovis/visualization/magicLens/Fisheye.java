/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.visualization.MagicLens;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * <b>Fisheye</b> is the interface that concrete fisheyes should
 * implement.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface Fisheye extends MagicLens {
    /**
     * Property focus height. 
     */
    String PROPERTY_FOCUS_HEIGHT = "focusHeight";
    /**
     * Transforms the specified shape, returning its deformation
     * through the Fisheye.
     *
     * @param s the initial shape
     *
     * @return the transformed shape
     */
    Shape transform(Shape s);

    /**
     * Transforms the specified point.
     * @param pt the point
     * @return the transformed point
     */
    Point2D transform(Point2D pt);
    
    /**
     * Transforms a specified point.
     * @param src the point to transform
     * @param dst the point where the result is stored
     */
    void transform(Point2D src, Point2D dst);
    
    /**
     * Transforms an array of coordinates.
     * @param coords the array
     * @param npoints the number of points to transform
     */
    void transform(float[] coords, int npoints);

    /**
     * Returns the height of the focus area.
     * 
     * @return the height of the focus area
     */
    float getFocusHeight();
    
    /**
     * Sets the height of the focus area.
     * 
     * @param h the height of the focus area.
     */
    void setFocusHeight(float h);
}
