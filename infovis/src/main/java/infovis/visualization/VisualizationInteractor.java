/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * <b>VisualizationInteractor</b> is the interface of interactors
 * managing a visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public interface VisualizationInteractor 
    extends MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    /**
     * Installs the interactor on the specified component.
     * @param comp the JComponent
     */
    void install(Component comp);
    /**
     * Uninstall the interactor from the specified component.
     * @param comp the JComponent
     */
    void uninstall(Component comp);
    /**
     * @return the associated visualization
     */
    Visualization getVisualization();
    /**
     * Sets the specified visualization.
     * @param vis the visualization
     */
    void setVisualization(Visualization vis);
    /**
     * @return the associated Component
     */
    Component getComponent();
    
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
    boolean isConsideringEvent(InputEvent e);
}