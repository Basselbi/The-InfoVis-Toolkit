/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.AbstractControlPanel;
import infovis.panel.FilteredColumnListModel;
import infovis.visualization.render.AbstractVisualColumn;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Base class for panels showing the configuration of visual column
 * descriptors 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public abstract class AbstractVisualColumnPanel extends AbstractControlPanel 
    implements PropertyChangeListener, ListDataListener {
    protected String propName;
    protected AbstractVisualColumn vc;
    protected FilteredColumnListModel model;
    protected JComboBox combo;
    
    /**
     * Creates a panel from a visual column.
     * @param vc the visual column
     */
    public AbstractVisualColumnPanel(AbstractVisualColumn vc) {
        super(vc.getVisualization());
        this.vc = vc;
        this.propName = Visualization.VC_DESCRIPTOR_PROPERTY_PREFIX+getName();
        getVisualization().addPropertyChangeListener(propName, this);
        String title = 
            getName().substring(0, 1).toUpperCase() + getName().substring(1);
        setTitleBorder(this, title);
    }
    
    protected void createCombo() {
        model = new FilteredColumnListModel(getTable(), getFilter());
        combo = createJCombo(model, null, null); // "Column");        
    }
    
    /**
     * @return the component for selecting the column
     */
    public JComponent getColumnSelector() {
        return combo;
    }

    public void update() {
        if (model != null) {
            model.setSelectedItem(vc.getColumn());
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(propName)) {
            update();
        }
    }
    public String getName() {
        return vc.getName();
    }
    
    public String getPropName() {
        return propName;
    }
    
    public AbstractVisualColumn getVc() {
        return vc;
    }
    
    public Table getTable() {
        return getVisualization().getTable();
    }
    
    public ColumnFilter getFilter() {
        return vc;
    }
    /**
     * @see javax.swing.event.ListDataListener#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == model) {
            getVisualization().setVisualColumn(
                getName(), 
                (Column)combo.getSelectedItem());
       }
    }

    /**
     * @see javax.swing.event.ListDataListener#intervalAdded(ListDataEvent)
     */
    public void intervalAdded(ListDataEvent e) {
    }

    /**
     * @see javax.swing.event.ListDataListener#intervalRemoved(ListDataEvent)
     */
    public void intervalRemoved(ListDataEvent e) {
    }
    
 }
