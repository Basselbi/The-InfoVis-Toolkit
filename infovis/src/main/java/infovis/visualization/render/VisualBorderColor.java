/*****************************************************************************
 * Copyright (C) 2003-2006 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Visualization;
import infovis.visualization.ItemRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Class VisualBorderColor
 * 
 * @author Nathalie Henry
 * @version $Revision: 1.2 $
 */
public class VisualBorderColor extends VisualColor {
    /** The visual name. */
    public static final String VISUAL = "bordercolor";

    /**
     * @param child
     */
    public VisualBorderColor(ItemRenderer child) {
        super(child, Color.BLACK);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return VISUAL;
    }

    /**
     * Return the visual color in the specified visualization.
     * @param vis the visualization
     * @return the visual color in the specified visualization
     */
    public static VisualColor get(Visualization vis) {
        return (VisualColor) findNamed(VISUAL, vis);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        Color c = getColorAt(row);
        if (c == null) {
            return;
        }
//        try {
            graphics.setColor(c);
            super.paint(graphics, row, shape);
//        } finally {
//            graphics.setColor(savedColor);
//        }
    }
}
