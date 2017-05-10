/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

import infovis.column.NumberColumn;
import infovis.column.ShapeColumn;

import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

/**
 * <b>QuadTree</b> is a data structure that maintains a set of points and allow
 * fast access to them.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class QuadTree {
    protected int               index;
    protected QuadTree[]        children = new QuadTree[4];
    protected double            x;
    protected double            y;
    protected double            xmin;
    protected double            ymin;
    protected double            xmax;
    protected double            ymax;
    protected double            weight;

    private static final Logger log      = Logger.getLogger(QuadTree.class);

    /**
     * Creates a quadtree node.
     * @param index the point index or -1
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param weight the weight of the point
     * @param xmin min x coord of the box 
     * @param ymin min y coord of the box 
     * @param xmax max x coord of the box 
     * @param ymax max y coord of the box 
     */
    public QuadTree(
            int index,
            double x,
            double y,
            double weight,
            double xmin,
            double ymin,
            double xmax,
            double ymax) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    /**
     * Builds a QuadTree from a shape column and a weight column.
     * @param shape the Shape Column
     * @param weight the repulsion weight column
     * @return a QuadTree from the shape column and the weight column
     */
    public static QuadTree build(ShapeColumn shape, NumberColumn weight) {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = -Double.MAX_VALUE;
        double ymax = -Double.MAX_VALUE;
        for (RowIterator iter = shape.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            Rectangle2D.Float r = shape.getRect(row);
            if (r == null || weight.getDoubleAt(row) <= 0) continue;
            xmin = Math.min(r.x, xmin);
            ymin = Math.min(r.y, ymin);
            xmax = Math.max(r.x, xmax);
            ymax = Math.max(r.y, ymax);
        }
        
        QuadTree result = null;
        for (RowIterator iter = shape.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            Rectangle2D.Float r = shape.getRect(row);
            if (r == null || weight.getDoubleAt(row) <= 0) continue;
            if (result == null) {
                result = new QuadTree(
                        row, 
                        r.x, r.y, 
                        weight.getDoubleAt(row),
                        xmin, ymin,
                        xmax, ymax);
            } else {
                result.addNode(
                        row, 
                        r.x, r.y, 
                        weight.getDoubleAt(row),
                        0);
            }
        }
        return result;
    }
    
    /**
     * Builds a QuadTree from a shape column and a weight column.
     * @param shape the Shape Column
     * @return a QuadTree from the shape column and the weight column
     */
    public static QuadTree build(ShapeColumn shape) {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = -Double.MAX_VALUE;
        double ymax = -Double.MAX_VALUE;
        for (RowIterator iter = shape.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            Rectangle2D.Float r = shape.getRect(row);
            if (r == null) continue;
            xmin = Math.min(r.x, xmin);
            ymin = Math.min(r.y, ymin);
            xmax = Math.max(r.x, xmax);
            ymax = Math.max(r.y, ymax);
        }
        
        QuadTree result = null;
        for (RowIterator iter = shape.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            Rectangle2D.Float r = shape.getRect(row);
            if (r == null) continue;
            if (result == null) {
                result = new QuadTree(
                        row, 
                        r.x, r.y, 
                        1,
                        xmin, ymin,
                        xmax, ymax);
            } else {
                result.addNode(
                        row, 
                        r.x, r.y, 
                        1,
                        0);
            }
        }
        return result;
    }
    
    /**
     * Builds a QuadTree from a shape column and a weight column.
     * @param xpos the column containing the X coordinates
     * @param ypos the column containing the Y coordinates
     * @param weight the repulsion weight column
     * @return a QuadTree from the shape column and the weight column
     */
    public static QuadTree build(
            NumberColumn xpos, 
            NumberColumn ypos, 
            NumberColumn weight) {
        double xmin = xpos.getDoubleMin();
        double ymin = ypos.getDoubleMin();
        double xmax = xpos.getDoubleMax();
        double ymax = ypos.getDoubleMax();
        
        QuadTree result = null;
        for (RowIterator iter = xpos.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            if (ypos.isValueUndefined(row) 
                    || weight.getDoubleAt(row) <= 0) continue;
            if (result == null) {
                result = new QuadTree(
                        row, 
                        xpos.getDoubleAt(row), 
                        ypos.getDoubleAt(row),
                        weight.getDoubleAt(row),
                        xmin, ymin,
                        xmax, ymax);
            } else {
                result.addNode(
                        row, 
                        xpos.getDoubleAt(row),
                        ypos.getDoubleAt(row),
                        weight.getDoubleAt(row),
                        0);
            }
        }
        return result;
    }
    
    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Returns true it the QuadTree is a node
     * and not a leaf.
     * @return true it the QuadTree is a node
     * and not a leaf
     */
    public boolean isNode() {
        return index < 0;
    }
    
    /**
     * Returns true it the QuadTree is a leaf
     * and not a node.
     * @return true it the QuadTree is a leaf
     * and not a node
     */
    public boolean isLeaf() {
        return ! isNode();
    }
    
    /**
     * @return Returns the x.
     */
    public double getX() {
        return x;
    }
    
    /**
     * @return Returns the y.
     */
    public double getY() {
        return y;
    }
    
    /**
     * @return Returns the xmin.
     */
    public double getXMin() {
        return xmin;
    }
    
    /**
     * @return Returns the ymin.
     */
    public double getYMin() {
        return ymin;
    }
    
    /**
     * @return Returns the xmax.
     */
    public double getXMax() {
        return xmax;
    }
    
    /**
     * @return Returns the ymax.
     */
    public double getYMax() {
        return ymax;
    }
    
    /**
     * @return Returns the weight.
     */
    public double getWeight() {
        return weight;
    }
    
    /**
     * @return Returns the number of children.
     */
    public int getChildrenCount() {
        return children.length;
    }
    
    /**
     * Returns the quadtre at the specified index.
     * @param i the index
     * @return the children.
     */
    public QuadTree getChild(int i) {
        return children[i];
    }

    /**
     * Inserts a specified node.
     * @param nodeIndex the node index
     * @param nodeX the x pos
     * @param nodeY the y pos
     * @param nodeWeight the weight
     * @param depth the depth
     */
    public void addNode(
            int nodeIndex,
            double nodeX,
            double nodeY,
            double nodeWeight,
            int depth) {
        if (depth > 20) {
            log.error("QuadTree: graph node dropped because tree depth > 20.");
            return;
        }
        if (isLeaf()) { // transform into a node
            addNode2(index, x, y, weight, depth);
            index = -1;
        }

        x = (x * weight + nodeX * nodeWeight) / (weight + nodeWeight);
        y = (y * weight + nodeY * nodeWeight) / (weight + nodeWeight);

        weight += nodeWeight;
        addNode2(nodeIndex, nodeX, nodeY, nodeWeight, depth);
    }
    
    /**
     * @return Returns the center X coordinate
     */
    public double getCenterX() {
        return (xmin+xmax)/2;
    }
    
    /**
     * @return Returns the center Y coordinate
     */
    public double getCenterY() {
        return (ymin+ymax)/2;
    }
    
    /**
     * Computes the index of the specified point.
     * @param x the X coord
     * @param y the Y coord
     * @return the index of the specified point
     */
    public int computeIndex(double x, double y) {
        return (x > getCenterX() ? 1 : 0) + (y > getCenterY() ? 2 : 0);
    }

    protected void addNode2(
            int nodeIndex,
            double nodeX,
            double nodeY,
            double nodeWeight,
            int depth) {
        int childIndex = computeIndex(nodeX, nodeY);

        if (children[childIndex] == null) {
            double newXMin;
            double newYMin;
            double newXMax;
            double newYMax;

            if ((childIndex & 1) == 0) {
                newXMin = xmin;
                newXMax = getCenterX();
            }
            else {
                newXMin = getCenterX();
                newXMax = xmax;
            }
            if ((childIndex & 2) == 0) {
                newYMin = ymin;
                newYMax = getCenterY();
            }
            else {
                newYMin = getCenterY();
                newYMax = ymax;
            }

            children[childIndex] = new QuadTree(
                    nodeIndex,
                    nodeX, nodeY,
                    nodeWeight,
                    newXMin, newYMin, newXMax, newYMax);
        }
        else {
            children[childIndex].addNode(
                    nodeIndex,
                    nodeX, nodeY,
                    nodeWeight,
                    depth + 1);
        }
    }

    /**
     * Moves the node by a specified amount and fix the descendants.
     * @param oldX old x pos
     * @param oldY old y pos
     * @param newX new x pos
     * @param newY new y pos
     * @param nodeWeight node weight
     */
    public void moveNode(
            double oldX, double oldY,
            double newX, double newY,
            double nodeWeight) {
        x += (newX - oldX) * (nodeWeight / weight);
        y += (newY - oldY) * (nodeWeight / weight);

        int childIndex = 0;
        if (oldX > (xmin+xmax)/2) {
            childIndex += 1;
        }
        if (oldY > (ymin+ymax)/2) {
            childIndex += 1 << 1;
        }

        if (children[childIndex] != null) {
            children[childIndex].moveNode(oldX, oldY, newX, newY, nodeWeight);
        }
    }

    /**
     * Returns the max width of the box.
     * @return the max width of the box
     */
    public double getWidth() {
        return Math.max(xmax-xmin, ymax-ymin);
    }
}
