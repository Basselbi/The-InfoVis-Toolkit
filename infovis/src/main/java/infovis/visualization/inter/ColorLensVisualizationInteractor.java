/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.visualization.magicLens.ColorLensVisualization;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class ColorLensVisualizationInteractor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.magicLens.ColorLensVisualization
 */
public class ColorLensVisualizationInteractor extends
        BasicVisualizationInteractor {
    protected ColorLensVisualization colorLens;
    int lastX = -1;
    int lastY = -1;
    
    /**
     * Constructor with a ColorLensVisualization.
     * @param vis the visualization
     */
    public ColorLensVisualizationInteractor(ColorLensVisualization vis) {
        super(vis);
        colorLens = vis;
    }
    
    /**
     * @return the colorLens
     */
    public ColorLensVisualization getColorLens() {
        return colorLens;
    }
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        if (colorLens.pick(e.getX(), e.getY())) {
            lastX = e.getX();
            lastY = e.getY();
            e.consume();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        if (lastX != -1) {
            lastX = -1;
            lastY = -1;
            e.consume();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        if (lastX != -1) {
            int dx = e.getX() - lastX;
            int dy = e.getY() - lastY;
            moveLensBy(dx, dy);
            lastX = e.getX();
            lastY = e.getY();
            e.consume();
        }
    }
    
    /**
     * Moves the lens by the specified amount.
     * @param dx the delta x
     * @param dy the delta y
     */
    public void moveLensBy(int dx, int dy) {
        if (dx == 0 && dy == 0) return;
        Rectangle len = colorLens.getLens();
        
        colorLens.setLens(new Rectangle(len.x+dx, len.y+dy, len.width, len.height));
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (colorLens.pick(e.getX(), e.getY())) {
            int r = e.getWheelRotation();
            Rectangle len = colorLens.getLens();
            int w = Math.max(len.width, len.height);
            w += 2*r;
            if (w < 10) w = 10;
            else if (w > 200) w = 200;
            if (len.width == w && len.height == w) return;
            int dx = (len.width - w)/2;
            int dy = (len.height - w)/2;
            colorLens.setLens(new Rectangle(len.x+dx, len.y+dy, w, w));
        }
    }
}
