/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.column.filter.NotNumberFilter;
import infovis.utils.TransformedShape;
import infovis.visualization.ItemRenderer;
import infovis.visualization.Orientable;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualArea
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class VisualArea extends AbstractVisualColumn 
    implements Orientable {
    /** Name of the visual. */
    public static final String VISUAL = "area";

    protected NumberColumn areaColumn;
    protected short orientation = ORIENTATION_NORTH;
    protected double defaultScale = 1;
    protected transient double scale;
    protected transient double origin;
    protected boolean fixed = false;
    protected transient AffineTransform TMP_TRANS = new AffineTransform();
    protected transient TransformedShape TMP_SHAPE;
    protected transient Rectangle2D.Double TMP_RECT = new Rectangle2D.Double();

    /**
     * Returns the VisualArea associated with a specified visualization.
     * @param vis the visualization
     * @return the VisualArea associated with a specified visualization
     * or <code>null</code> if it does not exist
     */
    public static VisualArea get(Visualization vis) {
        return (VisualArea) findNamed(VISUAL, vis);
    }

    /**
     * Returns the VisualArea associated with a specified ItemRenderer.
     * @param ir the ItemRenderer
     * @return the VisualArea associated with a specified ItemRenderer
     * or <code>null</code> if it does not exist
     */
    public static VisualArea get(ItemRenderer ir) {
        return (VisualArea) findNamed(VISUAL, ir);
    }
    
    /**
     * Creates a visual area with the specified name.
     * @param name the name
     */
    public VisualArea(String name) {
        super(name);
    }

    /**
     * Creates a visual area with the specified child
     * @param child the child
     */
    public VisualArea(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
        this.filter = NotNumberFilter.sharedInstance();
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return areaColumn;
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (areaColumn == column)
            return;
        super.setColumn(column);
        areaColumn = (NumberColumn) column;
        invalidate();
    }
    
    /**
     * @return the fixed
     */
    public boolean isFixed() {
        return fixed;
    }
    
    /**
     * @param fixed the fixed to set
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    /**
     * @return the scale
     */
    public double getScale() {
        return scale;
    }
    
    /**
     * @return the origin
     */
    public double getOrigin() {
        return origin;
    }
    
    /**
     * Fix the current limits of the visual area.
     * @param min
     * @param max
     */
    public void setLimits(double min, double max) {
        assert(min<=max);
        setFixed(true);
        origin = min;
        if (min == max) {
            scale = 1;
        }
        else {
            scale = 1 / (max - min); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(short orientation) {
        if (this.orientation == orientation)
            return;
        this.orientation = orientation;
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return orientation;
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        super.install(graphics);
        if (fixed) return;
        if (areaColumn == null
                || areaColumn.getMaxIndex() == areaColumn.getMinIndex()) {
            scale = 0;
            origin = 0;
        } else {
            origin = areaColumn.getDoubleMin();
            double max = areaColumn.getDoubleMax();
            scale = 1.0 / (max - origin);
        }
    }

    /**
     * Returns the scale for the specified row.
     * @param row the row
     * @return the scale for the row
     */
    public double getScaleAt(int row) {
        if (scale == 0 
                || areaColumn == null 
                || areaColumn.isValueUndefined(row)) {
            return defaultScale;
        }
        return scale * (areaColumn.getDoubleAt(row) - origin);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        double s = getScaleAt(row);
        if (s == 1) {
            super.paint(graphics, row, shape);
            return;
        }
        Rectangle2D bounds = shape.getBounds2D();
        double tx, ty;
        double sx, sy;
        switch (orientation) {
        default:
        case ORIENTATION_CENTER:
            tx = bounds.getCenterX();
            ty = bounds.getCenterY();
            sx = sy = s;
            break;
        case ORIENTATION_EAST:
            tx = bounds.getMinX();
            ty = bounds.getCenterY();
            sx = s;
            sy = 1;
            break;
        case ORIENTATION_WEST:
            tx = bounds.getMaxX();
            ty = bounds.getCenterY();
            sx = s;
            sy = 1;
            break;
        case ORIENTATION_NORTH:
            tx = bounds.getCenterX();
            ty = bounds.getMaxY();
            sx = 1;
            sy = s;
            break;
        case ORIENTATION_SOUTH:
            tx = bounds.getCenterX();
            ty = bounds.getMinY();
            sx = 1;
            sy = s;
            break;
        }
        TMP_TRANS.setToIdentity();
        TMP_TRANS.translate(tx, ty);
        TMP_TRANS.scale(sx, sy);
        TMP_TRANS.translate(-tx, -ty);
        if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            TMP_RECT.width = sx * rect.getWidth();
            TMP_RECT.height = sy * rect.getHeight();
            TMP_RECT.x = sx * rect.getX() + TMP_TRANS.getTranslateX();
            TMP_RECT.y = sy * rect.getY() + TMP_TRANS.getTranslateY();
            super.paint(graphics, row, TMP_RECT);
        }
        else {
            if (TMP_SHAPE == null) {
                TMP_SHAPE = new TransformedShape(shape, TMP_TRANS);
            }
            else {
                TMP_SHAPE.setShape(shape);
            }
            super.paint(graphics, row, TMP_SHAPE);
        }
    }

    /**
     * @return the default scale
     */
    public double getDefaultScale() {
        return defaultScale;
    }
    
    /**
     * Sets the default scale.
     * @param s the new scale
     */
    public void setDefaultScale(double s) {
        if (s < 0 || s > 1) return;
        if (s == defaultScale) return;
        defaultScale = s;
        invalidate();
    }
}