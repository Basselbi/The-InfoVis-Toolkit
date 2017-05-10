/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.utils.CompositeShape;
import infovis.utils.RectPool;
import infovis.utils.RowIterator;
import infovis.utils.ShapeFunction;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

/**
 * Columns of shapes and RectPool management.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class ShapeColumn extends BasicObjectColumn {
    private static final long serialVersionUID = -59941094925212610L;
    protected Rectangle2D.Float bbox;

    /**
     * Constructor.
     * @param name the column name.
     */
    public ShapeColumn(String name) {
        super(name);
    }
    
    /**
     * Constructor.
     * @param name the column name.
     * @param reserve the reserved size.
     */
    public ShapeColumn(String name, int reserve) {
        super(name, reserve);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        try {
            disableNotify();
            for (RowIterator iter = iterator(); iter.hasNext();) {
                freeRect(iter.nextRow());
            }
            super.clear();
        } finally {
            enableNotify();
        }
    }

    protected void updateMinMax() {
        if (min_max_updated) {
            return;
        }

        minIndex = -1;
        maxIndex = -1;

        if (bbox == null) {
            bbox = RectPool.allocateRect();
        }
        else {
            bbox.setRect(0, 0, 0, 0);
        }
        
        for (int i = 0; i < size(); i++) {
            if (!isValueUndefined(i)) {
                Rectangle2D b = get(i).getBounds2D();

                if (minIndex == -1) {
                    bbox.setRect(b);
                    minIndex = i;
                }
                else if (b.getX() < bbox.x) {
                    minIndex = i;
                }

                if (maxIndex == -1 || b.getMaxX() > bbox.getMaxX()) {
                    maxIndex = i;
                }
                bbox.add(b);
            }
        }
        min_max_updated = true;
    }

    /**
     * Returns the shape at the specified index or null.
     * @param index the index
     * @return the shape at the specified index or null.
     */
    public Shape get(int index) {
        return (Shape) getObjectAt(index);
    }

    /**
     * Sets the shape at the specified index.
     * @param index the index
     * @param s the shape.
     */
    public void set(int index, Shape s) {
        setObjectAt(index, s);
    }

    /**
     * Adds a shape.
     * @param s the shape
     */
    public void add(Shape s) {
        super.add(s);
    }

    /**
     * Returns a rectangle at the specified index or
     * null.
     * 
     * <p>Assumes the shapeColumn contains a rect
     * and not another kind of shape at this index,
     * otherwise, a ClassCastException is thrown.
     * 
     * @param index the index
     * @return a rectangle at the specified index
     * or null.
     */
    public Rectangle2D.Float getRect(int index) {
        return (Rectangle2D.Float) getObjectAt(index);
    }

    /**
     * Returns a rectangle at the specified index, 
     * allocating it if the index is undefined.
     * @param index the index
     * @return a rectangle at the specified index, 
     * allocating it if the index is undefined.
     */
    public Rectangle2D.Float findRect(int index) {
        Rectangle2D.Float r = getRect(index);
        if (r == null) {
            r = RectPool.allocateRect();
            setExtend(index, r);
        }
        return r;
    }

    /**
     * Frees the shape at that index,
     * returning rectangles to the RectPool.
     *
     * @param index the index
     */
    public void freeRect(int index) {
        if (isValueUndefined(index)) return;
        Shape s = get(index);
        if (s instanceof Rectangle2D.Float) {
            RectPool.freeRect(s);
        }
        else if (s instanceof CompositeShape) {
            CompositeShape cs = (CompositeShape) s;
            for (int i = 0; i < cs.getShapeCount(); i++) {
                RectPool.freeRect(cs.getShape(i));
            }
        }

        set(index, null);
    }

    /**
     * Returns the ShapeColumn at the specified index in the
     * table.
     * 
     * @param t the table.
     * @param index the index
     * @return the ShapeColumn at the specified index in the
     * table.
     */
    public static ShapeColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof ShapeColumn) {
            return (ShapeColumn) c;
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns the ShapeColumn of the specified name in the
     * table.
     * 
     * @param t the table.
     * @param name the name
     * @return the ShapeColumn of the specified name in the
     * table.
     */
    public static ShapeColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof ShapeColumn) {
            return (ShapeColumn) c;
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns the ShapeColumn of the specified name in the
     * table, creating it if needed.
     * 
     * @param t the table.
     * @param name the name
     * @return the ShapeColumn of the specified name in the
     * table, creating it if needed.
     */
    public static ShapeColumn findColumn(Table t, String name) {
        ShapeColumn c = getColumn(t, name);
        if (c == null) {
            c = new ShapeColumn(name);
            t.addColumn(c);
        }
        return c;
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return RectPool.allocateRect();
    }

    /**
     * Returns the bounds if the shapes
     * contained in this column.
     * @return the bounds if the shapes
     * contained in this column.
     */
    public Rectangle2D.Float getBounds() {
        updateMinMax();
        return RectPool.copyRect(bbox);
    }
    
    /**
     * Returns the bounding box of the set of items 
     * enumerated by the specified iterator.
     * @param iter the iterator
     * @return the bounding box or null
     */
    public Rectangle2D.Float getBounds(RowIterator iter) {
        Rectangle2D.Float bounds = null;
        while(iter.hasNext()) {
            int i = iter.nextRow();
            Shape s = get(i);
            if (s == null) continue;
            if (bounds == null) {
                bounds = RectPool.allocateRect();
                bounds.setRect(s.getBounds2D());
            }
            else {
                bounds.add(s.getBounds2D());
            }
        }
        return bounds;
    }
    
    /**
     * Apply a function on a set of items enumerated by the
     * specified iterator and return the bounding box of the
     * transformed shapes.
     * 
     * @param iter the iterator
     * @param fn the function
     * @return the bounding box of the transformed shapes
     */
    public Rectangle2D.Float transform(RowIterator iter, ShapeFunction fn) {

        Rectangle2D.Float bounds = null;
        try {
            disableNotify();
            while(iter.hasNext()) {
                int i = iter.nextRow();
                Shape s = fn.apply(get(i));
                if (s == null) continue;
                if (bounds == null) {
                    bounds = RectPool.allocateRect();
                    bounds.setRect(s.getBounds2D());
                }
                else {
                    bounds.add(s.getBounds2D());
                }
                modified(i);
            }
        }
        finally {
            enableNotify();
        }
        return bounds;
    }
 
    /**
     * Applies a translation to all the elements in the iterator.
     * @param iter the iterator
     * @param dx the dx component 
     * @param dy the dy component
     * @return the bounding box of the translated elements
     */
    public Rectangle2D.Float translate(RowIterator iter, final double dx, final double dy) {
        return transform(iter, new ShapeFunction() {
            AffineTransform at;
            /**
             * {@inheritDoc}
             */
            public Shape apply(Shape s) {
                if (s != null) {
                    if (s instanceof Rectangle2D) {
                        Rectangle2D rect = (Rectangle2D) s;
                        rect.setRect(
                                rect.getX()+dx, 
                                rect.getY()+dy, 
                                rect.getWidth(), 
                                rect.getHeight());
                    }
                    else {
                        if (at == null) {
                            at = new AffineTransform();
                            at.translate(dx, dy);
                        }
                        PathIterator pi = s.getPathIterator(at);
                        GeneralPath path = new GeneralPath();
                        path.append(pi, false);
                        s = path;
                    }
                }
                return s;
            }
        });
    }
}