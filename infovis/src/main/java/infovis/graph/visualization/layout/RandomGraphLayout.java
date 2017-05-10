/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import java.awt.geom.Rectangle2D.Float;

import cern.jet.random.engine.RandomEngine;

/**
 * <b>RandomGraphLayout</b> layouts a graph at random.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * @infovis.factory GraphLayoutFactory "Random"
 */
public class RandomGraphLayout extends AbstractGraphLayout {
    protected RandomEngine engine;
    
    /**
     * Creates a RandomGraphLayout.
     */
    public RandomGraphLayout() {
        engine = RandomEngine.makeDefault();
    }

    /**
     * Creates a RandomGraphLayout with the specified random engine.
     * @param engine the engine
     */
    public RandomGraphLayout(RandomEngine engine) {
        this.engine = engine;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Random Graph Layout";
    }
    
    protected double nextDouble() {
        return getEngine().nextDouble();
    }
    
    protected Float initRect(Float rect) {
        rect.x = (float)(nextDouble() * bounds.getWidth());
        rect.y = (float)(nextDouble() * bounds.getHeight());
        
        return rect;
    }
    
    /**
     * @return the random engine
     */
    public RandomEngine getEngine() {
        return engine;
    }
    
    /**
     * Sets the random engine.
     * @param engine the new engine to set
     */
    public void setEngine(RandomEngine engine) {
        this.engine = engine;
        invalidateVisualization();
    }
}
