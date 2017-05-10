/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.Visualization;
import infovis.visualization.DefaultVisualization;
import infovis.visualization.ItemRenderer;
import infovis.visualization.VisualizationInteractor;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

/**
 * <b>DefaultVisualizationInteractor</b> is the default implementation of a visualization interactor.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.DefaultVisualization
 * @infovis.factory InteractorFactory infovis.table.visualization.TimeSeriesVisualization infovis.visualization.DefaultVisualization
 */
public class DefaultVisualizationInteractor extends BasicVisualizationInteractor {
    protected ArrayList interactors;
        
    /**
     * Returns the DefaultVisualizationInteractor associated with a specified
     * visualization or null.
     * @param vis the visualization
     * @return a DefaultVisualizationInteractor or null.
     */
    public static DefaultVisualizationInteractor get(Visualization vis) {
        VisualizationInteractor inter = vis.getInteractor();
        if (inter instanceof DefaultVisualizationInteractor) {
            return (DefaultVisualizationInteractor) inter;
        }
        return null;
    }
    
    /**
     * Constructor. 
     */
    public DefaultVisualizationInteractor() {
    }

    /**
     * Constructor with a visualization.
     * @param vis the visualization
     */
    public DefaultVisualizationInteractor(Visualization vis) {
        super(vis);
    }
    
    /**
     * Constructor with a visualization.
     * @param vis the visualization
     */
    public DefaultVisualizationInteractor(DefaultVisualization vis) {
        super(vis);
    }
    

    /**
     * @return the default visualization associated with this interactor
     */
    public DefaultVisualization getDefaultVisualization() {
        return (DefaultVisualization)getVisualization();
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(Component comp) {
        if (parent != null) {
            if (parent == comp) {
                return;
            }
            throw new java.lang.RuntimeException("Error installing already installed interactor");
        }
        createInteractors(getVisualization().getItemRenderer(), comp);
        parent = comp;
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null)
                inter.install(comp);
        }
//
//        if (parent != null) {
//            parent.addMouseListener(this);
//            parent.addMouseMotionListener(this);
//            parent.addMouseWheelListener(this);
//            parent.addKeyListener(this);
//        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void uninstall(Component comp) {
        if (parent == null) return;
        for (int i = 0; i < getInteractorCount(); i++) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null)
                inter.uninstall(comp);
        }
        uninstallInteractors(getVisualization().getItemRenderer());
//        parent.addMouseListener(this);
//        parent.addMouseMotionListener(this);
//        parent.addMouseWheelListener(this);
//        parent.addKeyListener(this);
        super.uninstall(comp);

    }
    
    /**
     * {@inheritDoc}
     */
    public void setVisualization(Visualization vis) {
        super.setVisualization(vis);
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if (inter != null)
                inter.setVisualization(getVisualization());
        }
    }
    
    protected void createInteractors(ItemRenderer ir, Component comp) {
        BasicVisualizationInteractor inter = 
            RendererInteractorFactory.createInteractor(ir);
        if (inter != null) {
            addInteractor(inter);
        }
        for (int i = 0; i < ir.getRendererCount(); i++) {
            createInteractors(ir.getRenderer(i), comp);
        }
    }
    
    /**
     * Adds an interactor.
     * @param inter the interactor
     */
    public void addInteractor(BasicVisualizationInteractor inter) {
        assert(indexOf(inter)==-1);
        getInteractors().add(inter);
        inter.setVisualization(getVisualization());
    }

    protected void uninstallInteractors(ItemRenderer ir) {
        for (int i = getInteractorCount()-1; i >= 0; i--) {
            removeInteractor(i);
        }
    }

    /**
     * Removes an interactor at a specified index.
     * @param i the index of the interactor
     */
    public void removeInteractor(int i) {
        VisualizationInteractor inter = getInteractor(i);
        if (inter != null)
            inter.setVisualization(null);
        getInteractors().remove(i);
        assert(indexOf(inter)==-1);
    }
    
    /**
     * Returns the index of the specified interactor or -1.
     * @param inter the interactor
     * @return the index or -1
     */
    public int indexOf(VisualizationInteractor inter) {
        for (int i = getInteractorCount(); --i > 0; ) {
            VisualizationInteractor inter2 = getInteractor(i);
            if (inter2 == inter)
                return i;
        }
        return -1;
    }
    
    /**
     * Returns the index of the interactor of the specified class or -1.
     * @param cls the class
     * @return the index or -1
     */
    public int indexOf(Class cls) {
        for (int i = 0; i < getInteractorCount(); i++) {
            VisualizationInteractor inter = getInteractor(i);
            if (cls.isInstance(inter)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Removes the specified interactor.
     * @param inter the interactor
     */
    public void removeInteractor(BasicVisualizationInteractor inter) {
        int index = indexOf(inter);
        if (index != -1) {
            removeInteractor(index);
        }
    }

    private ArrayList getInteractors() {
        if (interactors == null) {
            interactors = new ArrayList();
        }
        return interactors;
    }

    /**
     * Returns the interactor at the specified index. 
     * @param index the index
     * @return the interactor at the specified index
     */
    public VisualizationInteractor getInteractor(int index) {
        return (VisualizationInteractor)getInteractors().get(index);
    }
    
    /**
     * @return the number of interactors
     */
    public int getInteractorCount() {
        if (interactors == null) return 0;
        return interactors.size();
    }


    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
        for (int i = getInteractorCount(); --i >= 0; ) {
            VisualizationInteractor inter = getInteractor(i);
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
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
            if  (inter == null) continue;
            if (inter.isConsideringEvent(e)) {
                inter.keyTyped(e);
            }
        }
    }

}
