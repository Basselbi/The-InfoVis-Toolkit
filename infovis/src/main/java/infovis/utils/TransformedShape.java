/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;

/**
 * Class TransformedShape
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * TODO: Test functions
 */
public class TransformedShape implements Shape {
    protected Shape shape;
    protected AffineTransform transform;
    protected AffineTransform inverse;
    
    /**
     * Creates a transformed shape.
     * @param shape the original shape
     * @param transform the transformation to apply
     */
    public TransformedShape(Shape shape, AffineTransform transform) {
        this.shape = shape;
        this.transform = transform;
    }
    
    /**
     * @return the inverse transformation
     */
    public AffineTransform getInverse() {
        if (inverse == null) {
            try {
                inverse = transform.createInverse();
            }
            catch(NoninvertibleTransformException e) {
                inverse = new AffineTransform();
            }
        }
        return inverse;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(double x, double y) {
        Point2D pt = new Point2D.Double(x, y);
        getInverse().transform(pt, pt);
        return shape.contains(pt);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(double x, double y, double w, double h) {
        Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
        Shape t = getInverse().createTransformedShape(rect);
        return shape.contains(t.getBounds2D());
    }

    /**
     * {@inheritDoc}
     */
    public boolean intersects(double x, double y, double w, double h) {
        Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
        Shape t = getInverse().createTransformedShape(rect);
        return shape.intersects(t.getBounds2D());
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle2D getBounds2D() {
        Rectangle2D rect = shape.getBounds2D();
        Shape t = transform.createTransformedShape(rect);
        return t.getBounds2D();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public boolean intersects(Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public PathIterator getPathIterator(AffineTransform at) {
        if (at == null) {
            return shape.getPathIterator(transform);
        }
        AffineTransform t = new AffineTransform(at);
        t.concatenate(transform);
        return shape.getPathIterator(t);
    }

    /**
     * {@inheritDoc}
     */
    public PathIterator getPathIterator(AffineTransform at,
            double flatness) {
        if (at == null) {
            return shape.getPathIterator(transform, flatness);
        }
        AffineTransform t = new AffineTransform(at);
        t.concatenate(transform);
        return shape.getPathIterator(t, flatness);
    }
    
    /**
     * @return the associated shape
     */
    public Shape getShape() {
        return shape;
    }
    
    /**
     * Sets the associated shape
     * @param shape the new shape
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }
    
    /**
     * @return the transform
     */
    public AffineTransform getTransform() {
        return transform;
    }
    
    /**
     * Sets the transform.
     * @param transform the new transform
     */
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
        this.inverse = null;
    }
}
