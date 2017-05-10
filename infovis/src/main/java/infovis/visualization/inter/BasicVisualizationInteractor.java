/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.Visualization;
import infovis.visualization.VisualizationInteractor;
import infovis.visualization.VisualizationProxy;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Base class for VisualizationInteractor.  Also useful as a null interactor.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.StrokingVisualization infovis.Visualization
 */
public class BasicVisualizationInteractor
    extends VisualizationProxy
    implements VisualizationInteractor {
    protected Component parent;
    
    /**
     * Constructor.
     */
    public BasicVisualizationInteractor() {
        super(null);
    }
    
    /**
     * Constructor with a visualization.
     * @param vis the visualization
     */
    public BasicVisualizationInteractor(Visualization vis) {
        super(null);
        setVisualization(vis);
    }
    
    /**
     * Try to install this BasicVisualizationInteractor on the specified 
     * visualization.
     * @param vis the visualization
     * @return <code>true</code> if the interactor has been successfully install
     * or <code>false</code> otherwise
     */
    public boolean install(Visualization vis) {
        DefaultVisualizationInteractor inter = DefaultVisualizationInteractor.get(vis);
        if (vis != null) {
            inter.addInteractor(this);
        }
//        else if (vis.getInteractor() == null) {
//            vis.setInteractor(this);
//        }
        else {
            return false;
        }
        return true;
    }
    
    /**
     * Filter for considering an input event.
     * 
     * <p>Used to specialize an interactor for different
     * mouse buttons or keys.
     * 
     * @param e the input event to consider
     * @return <code>true</code> if the event should be
     * considered.
     */
    public boolean isConsideringEvent(InputEvent e) {
        return !e.isConsumed();
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void keyPressed(KeyEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void keyReleased(KeyEvent e) {
    }
    /**
     * {@inheritDoc}
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization() {
        return visualization;
    }

    /**
     * {@inheritDoc}
     */
    public void install(Component comp) {
        this.parent = comp;
    }

    /**
     * {@inheritDoc}
     */
    public void setVisualization(Visualization vis) {
        if (this.visualization == vis) return;
        if (visualization != null && visualization.getParent() != null) {
            uninstall(visualization.getParent());
        }
        this.visualization = vis;
        if (visualization != null) {
            //vis.setInteractor(this);
            if (visualization.getParent() != null) {
                install(visualization.getParent());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void uninstall(Component comp) {
//  frequently not true; don't know if it's a problem      assert(comp==parent);
        this.parent = null;
    }
    
}
