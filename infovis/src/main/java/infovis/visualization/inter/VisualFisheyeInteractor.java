/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.visualization.ItemRenderer;
import infovis.visualization.magicLens.Fisheye;
import infovis.visualization.render.VisualFisheye;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class VisualFisheyeInteractor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * 
 * @infovis.factory RendererInteractorFactory infovis.visualization.render.VisualFisheye
 */
public class VisualFisheyeInteractor extends
        BasicVisualizationInteractor {
    protected VisualFisheye renderer;

    /**
     * Constructor.
     * @param renderer the item renderer
     */
    public VisualFisheyeInteractor(ItemRenderer renderer) {
        this.renderer = (VisualFisheye)renderer;
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    public void install(Component comp) {
//        if (parent == comp) return;
//        super.install(comp);
////        comp.addMouseMotionListener(this);
////        comp.addMouseWheelListener(this);
//    }
//    
//    /**
//     * {@inheritDoc}
//     */
//    public void uninstall(Component comp) {
//        comp.removeMouseMotionListener(this);
//        comp.removeMouseWheelListener(this);
//        super.uninstall(comp);
//    }
    
    /**
     * {@inheritDoc}
     */
    public void repaint() {
        Fisheye fisheye = renderer.getFisheye();
        if (fisheye != null
            && fisheye.isEnabled()) {
            super.repaint();
        }        
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {
        Fisheye fisheye = renderer.getFisheye();
        if (fisheye != null) {
            fisheye.setLens(e.getX(), e.getY());
            repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        Fisheye fisheye = renderer.getFisheye();

        if (fisheye != null) {
            float height = fisheye.getFocusHeight() + e.getWheelRotation();
            fisheye.setFocusHeight(height);
            repaint();
        }
    }    
}
