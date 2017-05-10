/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.utils.StrokedPath;
import infovis.visualization.ItemRenderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualStrokingLabel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class VisualStrokingLabel extends DefaultVisualLabel {
    /**
     * Constructor with a child specifying whether labels are shown.
     * @param child the child
     * @param showingLabel true if the labels are shown
     */
    public VisualStrokingLabel(ItemRenderer child, boolean showingLabel) {
        super(child, showingLabel);
    }

    /**
     * Constructor using a child.
     * @param child the child
     */
    public VisualStrokingLabel(ItemRenderer child) {
        this(child, false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClipped() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape s) {
        if (!showingLabel) {
            return;
        }
        String label = getLabelAt(row);
        if (label == null) {
            return;
        }
        
        Point2D pos = StrokedPath.pointAt(s, justification, null);
        super.paint(
                graphics, 
                row, 
                new Rectangle2D.Float(
                        (float)pos.getX()-justification*1, 
                        (float)pos.getY()-justification*1, 
                        1, 
                        1));
    }

}
