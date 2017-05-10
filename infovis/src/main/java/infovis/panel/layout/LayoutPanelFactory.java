/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.panel.layout;

import infovis.Visualization;
import infovis.utils.BasicFactory;
import infovis.visualization.Layout;

import javax.swing.JComponent;

/**
 * Class LayoutPanelFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class LayoutPanelFactory extends BasicFactory {
    private static final LayoutPanelFactory instance = new LayoutPanelFactory();
    
    public LayoutPanelFactory getInstance() {
        return instance;
    }
    
    /**
     * @param name
     */
    public LayoutPanelFactory() {
        super("LayoutPanelFactory");
    }
    
    public static JComponent create(String name, Visualization vis) {
        //TODO
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String className, String data) {
        
    }

}
