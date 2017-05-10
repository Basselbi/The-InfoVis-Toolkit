/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * Class NodeLinkGraphLayoutPanelFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class NodeLinkGraphLayoutPanelFactory extends BasicFactory {
    private static NodeLinkGraphLayoutPanelFactory instance;
    private static Logger LOG = Logger.getLogger(NodeLinkGraphLayoutPanelFactory.class);

    public static NodeLinkGraphLayoutPanelFactory getInstance() {
        if (instance == null) {
            instance = new NodeLinkGraphLayoutPanelFactory();
        }
        return instance;
    }

    public NodeLinkGraphLayoutPanelFactory() {
        addDefaultCreators("nodelinkgraphlayoutpanelfactory");
    }

    public void add(String name, String className, String data) {
        try {
            Class dataClass = Class.forName(className);
            Constructor cons = dataClass.getConstructor((Class[])null);
            add((Creator) cons.newInstance((Object[])null));
        } catch (Exception e) {
            LOG.error("Cannot instantiate class "+className, e);
        }
    }

    public void add(Creator c) {
        putEntry(c.getName(), c);
    }

    public interface Creator {
        public String getName();

        public JComponent create(NodeLinkGraphVisualization vis);
    }

}