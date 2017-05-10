/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.panel;

import infovis.Visualization;
import infovis.panel.layout.LayoutPanelFactory;
import infovis.visualization.Layout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JComponent;

/**
 * <b>LayoutControlPanel</b> is a control panel to
 * display layout specific controls.
 * 
 * TODO not finished.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class LayoutControlPanel extends AbstractControlPanel 
    implements PropertyChangeListener {
    protected Layout layout;
    protected JComponent layoutComp;
    protected HashMap layoutComps = new HashMap();
   
    /**
     * Constructor.
     * @param vis the visualization
     */
    public LayoutControlPanel(Visualization vis) {
        super(vis);
        vis.addPropertyChangeListener(Visualization.PROPERTY_LAYOUT, this);
    }
    
    /**
     * Sets the managed layout.
     * @param layout the layout
     */
    public void setLayout(Layout layout) {
        if (this.layout == layout) return;
        if (layoutComp != null) {
            remove(layoutComp);
        }
        layoutComp = (JComponent)layoutComps.get(layout.getName());
        if (layoutComp == null) {
            layoutComp = LayoutPanelFactory.create(layout.getName(), getVisualization());
            if (layoutComp != null) {
                layoutComps.put(layout.getName(), layoutComp);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource()==getVisualization() 
                && evt.getPropertyName().equals(Visualization.PROPERTY_LAYOUT)) {
            setLayout(getVisualization().getLayout());
        }
    }
}
