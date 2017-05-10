/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.Visualization;
import infovis.utils.BasicFactory;
import infovis.visualization.VisualizationInteractor;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Factory for VisualizationInteractor objects.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class InteractorFactory extends BasicFactory {
    private static InteractorFactory instance;
    private static Logger LOG = Logger.getLogger(InteractorFactory.class);
    
    /**
     * @return the singleton instance of this class.
     */
    public static InteractorFactory getInstance() {
        if (instance == null) {
            instance = new InteractorFactory();
        }
        return instance;
    }
    
    /**
     * Sets the singleton instance of this class.
     * @param i the new singleton
     */
    public static void setInstance(InteractorFactory i) {
        instance = i;
    }
    
    /**
     * Returns the creator associated with the specified visualization.
     * @param vis the visualization
     * @return the creator associated with the specified visualization
     * or <code>null</code>
     */
    public static Creator getVisualizationInteractorCreator(Visualization vis) {
        return getInstance().getCreator(vis);
    }
    
    /**
     * Returns the Interactor associated with the specified visualization.
     * @param vis the visualization
     * @return a VisualizationInteractor or null
     */
    public static VisualizationInteractor 
        createVisualizationInteractor(Visualization vis) {
        Creator c = getVisualizationInteractorCreator(vis);
        if (c != null) {
            return c.create(vis);
        }
        return null;
    }
    
    /**
     * Installs the interactor on the specified visualization, creating
     * it if required and initializes the sub interactors.
     * @param vis the visualization
     */
    public static void installInteractor(Visualization vis) {
        if (vis.getInteractor() == null) {
            vis.setInteractor(createVisualizationInteractor(vis));
        }
        int i = 0;
        for (Visualization sub = vis.getVisualization(i++);
            sub != null;
            sub = vis.getVisualization(i++)) {
            installInteractor(sub);
        }
    }
    
    /**
     * Default constructor.
     */
    public InteractorFactory() {
        addDefaultCreators("interactorfactory");
    }

    /**
     * Returns a Creator for the specified visualization or null.
     * @param vis the visualization
     * @return a Creator or null
     */
    public Creator getCreator(Visualization vis) {
        Class visClass = Visualization.class;
        for (Class c = vis.getClass(); 
            visClass.isAssignableFrom(c);
            c = c.getSuperclass()) {
            Creator creator = (Creator)getEntry(c.getName());
            if (creator != null) {
                return creator;
            }
        }
        //LOG.info("No interactor creator for "+vis.getClass());
        return null;
    }
    
    /**
     * Removes the specified creator.
     * @param c the creator
     * @return <code>true</code> if it has been removed
     */
    public boolean removeCreator(Creator c) {
        return entry.remove(c) != null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, String className, String data) {
        if (data == null) {
            putEntry(name, new DefaultCreator(name, className));
        }
        else {
            putEntry(name, new DefaultCreator(data, className));
        }
    }
    
    /**
     * <b>Creator</b> is the interface for interactor creators.
     * 
     * @author Jean-Daniel Fekete
     */
    public interface Creator {
        /**
         * @return the visualization class name
         */
        public abstract String getVisualizationClassName();
        /**
         * Creates an interactor for a specified visualization
         * @param vis the visualization
         * @return an interactor or null
         */
        public abstract VisualizationInteractor create(Visualization vis);
    }
    
    /**
     * <b>DefaultCreator</b> is the default implementation
     * of a Creator.
     * 
     * @author Jean-Daniel Fekete
     */
    public static class DefaultCreator implements Creator {
        protected String visClassName;
        protected Class visClass;
        protected String interClassName;
        protected Class interClass;
        
        /**
         * Construtor.
         * @param visClassName the visualization class name
         * @param interClassName the interactor class name
         */
        public DefaultCreator(String visClassName, String interClassName) {
            this.interClassName = interClassName;
            this.visClassName = visClassName;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getVisualizationClassName() {
            return visClassName;
        }
        
        /**
         * {@inheritDoc}
         */
        public VisualizationInteractor create(Visualization vis) {
            if (interClass == null) {
                try {
                    interClass = Class.forName(interClassName);
                } catch (ClassNotFoundException e) {
                    LOG.error("Cannot load interactor class "+interClassName, e);
                    return null;
                }
            }
            if (visClass == null) {
                try {
                    visClass = Class.forName(visClassName);
                }
                catch (ClassNotFoundException e) {
                    LOG.error("Cannot load visualization class "+visClassName, e);
                }
            }
            Class[] parameterTypes = { visClass };

            Constructor cons;
            try {
                cons = interClass.getConstructor(parameterTypes);
            } catch (NoSuchMethodException e) {
                LOG.error("Cannot find constructor for "+interClassName, e);
                return null;
            }
            if (cons != null) {
                Object[] args = { vis };
                try {
                    return (VisualizationInteractor) cons.newInstance(args);
                } catch (Exception e) {
                    LOG.error(
                            "Cannot instantiate new interactor "+interClassName, 
                            e);
                }
            }
            return null;
        }
    }
}
