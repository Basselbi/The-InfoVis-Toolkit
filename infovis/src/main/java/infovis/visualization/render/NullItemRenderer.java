/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import infovis.Visualization;
import infovis.visualization.ItemRenderer;

/**
 * <b>NullItemRenderer</b> is a no-op item renderer.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class NullItemRenderer implements ItemRenderer {
    
    /**
     * Instance of a null item renderer.
     */
    public static final NullItemRenderer INSTANCE = new NullItemRenderer();

    /**
     * {@inheritDoc}
     */
    public ItemRenderer addRenderer(ItemRenderer r) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer compile() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "null";
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer getRenderer(int index) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getRendererCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer insertRenderer(int index, ItemRenderer r) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer instantiate(Visualization vis) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPrototype() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer removeRenderer(int index) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer setRenderer(int index, ItemRenderer r) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void uninstall(Graphics2D graphics) {
    }
}
