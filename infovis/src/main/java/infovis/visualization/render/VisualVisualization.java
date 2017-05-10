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
import infovis.column.ObjectColumn;
import infovis.column.filter.NotTypedFilter;
import infovis.visualization.ItemRenderer;
import infovis.visualization.magicLens.Fisheye;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.VolatileImage;

/**
 * Class VisualVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class VisualVisualization extends AbstractVisualColumn {
    /** Default name for visual item. */
    public static final String VISUAL = "visualization";
    protected ObjectColumn visualizationColumn;
    protected transient Fisheye fisheye;
    protected boolean cached = false;
    protected ObjectColumn images;

    /**
     * Retrieve the VisualVisualization in a specified Visualization
     * @param vis the visualization
     * @return the VisualVisualization or null
     */
    public static VisualVisualization get(Visualization vis) {
        return (VisualVisualization) findNamed(VISUAL, vis);
    }

    /**
     * Retrieve the VisualVisualization in a specified ItemRenderer tree
     * @param ir the ItemRenderer root
     * @return the VisualVisualization or null
     */
    public static VisualVisualization get(ItemRenderer ir) {
        return (VisualVisualization) findNamed(VISUAL, ir);
    }
    
    /**
     * Constructor with a name for subclassing.
     * @param name the name
     */
    public VisualVisualization(String name) {
        super(name);
    }

    /**
     * Default constructor.
     */
    public VisualVisualization() {
        super(VISUAL);
    }

    /**
     * Constructor with a specified child.
     * @param child the child
     */
    public VisualVisualization(ItemRenderer child) {
        this();
        addRenderer(child);
        this.invalidate = true;
        this.filter = new NotTypedFilter(Visualization.class);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return visualizationColumn;
    }

    /**
     * @return the visualization column
     */
    public ObjectColumn getVisualizationColumn() {
        return visualizationColumn;
    }
    
    /**
     * @return the cached
     */
    public boolean isCached() {
        return cached;
    }
    
    /**
     * Enable/disable the cache
     * @param cached the cached to set
     */
    public void setCached(boolean cached) {
        if (this.cached == cached) return;
        this.cached = cached;
        if (! cached && images != null) {
            for (int i = images.size(); --i >= 0; ) {
                VolatileImage cache = (VolatileImage)images.getObjectAt(i);
                if (cache != null) {
                    cache.flush();
                }
            }
            images.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (visualizationColumn == column)
            return;
        super.setColumn(column);
        visualizationColumn = (ObjectColumn) column;
        invalidate();
    }

    /**
     * Returns the visualization at the specified row.
     * @param row the row
     * @return the Visualization or null
     */
    public Visualization getVisualizationAt(int row) {
        if (visualizationColumn == null
                || visualizationColumn.isValueUndefined(row))
            return null;
        Object vis = visualizationColumn.get(row);
        if (vis instanceof Visualization) {
            return (Visualization)vis;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        super.install(graphics);
        VisualFisheye vf = VisualFisheye.get(getVisualization());
        if (vf != null) {
            fisheye = vf.getFisheye();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        super.paint(graphics, row, shape);
        Visualization vis = getVisualizationAt(row);
        if (vis == null) return;
        VisualFisheye vf = VisualFisheye.get(vis);
        if (vf != null && vf.getFisheye() != fisheye) {
            vf.setFisheye(fisheye);
        }
        Component parent = vis.getParent();
        // Set the parent last to avoid repaints if possible
        if (parent != getVisualization().getParent()) {
            vis.setParent(parent);
        }

        if (! cached
                || fisheye == null 
                || (fisheye.isEnabled() && shape.intersects(fisheye.getBounds()))) { 
            vis.paint(graphics, shape.getBounds2D());
        }
        else {
            VolatileImage cache;
            if (images == null) {
                images = new ObjectColumn("#cache");
                cache = null;
            }
            else {
                cache = (VolatileImage)images.getObjectAt(row); 
            }
            Rectangle r = shape.getBounds();
            if (cache != null &&
                    (cache.getWidth() < r.width || cache.getHeight() < r.height)) {
                cache.flush();
                cache = null;
            }

            GraphicsConfiguration config = parent.getGraphicsConfiguration();
            if (config == null) {
                config = GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDefaultConfiguration();
            }
            if (cache == null) {   
                cache = config.createCompatibleVolatileImage(r.width, r.height);
            }
            do {
                if (cache.validate(config) == VolatileImage.IMAGE_INCOMPATIBLE) {
                    cache = config.createCompatibleVolatileImage(r.width, r.height);
                }
                Graphics2D g = (Graphics2D)cache.getGraphics();
                g.translate(-r.x, -r.y);
                vis.paint(g, shape.getBounds2D());
                g.dispose();
            }
            while (cache.contentsLost());
            images.setExtend(row, cache);
            graphics.drawImage(cache, r.x, r.y, parent);
        }
    }
}
