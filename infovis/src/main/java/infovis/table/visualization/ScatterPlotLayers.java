/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.table.visualization;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import infovis.Visualization;
import infovis.visualization.VisualizationLayers;

/**
 * <b>ScatterPlotLayers</b> implements layers of scatter plots
 * sharing the same data bounds (visible x/y ranges).
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class ScatterPlotLayers extends VisualizationLayers {

    /**
     * Creates layers of scatter plots, sharing the same visible bounds.
     * 
     * @param visualization
     *            the initial scatter plot visualization
     */
    public ScatterPlotLayers(ScatterPlotVisualization visualization) {
        super(visualization);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        int i = 0;
        Rectangle2D.Double dataBounds = new Rectangle2D.Double();
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            if (vis instanceof ScatterPlotVisualization) {
                ScatterPlotVisualization sp = (ScatterPlotVisualization) vis;
                if (dataBounds.isEmpty()) {
                    dataBounds.setFrame(sp.getDefaultDataBounds());
                }
                else {
                    dataBounds.add(sp.getDefaultDataBounds());
                }
            }
        }
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            if (vis instanceof ScatterPlotVisualization) {
                ScatterPlotVisualization sp = (ScatterPlotVisualization) vis;
                sp.setDataBounds(dataBounds);
            }
            super.paint(graphics, bounds);
        }
    }
}
