/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.Visualization;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JViewport;

/**
 * <b>PanningInteractor</b> implements panning with the second mouse button.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.DefaultVisualization
 */
public class PanningInteractor extends BasicVisualizationInteractor {
    protected JViewport viewport;
    protected int lastX;
    protected int lastY;

    /**
     * Constructor.
     */
    public PanningInteractor() {
        super();
    }

    /**
     * Constructor with a visualization.
     * @param vis the visualization
     */
    public PanningInteractor(Visualization vis) {
        super(vis);
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(Component comp) {
        if (parent == comp) return;
        super.install(comp);
//        comp.addMouseListener(this);
//        comp.addMouseMotionListener(this);
        Container cont;
        for (cont = comp.getParent(); 
            cont != null && ! (cont instanceof JViewport); 
            cont = cont.getParent()) 
            ;
        viewport = (JViewport)cont; // either null or JViewport
    }
    
    /**
     * {@inheritDoc}
     */
    public void uninstall(Component comp) {
//        comp.removeMouseListener(this);
//        comp.removeMouseMotionListener(this);
        viewport = null;
        super.uninstall(comp);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isConsideringEvent(InputEvent e) {
        return (e.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK) != 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        if (viewport == null || !isConsideringEvent(e)) {
            return;
        }
        lastX = e.getX();
        lastY = e.getY();
        viewport.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        if (viewport == null || !isConsideringEvent(e)) {
            return;
        }
        Rectangle r = viewport.getViewRect();
        Dimension size = viewport.getViewSize();
        r.x -= e.getX()-lastX;
        if (r.x < 0) r.x = 0;
        else if (r.getMaxX() > size.width) {
            r.x = size.width - r.width;
        }
        r.y -= e.getY()-lastY;
        if (r.y < 0) r.y = 0;
        else if (r.getMaxY() > size.height) {
            r.y = size.height - r.height;
        }
        viewport.setViewPosition(r.getLocation());
//        lastX = e.getX();
//        lastY = e.getY();
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        if (viewport == null || !isConsideringEvent(e)) {
            viewport.setCursor(null);
        }
    }
}
