/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.Visualization;
import infovis.visualization.VisualizationInteractor;
import infovis.visualization.VisualizationLayers;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class VisualizationLayersInteractor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.VisualizationLayers
 */
public class VisualizationLayersInteractor extends BasicVisualizationInteractor {

    /**
     * 
     */
    public VisualizationLayersInteractor() {
    }

    /**
     * @param vis
     */
    public VisualizationLayersInteractor(VisualizationLayers vis) {
        super(vis);
    }
    
    /**
     * Returns the interactor at the specified index.
     * @param i the index
     * @return the interactor or null
     */
    public VisualizationInteractor getInteractor(int i) {
        Visualization vis = visualization.getVisualization(i);
        if (vis == null) return null;
        VisualizationInteractor inter = vis.getInteractor();
        return inter;
    }
    
    /**
     * @return the number of interactors.
     */
    public int getInteractorCount() {
        int i;
        for (i = 0; visualization.getVisualization(i) != null; i++) 
            ;
        return i;
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseClicked(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseEntered(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseExited(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mousePressed(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseReleased(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseDragged(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseMoved(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.mouseWheelMoved(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void keyPressed(KeyEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.keyPressed(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void keyReleased(KeyEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.keyReleased(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void keyTyped(KeyEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null && inter.isConsideringEvent(e)) {
                inter.keyTyped(e);
            }
        }
    }

}
