/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.Column;
import infovis.column.ShapeColumn;

import java.awt.geom.Rectangle2D;

import cern.colt.list.IntArrayList;

/**
 * <b>ShapePacker</b> is an interface for object used to pack
 * connected components shapes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface ShapePacker {
    /**
     * Pack the specified shapes from the specified connected components.
     * 
     * @param bounds the window bounds
     * @param shapes the shape column containing each component already laid out
     * @param comps the connected components
     * @param fixedColumn a column where defined rows are fixed vertices
     * @return the bounding box of the 
     */
    Rectangle2D.Float packShapes(
            Rectangle2D bounds,
            ShapeColumn shapes, 
            IntArrayList[] comps,
            Column fixedColumn);
}
