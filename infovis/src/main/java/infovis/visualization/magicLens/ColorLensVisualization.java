/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.visualization.VisualizationInteractor;
import infovis.visualization.VisualizationProxy;
import infovis.visualization.color.OrderedColor;
import infovis.visualization.render.VisualAttributeCollector;
import infovis.visualization.render.VisualColor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Class ColorLensVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class ColorLensVisualization extends VisualizationProxy {
    protected Rectangle               lens;
    protected boolean                 enabled = false;
    protected VisualizationInteractor interactor;
    protected transient ArrayList     vas     = new ArrayList();

    /**
     * @param visualization
     */
    public ColorLensVisualization(Visualization visualization) {
        super(visualization);
        lens = new Rectangle(0, 0, 50, 50);
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        visualization.repaint();
    }

    /**
     * {@inheritDoc}
     */
    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(this.getClass()))
            return this;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor inter) {
        if (this.interactor == inter)
            return;
        if (this.interactor != null) {
            this.interactor.setVisualization(null);
        }
        this.interactor = inter;
        if (this.interactor != null) {
            this.interactor.setVisualization(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        return interactor;
    }

    /**
     * @return the lens
     */
    public Rectangle getLens() {
        return lens;
    }

    /**
     * @param lens
     *            the lens to set
     */
    public void setLens(Rectangle lens) {
        this.lens = lens;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (enabled) {
            // vc = VisualColor.get(visualization);
            // if (vc != null && bounds.intersects(lens)) {
            // gbuffer.clear();
            // visualization.paint(gbuffer, bounds);
            graphics.setColor(Color.BLACK);
            graphics.draw(lens);
            Rectangle frame = getFrame();
            graphics.fill(frame);
            vas.clear();
            ColorLensBackgroundLayer.collectVA(visualization, vas);
            int n = vas.size();
            if (n != 0) {
                int h = frame.height / n;
                int y = frame.y;
                for (int i = 0; i < n; i++, y += h) {
                    VisualAttributeCollector va = (VisualAttributeCollector)vas.get(i);
                    VisualColor vc = va.getVisualColor();
                    OrderedColor oc = (OrderedColor)vc.getColorVisualization();
                    NumberColumn col = (NumberColumn)vc.getColumn();
                    double v = col.getDoubleMin();
                    double delta = (col.getDoubleMax() - v) / frame.width;
                    for (int x = 0; x < frame.width; x++, v += delta) {
                        Color c = oc.getColorForParameter(oc.getColorParameterFor(v));
                        graphics.setColor(c);
                        graphics.drawLine(x+frame.x, y, x+frame.x, y+h);
                    }
                }
            }
            // }
        }
        // super.paint(graphics, bounds);
    }

    /**
     * @return the bounds of the top frame
     */
    public Rectangle getFrame() {
        return new Rectangle(lens.x, lens.y - 30, lens.width + 1, 30);
    }

    /**
     * Returns true if a position is in the lens.
     * 
     * @param x
     *            coord X
     * @param y
     *            coord Y
     * @return
     */
    public boolean pick(int x, int y) {
        return lens.contains(x, y) || getFrame().contains(x, y);
    }

}
