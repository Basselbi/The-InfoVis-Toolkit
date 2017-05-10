/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Column;
import infovis.Graph;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.column.NumberColumn;
import infovis.column.filter.ComposeExceptFilter;
import infovis.column.filter.ExceptNamed;
import infovis.column.filter.InternalFilter;
import infovis.graph.property.InDegree;
import infovis.graph.property.OutDegree;
import infovis.metadata.ValueCategory;
import infovis.panel.ColumnListCellRenderer;
import infovis.panel.ControlPanel;
import infovis.panel.DefaultVisualPanel;
import infovis.panel.DetailTable;
import infovis.panel.DynamicQueryPanel;
import infovis.panel.FilteredColumnListModel;
import infovis.table.FilteredTable;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cern.colt.list.IntArrayList;

/**
 * Control panel for a Matrix BasicVisualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.31 $
 * 
 * @infovis.factory ControlPanelFactory infovis.graph.visualization.MatrixVisualization
*/
public class MatrixControlPanel extends ControlPanel {
    protected DynamicQueryPanel       rowQueryPanel;
    protected DynamicQueryPanel       columnQueryPanel;
    protected FilteredTable           filteredGraph;
    protected JCheckBox               squareEdges;
    protected FilteredColumnListModel catColumns;

    /**
     * Constructor for MatrixControlPanel.
     *
     * @param visualization
     */
    public MatrixControlPanel(Visualization visualization) {
        super(visualization);
    }

    /**
     * Returns the MatrixVisualization.
     *
     * @return the MatrixVisualization.
     */
    public MatrixVisualization getMatrix() {
        return (MatrixVisualization) getVisualization()
            .findVisualization(MatrixVisualization.class);
    }

    /**
     * Returns the Graph
     *
     * @return Return the Graph
     */
    public Graph getGraph() {
        return getMatrix().getGraph();
    }

    /**
     * Return a filtered graph for the vertex table
     *
     * @return a filtered graph for the vertex table
     */
    public FilteredTable getFilteredGraph() {
        if (filteredGraph == null) {
            filteredGraph =
                new FilteredTable(
                    getGraph().getVertexTable(),
                    new ComposeExceptFilter(
                        new ExceptNamed(OutDegree.OUTDEGREE_COLUMN),
                        new ComposeExceptFilter(
                            new ExceptNamed(InDegree.INDEGREE_COLUMN),
                            InternalFilter.sharedInstance())));
        }
        return filteredGraph;
    }
    
    protected void createOtherTabs() {
        super.createOtherTabs();
        int visual = tabs.indexOfTab("Visual");
        tabs.insertTab(
                "Row Visual", 
                null, 
                new JScrollPane(createRowVisualPanel()), 
                "Setting of visual attributes for the visualization of rows",
                visual+1);
        tabs.insertTab(
                "Column Visual",
                null,
                new JScrollPane(createColumnVisualPanel()),
                "Setting of visual attributes for the visualization of columns",
                visual+2);
    }
    
    protected JComponent createRowVisualPanel() {
        return new DefaultVisualPanel(
                getMatrix().getRowVisualization(),
                getFilteredGraph().getFilter());
    }
    
    protected JComponent createColumnVisualPanel() {
        return new DefaultVisualPanel(
                getMatrix().getColumnVisualization(),
                getFilteredGraph().getFilter());
    }
    
    protected JComponent createDetailControlPanel() {
        MatrixVisualization matrix = getMatrix();
        
        Box stack = Box.createVerticalBox();
        JComponent detail = super.createDetailControlPanel();
        detail.setBorder(BorderFactory.createTitledBorder("Edge details"));
        stack.add(detail);

        JScrollPane rowJScroll = DetailTable.createDetailJTable(
                getFilteredGraph(),
                matrix.getRowSelection());
        rowJScroll.setBorder(BorderFactory.createTitledBorder("Row details"));
        stack.add(rowJScroll);

        JScrollPane columnJScroll = DetailTable.createDetailJTable(
                getFilteredGraph(),
                matrix.getColumnSelection());
        columnJScroll.setBorder(BorderFactory.createTitledBorder("Column details"));
        stack.add(columnJScroll);
        return stack;
    }

