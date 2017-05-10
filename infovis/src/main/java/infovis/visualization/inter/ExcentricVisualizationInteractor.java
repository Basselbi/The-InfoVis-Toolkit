/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.visualization.magicLens.ExcentricLabelVisualization;
import infovis.visualization.magicLens.ExcentricLabels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

/**
 * Interactor for excentric labels.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.magicLens.ExcentricLabelVisualization
 */
public class ExcentricVisualizationInteractor extends
        BasicVisualizationInteractor {
    protected ExcentricLabels excentric;
    protected Timer insideTimer;
    protected int threshold = 20;
    protected boolean entered;
  
    /**
     * Creates an interactor.
     * @param vis the visualization
     */
    public ExcentricVisualizationInteractor(ExcentricLabelVisualization vis) {
        super(vis);
        insideTimer = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getExcentric().setVisible(true);
            }
        });
        insideTimer.setRepeats(false);
//        install(vis.getComponent());
    }
    
    /**
     * @return the Excentric
     */
    public ExcentricLabels getExcentric() {
        if (excentric == null) {
            excentric = getExcentricVisualization().getExcentric();
        }
        return excentric;
    }
    
    /**
     * @return an ExcentricLabelVisualization
     */
    public ExcentricLabelVisualization getExcentricVisualization() {
        return (ExcentricLabelVisualization)getVisualization();
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(Component comp) {
        if (parent == comp) return;
        super.install(comp);
        if (comp != null && entered) {
            restart();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void uninstall(Component comp) {
        if (comp != null) {
            setVisible(false);
            stop();
        }
        super.uninstall(comp);
    }

    /**
     * Restarts the timer.
     *
     */
    public void restart() {
        if (insideTimer != null)
            insideTimer.restart();
    }
    
    /**
     * Stops the timer.
     *
     */
    public void stop() {
        if (insideTimer != null)
            insideTimer.stop();
    }
    
    /**
     * Sets the excentric label visiblility.
     * @param v the visibility
     */
    public void setVisible(boolean v) {
        getExcentric().setVisible(v);
    }
    
    /**
     * 
     * @return true if the excentric is visible
     */
    public boolean isVisible() {
        return getExcentric().isVisible();
    }

    /**
     * Computes the squarred distance.
     * @param dx the delta x
     * @param dy the delta y
     * @return the squarred distance
     */
    public static float dist2(float dx, float dy) {
        return dx * dx + dy * dy;
    }

    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {
        entered = true;
        restart();
    }

    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {
        if (e.getModifiers() != 0) {
            return;
        }
        entered = false;
        stop();
        setVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        setVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {
        if (isVisible()) {
            if (e.getModifiers() != 0)
                return;
            if (dist2(
                    getExcentric().getLensX() - e.getX(), 
                    getExcentric().getLensY() - e.getY())
                > threshold * threshold) {
                setVisible(false);
                insideTimer.restart();
            }
        }

        getExcentric().setLens(e.getX(), e.getY());
    }

    /**
     * Returns the threshold.
     *
     * When the mouse moves a distance larger than this
     * threshold since the last event, excentric labels
     * are disabled.
     *
     * @return int
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Sets the threshold.
     *
     * When the mouse moves a distance larger than the
     * specified threshold since the last event, excentric
     * labels are disabled.
     *
     * @param threshold The threshold to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
