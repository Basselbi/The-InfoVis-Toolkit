/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.ColumnFilter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Base class for creating Visual Panels.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public abstract class AbstractControlPanel extends Box
    implements ListDataListener, ActionListener, ChangeListener {
    private Visualization visualization;
    /** Dimension for sliders. */
    public static final Dimension MAX_SLIDER_DIMENSION = new Dimension(Integer.MAX_VALUE, 60);

    /**
     * Constructor for AbstractControlPanel.
     * @param vis the visualization to control
     */
    public AbstractControlPanel(Visualization vis) {
        super(BoxLayout.Y_AXIS);
        this.visualization = vis;
    }
    
    /**
     * Creates a title border around a specified component.
     * @param comp the JComponent to decorate
     * @param title the title
     */
    public static void setTitleBorder(JComponent comp, String title) {
        comp.setBorder(BorderFactory.createTitledBorder(title));
    }
    
    protected JComboBox createColumnCombo(Table table, ColumnFilter filter) {
        return createJCombo(
                new FilteredColumnListModel(table, filter),
                null, 
                null);
    }
    
    protected JComboBox createJCombo(
            FilteredColumnListModel model, 
            Column c,
            String label) {
        model.setNullAdded(true);
        JComboBox combo = new JComboBox(model);
        model.setSelectedItem(c);
        combo.setRenderer(new ColumnListCellRenderer());
        return addJCombo(label, combo);
    }

    protected JComboBox addJCombo(
        String label,
        JComboBox combo) {
        setTitleBorder(combo, label);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                           (int)combo.getPreferredSize()
                                                     .getHeight()));
        combo.setAlignmentX(LEFT_ALIGNMENT);
        add(combo);
        combo.getModel().addListDataListener(this);
        return combo;
    }
    
    /**
     * Sets the maximum size of a specified component (slider like).
     * @param rs the component
     */
    public static void setMaximumSize(JComponent rs) {
        rs.setMaximumSize(MAX_SLIDER_DIMENSION);
    }

    /**
     * @see javax.swing.event.ListDataListener#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
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
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
    }

    /**
     * Returns the visualization.
     * @return the Visualization
     */
    public Visualization getVisualization() {
        return visualization;
    }

}
