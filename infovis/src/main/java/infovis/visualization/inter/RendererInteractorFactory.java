/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.utils.BasicFactory;
import infovis.visualization.ItemRenderer;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Factory of ItemInteractors.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class RendererInteractorFactory extends BasicFactory {
    private static RendererInteractorFactory instance;
    private static Logger LOG = Logger.getLogger(RendererInteractorFactory.class);

    /**
     * @return the instance
     */
    public static RendererInteractorFactory getInstance() {
        if (instance == null) {
            instance = new RendererInteractorFactory();
        }
        return instance;
    }
    
    /**
     * Sets the default instance.
     * @param inst the new instance
     */
    public static void setInstance(RendererInteractorFactory inst) {
        instance = inst;
    }
    
    /**
     * Creates an interactor from a specified renderer.
     * @param renderer the renderer
     * @return an interactor or null
     */
    public static BasicVisualizationInteractor createInteractor(ItemRenderer renderer) {
        return getInstance().create(renderer);
    }
    
    protected RendererInteractorFactory() {
        super("rendererinteractorfactory");
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, String className, String data) {
        if (getEntry(name) != null) {
            LOG.info("Replacing Creator for "+name+" by "+className); 
        }
        putEntry(name, new Creator(className));
    }
    
    /**
     * Removes the class with the specified name.
     * @param name the name
     * @return true if it has been removed
     */
    public boolean remove(String name) {
        return entry.remove(name) != null;
    }
    
    /**
     * Creates an interactor from a renderer.
     * @param renderer the renderer
     * @return the interactor or null
     */
    public BasicVisualizationInteractor create(ItemRenderer renderer) {
        for (Class cls = renderer.getClass(); 
            cls != Object.class; 
            cls = cls.getSuperclass()) {
            Creator c = (Creator)getEntry(cls.getName());
            if (c != null) {
                return c.create(renderer);
            }
        }
        return null;
    }

    /**
     * <b>Creator</b> is the base class for creators.
     * 
     * @author Jean-Daniel Fekete
     */
    public static class Creator {
        protected String rendererClassName;
        protected Class rendererClass;
        
        /**
         * Constructor.
         * @param rendererClassName the class name
         */
        public Creator(String rendererClassName) {
            this.rendererClassName = rendererClassName;
        }
        
        /**
         * Creates the interator from a specified renderer.
         * @param renderer the renderer
         * @return the interactor or null
         */
        public BasicVisualizationInteractor create(ItemRenderer renderer) {
            try {
                if (rendererClass == null) {
                    rendererClass = Class.forName(rendererClassName);
                }
                Class[] parameterTypes = { ItemRenderer.class };
                Constructor cons = rendererClass.getConstructor(parameterTypes);
                Object[] args = { renderer }; 
                return (BasicVisualizationInteractor)cons.newInstance(args);
            }
            catch(Exception e) {
                LOG.error("Cannot instantiate new RendererInteractor ", e);
            }
            return null;
        }
    }
}
