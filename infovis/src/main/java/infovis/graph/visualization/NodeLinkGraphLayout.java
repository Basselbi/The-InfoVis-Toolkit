/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.column.AbstractBooleanColumn;
import infovis.graph.visualization.layout.ShapePacker;
import infovis.visualization.Layout;

import java.awt.geom.Rectangle2D;

/**
 * <b>NodeLinkGraphLayout</b> is the base interface for Node-Link
 * Graph layouts.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public interface NodeLinkGraphLayout extends Layout {
    /**
     * @return true if the layout is incremental.
     */
    boolean isIncremental();
    
    /**
     * @return Return the shape packer 
     */
    ShapePacker getShapePacker();
    
    /**
     * @return Returns the column specifiying fixed vertices
     */
    AbstractBooleanColumn getFixedColumn();
    
    /**
     * Sets the column specifying fixed vertices.
     * @param c the column
     */
    void setFixedColumn(AbstractBooleanColumn c);
    
    /**
     * Sets the shape packer.
     * @param packer the packer
     */
    void setShapePacker(ShapePacker packer);
    
    /**
     * Performs an increment of the layout.
     * @param bounds the visualization bounds
     * @param vis the visualization
     */
    void incrementLayout(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis);
    
    /**
     * @return true when the layout is finished
     */
    public abstract boolean isFinished();
}