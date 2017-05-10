/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.panel.layout.LayoutPanel;
import infovis.visualization.Layout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * <b>NodeLinkLayoutPanel</b> is the base class of all panels
 * for NodeLinkVisualizationLayout.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class NodeLinkLayoutPanel extends LayoutPanel {
    JTable jtable;
    DefaultTableModel model;
    NodeLinkGraphLayout layout;
    /** column names. */
    public static final String[] COLUMN_NAMES = { "Name", "Value" };
    
    /**
     * Creates a Panel.
     * @param vis the visualization
     */
    public NodeLinkLayoutPanel(Layout l) {
        super(l.getVisualization());
        this.layout = (NodeLinkGraphLayout)l;
        JLabel name = new JLabel(layout.getName());
        add(name);
        model = new DefaultTableModel(COLUMN_NAMES, 1);
        jtable = new JTable(model);
        JScrollPane jscroll = new JScrollPane(jtable);
        jscroll.setColumnHeaderView(jtable.getTableHeader());
        add(jscroll);
        
    }
}