    /**
     * @see infovis.panel.ControlPanel#createFiltersPane()
     */
    protected JComponent createFiltersControlPanel() {
        Box stack = new Box(BoxLayout.Y_AXIS);
        JComponent filters = super.createFiltersControlPanel();
        filters.setBorder(
            BorderFactory.createTitledBorder("Edge dynamic queries"));
        stack.add(filters);

        Box checkBoxes = new Box(BoxLayout.X_AXIS);
        squareEdges =
            new JCheckBox("Square Edges", getMatrix().isSquared());
        squareEdges.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMatrix().setSquared(squareEdges.isSelected());
            }
        });
        checkBoxes.add(squareEdges);
        stack.add(checkBoxes);
        
        catColumns = 
            new FilteredColumnListModel(
                    getGraph().getVertexTable(),
                    new ColumnFilter() {
                        public boolean filter(Column column) {
                            return column != null 
                            && ValueCategory.findValueCategory(column)
                                !=ValueCategory.TYPE_CATEGORIAL;
                        }
                    });
        catColumns.setNullAdded(true);
        JComboBox categoryCombo = new JComboBox(catColumns);
        catColumns.setSelectedItem(null);
        categoryCombo.setRenderer(new ColumnListCellRenderer());
        categoryCombo.setBorder(BorderFactory.createTitledBorder("Partition by"));
        categoryCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NumberColumn c = (NumberColumn)catColumns.getSelectedItem();
                if (c == null) {
                    getMatrix().setRowPermutation(null);
                    getMatrix().setColumnPermutation(null);
                }
                else {
                    Map map = new TreeMap();
                    for (RowIterator iter = c.iterator(); iter.hasNext(); ) {
                        int row = iter.nextRow();
                        String key = c.getValueAt(row);
                        if (map.containsKey(key)) {
                            int[] i = (int[])map.get(key);
                            i[0]++;
                        }
                        else {
                            int[] i = new int[2];
                            i[0] = 1;
                            i[1] = c.getIntAt(row);
                            map.put(key, i);
                        }
                    }
                    String[] columnNames = { "Value", "Count" };
                    DefaultTableModel tableModel = new DefaultTableModel(columnNames, map.size());
                    
                    int row = 0;
                    for (Iterator iter = map.entrySet().iterator(); iter.hasNext();
                        row++) {
                        Entry entry = (Entry)iter.next();
                        tableModel.setValueAt(entry.getKey(), row, 0);
                        tableModel.setValueAt(new Integer(((int[])entry.getValue())[0]), row, 1);
                    }
                    JTable jtable = new JTable(tableModel);
                    ListSelectionModel sel = jtable.getSelectionModel();
                    sel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    jtable.getSelectionModel().addSelectionInterval(0, 0);
                    JScrollPane scroll = new JScrollPane(jtable);
                    scroll.setColumnHeaderView(jtable.getTableHeader());
                   
                    int ret = JOptionPane.showConfirmDialog(
                            null, 
                            scroll, 
                            "Selected value(s) for the rows",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                    if (ret ==  JOptionPane.CANCEL_OPTION 
                            || sel.isSelectionEmpty()) {
                        return;
                    }
                    IntArrayList values = new IntArrayList();
                    for (int i = sel.getMinSelectionIndex(); i <= sel.getMaxSelectionIndex(); i++) {
                        if (sel.isSelectedIndex(i)) {
                            int[] val = (int[])map.get(tableModel.getValueAt(i, 0));
                            values.add(val[1]);
                        }
                    }
                    values.sort();
                    final IntArrayList filter = new IntArrayList();
                    for (RowIterator iter = c.iterator(); iter.hasNext(); ) {
                        int r = iter.nextRow();
                        int value = c.getIntAt(r);
                        int binarySearch = values.binarySearch(value);
                        if (binarySearch >= 0) {
                            filter.add(r);
                        }
                    }
                    getMatrix().setRowPermutation(null);
                    getMatrix().setColumnPermutation(null);
                    getMatrix().filterRowColumn(new RowFilter() {
                        public boolean isFiltered(int row) {
                            return filter.binarySearch(row)>=0;
                        }
                    });
                }
            }
        
        });
        
        checkBoxes.add(categoryCombo);

        rowQueryPanel =
            new DynamicQueryPanel(
                getMatrix().getRowVisualization(),
                getGraph().getVertexTable(),
                getMatrix().getRowFilter(),
                getFilteredGraph().getFilter());
        JScrollPane rowQueryScroll =
            new JScrollPane(
                rowQueryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        rowQueryScroll.setBorder(
            BorderFactory.createTitledBorder("Row dynamic queries"));
        stack.add(rowQueryScroll);

        columnQueryPanel =
            new DynamicQueryPanel(
                getMatrix().getColumnVisualization(),
                getGraph().getVertexTable(),
                getMatrix().getColumnFilter(),
                getFilteredGraph().getFilter());
        JScrollPane columnQueryScroll =
            new JScrollPane(
                columnQueryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        columnQueryScroll.setBorder(
            BorderFactory.createTitledBorder("Column dynamic queries"));
        stack.add(columnQueryScroll);

        return stack;
    }
    
//    protected JComponent createVisualPanel() {
//        MatrixVisualPanel visualPanel =
//            new MatrixVisualPanel(
//                getMatrix(),
//                getFilteredGraph().getFilter());
//        return visualPanel;
//    }

}
