/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Visualization;
import infovis.utils.RowBufferGraphics2D;
import infovis.visualization.VisualizationProxy;
import infovis.visualization.color.OrderedColor;
import infovis.visualization.render.VisualAttributeCollector;
import infovis.visualization.render.VisualColor;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class ColorLensBackgroundLayer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class ColorLensBackgroundLayer extends VisualizationProxy {
    protected ColorLensVisualization   colorLensVisualization;
    protected Rectangle                lens;
    protected RowBufferGraphics2D      gbuffer;
    protected ArrayList<VisualAttributeCollector> vas = new ArrayList<VisualAttributeCollector>();

    /**
     * Constructor with the visualization to render and the lens.
     * @param vis the visualization to render 
     * @param visualization the color lens
     */
    public ColorLensBackgroundLayer(Visualization vis, ColorLensVisualization visualization) {
        super(vis);
        this.colorLensVisualization = visualization;        
    }
    
    /**
     * Collect all the VisualAttributeCollectors contained in a visualization
     * and its children.
     * @param vis the root visualization
     * @param vas the ArrayList that will contain the VisualAttributeCollectors
     */
    public static void collectVA(Visualization vis, ArrayList vas) {
        VisualAttributeCollector va = (VisualAttributeCollector)VisualAttributeCollector.get(vis);
        VisualColor              vc = VisualColor.get(vis);
        if (va == null
                || vc == null
                || ! (vc.getColorVisualization() instanceof  OrderedColor)) {
        }
        else {
            vas.add(va);
        }
        int i = 0;
        
        for (Visualization v = vis.getVisualization(i++); 
            v != null; 
            v = vis.getVisualization(i++)) {
            collectVA(v, vas);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (!colorLensVisualization.isEnabled()) {
            return;
        }
        vas.clear();
        collectVA(visualization, vas);
        if (vas.isEmpty()) {
            return;
        }

        // gbuffer = new RowBufferGraphics2D(lens);
        // vc = (VisualColor) VisualColor.findNamed(
        // VisualColor.VISUAL,
        // visualization);
//      vc = VisualColor.get(visualization);
//      if (vc != null && bounds.intersects(lens)) {
          // gbuffer.clear();
          // visualization.paint(gbuffer, bounds);
        Shape save = graphics.getClip();
        graphics.clip(colorLensVisualization.getLens());
        for (Iterator it = vas.iterator(); it.hasNext(); ) {
            VisualAttributeCollector va = (VisualAttributeCollector)it.next();
            va.setEnabled(true);
        }
        super.paint(graphics, bounds);
        for (Iterator it = vas.iterator(); it.hasNext(); ) {
            VisualAttributeCollector va = (VisualAttributeCollector)it.next();
            va.setEnabled(false);
        }
        graphics.setClip(save);
    }

}
