/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Visualization;
import infovis.visualization.VisualizationInteractor;
import infovis.visualization.VisualizationProxy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.Component;

/**
 * Visualization wrapping a DefaultExcentricLabel.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class ExcentricLabelVisualization extends VisualizationProxy {
    protected ExcentricLabels excentric;
    protected VisualizationInteractor interactor;
    protected Component parent;
    protected Visualization visualization;

    /**
     * Creates an ExcentricLabelVisualization.
     * @param visualization the visualization
     * @param el the ExcentricLabels
     */
    public ExcentricLabelVisualization(
        Visualization visualization,
        ExcentricLabels el) {
        super(null);
        this.visualization = visualization;
        if (el == null) {
            el = new DefaultExcentricLabels();
        }
        this.excentric = el;
        this.excentric.setVisualization(visualization);
        //setFisheye(visualization.getFisheye());
    }

    /**
     * Creates an ExcentricLabelVisualization.
     * @param visualization the visualization
     */
    public ExcentricLabelVisualization(Visualization visualization) {
        this(visualization, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParent(Component parent) {
        Component old = getParent();
        if (old != parent) {
            uninstall(old);
            install(parent);
            this.parent = parent;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Component getParent() {
        return parent;
    }
    
    protected void uninstall(Component parent) {
        if (parent == null || interactor == null) return;
        if (getInteractor() != null) {
            getInteractor().uninstall(parent);
        }
    }

    protected void install(Component parent) {
        if (getInteractor() != null) {
            getInteractor().install(parent);
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (excentric != null)
            excentric.paint(graphics, bounds);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        setExcentric(null);
    }

    /**
     * {@inheritDoc}
     */
    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(this.getClass())) return this;
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        paint(graphics, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return getParent();
    }
    /**
     * Returns the excentric.
     * @return ExcentricLabels
     */
    public ExcentricLabels getExcentric() {
        return excentric;
    }
    
    protected void setExcentric(ExcentricLabels el) {
        if (excentric == el)
            return;
        if (excentric != null) {
            excentric.setVisualization(null);
            if (interactor != null)
                interactor.uninstall(getComponent());
        }
        excentric = el;
        if (excentric != null) {
            excentric.setVisualization(this);
            //setFisheye(super.getFisheye());
            if (interactor != null)
                interactor.install(getComponent());
        }
    }
    
    /**
     * Finds the <code>ExcentricLabelVisualization</code> in the
     * specified <code>Visualization</code>.
     * @param vis the visualization
     * @return an <code>ExcentricLabelVisualization</code> or null
     */
    public static ExcentricLabelVisualization find(Visualization vis) {
        return (ExcentricLabelVisualization)vis
            .findVisualization(ExcentricLabelVisualization.class);
    }

    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor inter) {
        if (this.interactor == inter) return;
        if (this.interactor != null) {
            this.interactor.setVisualization(null);
        }
        this.interactor = inter;
        if (this.interactor != null) {
            this.interactor.setVisualization(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        return interactor;
    }
    
    /**
     * @return true if the excentric is enabled
     */
    public boolean isEnabled() {
        return excentric.isEnabled();
    }
    
    /**
     * Sets the excentric enabled.
     * @param enabled value
     */
    public void setEnabled(boolean enabled) {
        excentric.setEnabled(enabled);
    }
}
