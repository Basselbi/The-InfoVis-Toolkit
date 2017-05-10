/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;


import infovis.Visualization;
import infovis.column.ShapeColumn;

import java.awt.Shape;

/**
 * 
 * <b>LinkShaper</b> Computes the shape of a link in a LinkVisualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */

public interface LinkShaper {
    /**
     * Initialize the LinkShaper with the specified visualization and
     * shape column.
     * @param vis the visualization
     * @param shapes the shape column
     */
    public void init(Visualization vis, ShapeColumn shapes);
    /**
     * Computes and return the shape of the specified link using the
     * specified node accessor. 
     * @param link the link
     * @param accessor the node accessor
     * @param prevLinkShape the previous value of the link shape that
     * can be reused
     * @return the computed shape
     */
    public Shape computeLinkShape(int link, NodeAccessor accessor, Shape prevLinkShape);
    /**
     * @return the name of this LinkShaper
     */
    public String getName();
}