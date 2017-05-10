/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Visualization;
import infovis.utils.RectPool;
import infovis.visualization.ItemRenderer;

import java.awt.*;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

/**
 * Class VisualStatistics
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class VisualStatistics extends AbstractItemRenderer {
    /** Name of the renderer. */
    public static final String VISUAL = "statistics";
    
    protected long redisplayTime;
    protected int displayedItems;
    protected boolean displayingStatistics = false;
    
    private static final Logger LOG = Logger.getLogger(VisualStatistics.class);

    /**
     * Return the VisualStatistics from the visualization.
     * @param vis the visualization
     * @return the VisualStatistics from the visualization
     */
    public static VisualStatistics get(Visualization vis) {
        return (VisualStatistics)findNamed(VISUAL, vis.getItemRenderer());
    }

    /**
     * Constructor.
     * @param child a child
     */
    public VisualStatistics(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        super.install(graphics);
        displayedItems = 0;
        if (displayingStatistics && graphics != null) {
            NDC.push("Painting "+visualization);
            redisplayTime = System.currentTimeMillis();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        displayedItems++;
        super.paint(graphics, row, shape);
    }

    /**
     * {@inheritDoc}
     */
    public void uninstall(Graphics2D graphics) {
        if (displayingStatistics 
                && graphics != null
                && displayedItems != 0) {
            redisplayTime = System.currentTimeMillis() - redisplayTime;
            float fps = 1000.0f / redisplayTime;
            LOG.info("Visualization "+visualization+": displayed items: " +displayedItems);
            LOG.info("Visualization "+visualization+": redisplay time:  "+redisplayTime);
            LOG.info("Visualization "+visualization+": disp per second: "+fps);
            LOG.info("Visualization "+visualization+": item per second: "+displayedItems*fps);
//            paintStatistics(graphics);
            NDC.pop();
        }
        super.uninstall(graphics);
    }

    protected void paintStatistics(Graphics2D graphics) {
        if (graphics != null && displayingStatistics) {
            float fps = 1000.0f / redisplayTime;
            float ips = displayedItems * fps;
            graphics.setColor(Color.BLACK);
            graphics.drawString(
                "Redisplay "
                    + displayedItems
                    + " items in "
                    + redisplayTime
                    + "ms, "
                    + fps
                    + "fps "
                    + ips
                    + "ips",
                0,
                10);
            graphics.drawString(
                    RectPool.getInstance().getStatistics(),
                    0, 25);
        }
    }


    /**
     * Returns the displayingStatistics.
     * @return boolean
     */
    public boolean isDisplayingStatistics() {
        return displayingStatistics;
    }

    /**
     * Sets the displayingStatistics.
     * @param displayingStatistics The displayingStatistics to set
     */
    public void setDisplayingStatistics(boolean displayingStatistics) {
        if (this.displayingStatistics != displayingStatistics) {
            this.displayingStatistics = displayingStatistics;
            repaint();
        }
    }

    /**
     * Returns the displayedItems.
     * @return int
     */
    public int getDisplayedItems() {
        return displayedItems;
    }

    /**
     * Returns the redisplayTime.
     * @return long
     */
    public long getRedisplayTime() {
        return redisplayTime;
    }    
}
