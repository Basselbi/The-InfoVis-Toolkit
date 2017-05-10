/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.visualization.ItemRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import cern.colt.map.OpenIntObjectHashMap;

/**
 * Visual size for stroking visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class VisualStrokeSize extends VisualSize {
//    public static final String VISUAL = "stroke";
    /** Default minimum size (1) */
    public static double DEFAULT_MIN_SIZE = 1;
    /** Default maximum size (10) */
    public static double DEFAULT_MAX_SIZE = 10;
    /** Default default size (1) */
    public static double DEFAULT_DEFAULT_SIZE = 1;
    protected static final OpenIntObjectHashMap STROKES = new OpenIntObjectHashMap();
    
    static {
        for (int i = 0; i < 50; i++) {
            STROKES.put(i, new BasicStroke(i));
        }
    }
    
    protected transient BasicStroke stroke;
    protected transient Rectangle2D.Float pick;
    
    /**
     * Constructor with a child.
     * @param child the child
     */
    public VisualStrokeSize(ItemRenderer child) {
        this(child, DEFAULT_DEFAULT_SIZE, DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE);
    }
    
    /**
     * Constructor with a child and default values.
     * @param child the child
     * @param def the default size
     * @param min the min size
     * @param max the max size
     */
    public VisualStrokeSize(ItemRenderer child, double def, double min, double max) {
        super(VISUAL);
        defaultSize = def;
        minSize = min;
        maxSize = max;
        addRenderer(child);
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        if (sizeColumn != null) {            
            stroke = null;
        }
        else {
            stroke = new BasicStroke((float)defaultSize);
        }
        super.install(graphics);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        float w;
        if (stroke != null) {
            w = stroke.getLineWidth();
        }
        else {
            w = (float)getSizeAt(row);
        }
        if (pick == null) {
            pick = new Rectangle2D.Float();
        }
        if (w != 0) {
            pick.setRect(hitBox);
            pick.x -= w/2;
            pick.y -= w/2;
            pick.width += w;
            pick.height += w;
            hitBox = pick;
        }
        return super.pick(hitBox, row, shape);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        java.awt.Stroke saved = graphics.getStroke();
        try {
            if (stroke != null) { 
                graphics.setStroke(stroke);
            }
            else {
                int s = (int)getSizeAt(row);
                if (s < 0) return;
                BasicStroke stroke = (BasicStroke)STROKES.get(s);
                if (stroke == null) {
                    stroke = new BasicStroke(s);
                    STROKES.put(s, stroke);
                }
                graphics.setStroke(stroke);
            }
            super.paint(graphics, row, shape);
        }
        finally {
            graphics.setStroke(saved);
        }
    }
}
