/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.Visualization;
import infovis.graph.Algorithms;
import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.utils.IntArrayIterator;
import infovis.utils.Permutation;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import cern.colt.list.IntArrayList;

/**
 * <b>PackingGraphLayout</b> is a layout that uses another layout
 * for packing several connected components of a graph.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class PackingGraphLayout extends AbstractGraphLayout {
    protected NodeLinkGraphLayout layout;
    protected Rectangle2D.Float bbox;
    protected float gap = 10;
    
    /**
     * Creates a PackinGraphLayout on a specified layout.
     * @param layout the layout
     */
    public PackingGraphLayout(NodeLinkGraphLayout layout) {
        this.layout = layout;
    }
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return layout.getName();
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
        super.invalidate(vis);
        bbox = null;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (bbox == null) {
            ((NodeLinkGraphVisualization)vis).computeShapes(null);
            if (bbox != null) {
                preferredSize = new Dimension((int)bbox.getWidth(), (int)bbox.getHeight());
            }
        }
        return preferredSize; 
    }    
    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, NodeLinkGraphVisualization vis) {
        if (bbox != null) {
            return;
        }
        IntArrayList[] comps = Algorithms.computeConnectedComponents(vis);
        Permutation savedPerm = vis.getPermutation();
        Permutation savedLinkPerm = vis.getLinkVisualization().getPermutation();
        try {
            for (int c = 0; c < comps.length; c++) {
                IntArrayList comp = comps[c];
                if (comps.length != 1) {
                    Permutation perm = new Permutation(comp);
                    vis.setPermutation(perm);
                }
                layout.invalidate(vis);
                layout.computeShapes(bounds, vis);
            }
        }
        finally {
            vis.setPermutation(savedPerm);
            vis.getLinkVisualization().setPermutation(savedLinkPerm);
        }
        bbox = packShapes(comps);
    }
    
    static class Bin {
        boolean horizontal;
        Rectangle2D.Float bounds;
        Rectangle2D.Float free;
        
        Bin(boolean hor, float x, float y, float w, float h) {
            horizontal = hor;
            this.bounds = new Rectangle2D.Float(x, y, w, h);
            this.free = new Rectangle2D.Float(x, y, w, h);
        }
        
        /**
         * @return the bounds
         */
        public Rectangle2D.Float getBounds() {
            return bounds;
        }
        
        /**
         * @return the free
         */
        public Rectangle2D.Float getFree() {
            return free;
        }
        
        boolean add(float gap, Rectangle2D.Float box) {
            boolean extend = false;
            if (horizontal) {
                box.x = free.x+gap;
                box.y = free.y;
                free.x += box.width+gap;
                free.width -= box.width+gap;
                if (free.width < 0) {
                    bounds.width -= free.width;
                    free.width = 0;
                    extend = true;
                }
                if (free.height < box.height) {
                    free.height = box.height;
                    bounds.height = box.height;
                }
            }
            else {
                box.x = free.x;
                box.y = free.y+gap;
                free.y += box.height+gap;
                free.height -= box.height+gap;
                if (free.height < 0) {
                    bounds.height -= free.height;
                    free.height = 0;
                    extend = true;
                }
                if (free.width < box.width) {
                    free.width = box.width;
                    bounds.width = box.width;
                }
            }
            return extend;
        }
    }
    
    protected Rectangle2D.Float packShapes(IntArrayList[] comps) {
        Rectangle2D.Float[] bounds = new Rectangle2D.Float[comps.length];
        for (int c = 0; c < comps.length; c++) {
            IntArrayList comp = comps[c];
            bounds[c] = shapes.getBounds(new IntArrayIterator(comp));
        }
        Rectangle2D.Float totalBounds = bounds[0];
        Bin bin;
        if (totalBounds.width > totalBounds.height) {
            bin = new Bin(true, 0, totalBounds.height+gap, totalBounds.width, 0);
        }
        else {
            bin = new Bin(false, totalBounds.width+gap, 0, 0, totalBounds.height);
        }
        for (int c = 1; c < comps.length; c++) {
            boolean extend = bin.add(gap, bounds[c]);
            shapes.translate(
                    new IntArrayIterator(comps[c]),
                    bounds[c].x, bounds[c].y);
            if (extend) {
                totalBounds.add(bin.getBounds());
                if (totalBounds.width > totalBounds.height) {
                    bin = new Bin(true, 0, totalBounds.height+gap, totalBounds.width, 0);
                }
                else {
                    bin = new Bin(false, totalBounds.width+gap, 0, 0, totalBounds.height);
                }
            }
        }
        totalBounds.add(bin.getBounds());
        return totalBounds;
    }


    /**
     * @return the gap
     */
    public float getGap() {
        return gap;
    }
    /**
     * @param gap the gap to set
     */
    public void setGap(float gap) {
        this.gap = gap;
    }
}
