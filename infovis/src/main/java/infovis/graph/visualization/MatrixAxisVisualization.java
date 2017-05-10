/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Graph;
import infovis.Visualization;
import infovis.table.Item;
import infovis.utils.InfovisUtilities;
import infovis.utils.Permutation;
import infovis.visualization.Layout;
import infovis.visualization.Orientation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JViewport;

import cern.colt.function.IntFunction;

/**
 * <b>MatrixAxisVisualization</b> shows the axis of the matrix.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.35 $
 */
public class MatrixAxisVisualization extends GraphVisualization 
    implements Layout {
    /** The preferedSize property. */
    public static final String    PROPERTY_PREFERED_SIZE = "preferedSize";
    protected int                 preferedSize           = 100;
    protected MatrixVisualization matrix;
    protected Font                font                   = new Font(
                                                                 "Dialog",
                                                                 Font.BOLD,
                                                                 20);
    protected boolean 			  axisOrientationLabel   = true;
    
    /**
     * Constructor with a graph and a matrix visualization.
     * @param graph the graph
     * @param matrix the matrix
     */
    public MatrixAxisVisualization(Graph graph, MatrixVisualization matrix) {
        super(graph, graph.getVertexTable());
        this.matrix = matrix;
    }
    
    public MatrixVisualization getMatrixVisualization()
    {return matrix;}
    

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization() {
        return this;
    }

    /**
     * Sets the orientation.
     * @param o the new orientation
     */
    public void setOrientationLabel(boolean o){
    	axisOrientationLabel = false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        super.paint(graphics, bounds);

        if(graph.isDirected() && axisOrientationLabel){
        
	        boolean vertical = !Orientation.isVertical(orientation);
	        String label = vertical ? "From" : "To";
	        Component parent = getParent();
	        parent = (parent == null) ? null : parent.getParent();
	        if (parent instanceof JViewport) {
	            JViewport vp = (JViewport) parent;
	            bounds = vp.getViewRect();
	        }
	        graphics.setFont(font);
	        graphics.setColor(Color.BLACK);
	        if (vertical) {
	            InfovisUtilities.drawStringVertical(
	                    graphics,
	                    label,
	                    bounds,
	                    0.5f,
	                    orientation == ORIENTATION_EAST ? 1 : 0,
	                    true);
	        }
	        else {
	            InfovisUtilities.drawString(
	                    graphics,
	                    label,
	                    bounds,
	                    0.5f,
	                    orientation == ORIENTATION_NORTH ? 0 : 1,
	                    true);
	        }
        }
    }
    
    /**
     * Applies a function to each edges contained in a specified bounds.
     * 
     * <p>Continue while the function returns -1, stop otherwise.
     * Returns the last value returned by the function or -1. 
     * 
     * @param hitBox the bounds 
     * @param fn the function object
     * @return -1 or the value returned by the function object
     */
    public int foreachRowInBounds(Rectangle2D hitBox, IntFunction fn) {
        Rectangle2D bounds = getBounds();
        if (! bounds.contains(hitBox)) {
            if (!bounds.intersects(hitBox)) return -1;
            Rectangle2D inter = new Rectangle2D.Double();
            Rectangle2D.intersect(bounds, hitBox, inter);
            hitBox = inter;
            if (hitBox.isEmpty()) return -1; // double check
        }
        int min;
        int max;
        double xoff = bounds.getX();
        double yoff = bounds.getY();
        if (Orientation.isVertical(orientation)) {
            double w = bounds.getWidth() / getRowCount();
            min = (int)Math.floor((hitBox.getMinX()-xoff) / w);
            max = (int)Math.ceil((hitBox.getMaxX()-xoff) / w);
        }
        else {
            double h = bounds.getHeight() / getRowCount();
            min = (int)Math.floor((hitBox.getMinY()-yoff) / h);
            max = (int)Math.ceil((hitBox.getMaxY()-yoff) / h);
        }
        for (int x = min; x < max; x++) {
            int row = x;
            row = Permutation.getDirect(permutation, row);
            if (row == -1) continue;
            int ret = fn.apply(row);
            if (ret != -1) {
                return ret;
            }
        }
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public void paintItems(final Graphics2D graphics, Rectangle2D bounds) {
        Rectangle clip = graphics.getClipBounds();
        if (clip == null) {
            super.paintItems(graphics, bounds);
        }
        else {
            foreachRowInBounds(clip, new IntFunction() {
                public int apply(int row) {
                    paintItem(graphics, row);
                    return -1;
                }
            });
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Item pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
    	validateShapes(bounds);
        return getEdge(foreachRowInBounds(hitBox, new IntFunction() {
            public int apply(int edge) {
                return edge;
            }
        }));
    }
    
    /**
     * {@inheritDoc}
     */
    public ArrayList pickAll(Rectangle2D hitBox, Rectangle2D bounds, ArrayList pick) {
        validateShapes(bounds);
        if (pick == null)
            pick = new ArrayList();
        else
            pick.clear();
        final ArrayList p = pick;
        foreachRowInBounds(hitBox, new IntFunction() {
            public int apply(int edge) {
                p.add(getEdge(edge));
                return -1;
            }
        
        });
        return pick;
    }
    
    protected Rectangle2D.Float TMP_RECT = new Rectangle2D.Float();
    
    /**
     * {@inheritDoc}
     */
    public Shape getShapeAt(int row) {
	    Rectangle2D bounds = getBounds();
	    row = permutation == null ? row : permutation.getInverse(row);
	    if (Orientation.isVertical(orientation)) {
	        double w = bounds.getWidth() / getRowCount();
	        TMP_RECT.setRect(w * row + bounds.getX(), bounds.getY(), w, bounds.getHeight());
	    }
	    else {
	        double h = bounds.getHeight() / getRowCount();
	        TMP_RECT.setRect(bounds.getX(), h * row + bounds.getY(), bounds.getWidth(), h);
	    }
	    return TMP_RECT;
	
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
    	/*
        assert (this == vis);
        double xoff = bounds.getX();
        double yoff = bounds.getY();
        if (Orientation.isVertical(orientation)) {
            double w = bounds.getWidth() / getRowCount();
            int row = 0;
            for (RowIterator iter = iterator(); iter.hasNext(); row++) {
                int v = iter.nextRow();
                Rectangle2D.Float s = findRectAt(v);
                s.setRect(w * row + xoff, yoff, w, bounds.getHeight());
            }
        }
        else {
            double h = bounds.getHeight() / getRowCount();
            int row = 0;
            for (RowIterator iter = iterator(); iter.hasNext(); row++) {
                int v = iter.nextRow();
                Rectangle2D.Float s = findRectAt(v);
                s.setRect(xoff, h * row+yoff, bounds.getWidth(), h);
            }
       }*/
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        Dimension pref = matrix.getPreferredSize();
        if (pref == null) return null;
        if (Orientation.isVertical(orientation)) {
            return new Dimension(pref.width, (int) preferedSize);
        }
        else {
            return new Dimension((int) preferedSize, pref.height);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }

    /**
     * @return the preferred size
     */
    public int getPreferedSize() {
        return preferedSize;
    }

    /**
     * Sets the prefered size.
     * @param preferedSize the new size
     */
    public void setPreferedSize(int preferedSize) {
        if (this.preferedSize == preferedSize)
            return;
        int old = this.preferedSize;
        this.preferedSize = preferedSize;
        firePropertyChange(PROPERTY_PREFERED_SIZE, old, this.preferedSize);
        invalidate();
    }
}
