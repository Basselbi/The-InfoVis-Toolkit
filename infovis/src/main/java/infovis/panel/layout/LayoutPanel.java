/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.panel.layout;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import javax.swing.JTable;

import org.apache.log4j.Logger;

import infovis.Visualization;
import infovis.panel.AbstractControlPanel;
import infovis.utils.PropertyColumnModel;
import infovis.utils.PropertyTableModel;
import infovis.visualization.Layout;

/**
 * Class LayoutPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class LayoutPanel extends AbstractControlPanel 
    implements PropertyChangeListener {
    protected Layout layout;
    private static final Logger LOG = Logger.getLogger(LayoutPanel.class);
    private static final int ROW_HEIGHT = 20;
    
    /**
     * Creates a Layout Panel from a visualization and an associated Layout.
     * @param vis the visualization
     * @param layout the layout
     */
    public LayoutPanel(Visualization vis) {
        super(vis);
        vis.addPropertyChangeListener(Visualization.PROPERTY_LAYOUT, this);
        setLayout(vis.getLayout());
    }
    
    public void setLayout(Layout layout) {
        if (this.layout == layout) return;
        if (this.layout != null) {
            this.layout.removePropertyChangeListener(this);
        }
        removeAll();
        this.layout = layout;
        if (this.layout != null) {
            computeComponents();
            this.layout.addPropertyChangeListener(this);
        }
    }
    
    protected void computeComponents() {
        PropertyTableModel tableModel = new PropertyTableModel(layout);
      
        PropertyColumnModel columnModel = new PropertyColumnModel();
        JTable table = new JTable(tableModel, columnModel);
        table.setRowHeight(ROW_HEIGHT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        add(table);
//        try {
//            
//            
//            BeanInfo binfo = Introspector.getBeanInfo(layout.getClass());
//            PropertyDescriptor[] props = binfo.getPropertyDescriptors();
//            for (int i = 0; i < props.length; i++) {
//                PropertyDescriptor prop = props[i];
//                if (prop.getWriteMethod() != null) {
//                    PropertyEditor ed = prop.createPropertyEditor(layout);
//                    if (ed != null) {
//                        Component comp = ed.getCustomEditor();
//                        if (comp != null) {
//                            add(comp);
//                            return;
//                        }
//                    }
//                    Class c = prop.getPropertyType();
//                    
//                }
//            }
//        }
//        catch(IntrospectionException e) {
//            LOG.error("Cannot instantiate components for layout "+layout.getName(), e);
//        }
    }
    
    protected void updateComponents() {
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == layout) {
            updateComponents();
        }
        else if (evt.getSource() == getVisualization()) {
            setLayout(getVisualization().getLayout());
        }
        
    }
}
