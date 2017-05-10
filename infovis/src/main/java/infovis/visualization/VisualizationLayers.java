/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

import cern.colt.list.IntArrayList;

/**
 * Class VisualizationLayers
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class VisualizationLayers extends VisualizationProxy {
    protected ArrayList         layer;
    protected IntArrayList      layerRank;
    protected VisualizationInteractor interactor;
    
    /** Rank of layers for rulers. */
    public final static int RULER_LAYER      = 0;
    /** Rank of the main layer. */
    public final static int MAIN_LAYER       = 100;
    /** Rank of layers for magic lenses. */
    public final static int MAGIC_LENS_LAYER = 200;
    /** Rank of layers for transient objects. */
    public final static int DRAG_LAYER       = 300;

    /**
     * Creates a VisualizationLayers around a specified visualization.
     * @param visualization the visualization
     */
    public VisualizationLayers(Visualization visualization) {
        super(visualization);
    }
    
    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        return interactor;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor inter) {
        this.interactor = inter;
    }

    protected ArrayList getLayer() {
        if (layer == null) {
            layer = new ArrayList();
            layerRank = new IntArrayList();
        }
        return layer;
    }
    
    protected IntArrayList getLayerRank() {
        if (layer == null) {
            layer = new ArrayList();
            layerRank = new IntArrayList();
        }
        return layerRank;
    }

    /**
     * @return the number or layers
     */
    public int size() {
        return getLayer().size();
    }

    /**
     * Adds a specified visualization as a main layer.
     * @param vis the visualization
     */
    public void add(Visualization vis) {
        add(vis, MAIN_LAYER, -1);
    }

    /**
     * Adds a specified visualization in a specified layer.
     * @param vis the visualization
     * @param layer the layer
     */
    public void add(Visualization vis, int layer) {
        add(vis, layer, -1);
    }

    /**
     * Adds a specified visualization in a specified layer and a position
     * relative to other layers of the same rank.
     * @param vis the visualization
     * @param l the layer
     * @param pos the position
     */
    public void add(Visualization vis, int l, int pos) {
        pos = insertIndexForLayer(l, pos);
        layer.add(pos, vis);
        layerRank.beforeInsert(pos, l);
        if (getParent() != null) {
            vis.setParent(getParent());
        }
        repaint();
    }

    /**
     * Removes the layer at the specified index.
     * @param index the index
     */
    public void remove(int index) {
        if (index < 0)
            return;
        Visualization vis = getVisualization(index);
        if (vis == visualization) {
            throw new RuntimeException("cannot remove main visualization");
        }
        vis.setParent(null);
        layer.remove(index);
        layerRank.remove(index);
        repaint();
    }

    /**
     * Returns the index of the layer at the specified rank and position.
     * @param layer the rank
     * @param pos the position
     * @return the index
     */
    public int insertIndexForLayer(int layer, int pos) {
        int index = getLayerRank().binarySearch(layer);
        if (index < 0) {
            return -index - 1;
        }
        if (pos == -1) {
            int last = layerRank.size()-1;
            while(index != last 
                    && layerRank.get(index+1) == layer) {
                index++;
            }
        }
        else {
            while(index != 0 
                    && layerRank.get(index-1) == layer) {
                index--;
            }
        }
        return index;
    }

    /**
     * Returns the index of the specifed visualization or -1
     * @param visualization the visualization
     * @return the index or -1
     */
    public int indexOf(Visualization visualization) {
        if (layer == null)
            return -1;
        return layer.indexOf(visualization);
    }

    /**
     * Returns the rank of the specified visualization or -1
     * @param vis the visualization
     * @return the rank or -1
     */
    public int getRank(Visualization vis) {
        int index = indexOf(vis);
        if (index == -1) return -1;
        return layerRank.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.dispose();
        }
        layer.clear();
        layerRank.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Visualization findVisualization(Class cls) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            Visualization v = vis.findVisualization(cls);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        if (index >= size())
            return null;
        return (Visualization) layer.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate() {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.invalidate();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isInvalidated() {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            if (vis.isInvalidated()) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.paint(graphics, bounds);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            pick = vis.pickAll(hitBox, bounds, pick);
        }
        return pick;
    }

    /**
     * {@inheritDoc}
     */
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.print(graphics, bounds);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setParent(Component parent) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.setParent(parent);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setVisualization(Visualization vis) {
        if (visualization == vis)
            return;
        int index = indexOf(visualization);
        remove(index);
        visualization = vis;
        add(vis);
        invalidate();
    }
}
