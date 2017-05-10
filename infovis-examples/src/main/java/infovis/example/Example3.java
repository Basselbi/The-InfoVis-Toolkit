/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example;

import javax.swing.*;

import org.apache.log4j.BasicConfigurator;

import infovis.column.ColumnFilter;
import infovis.column.filter.InternalFilter;
import infovis.io.AbstractReader;
import infovis.panel.DetailTable;
import infovis.panel.DynamicQueryPanel;
import infovis.panel.VisualizationPanel;
import infovis.table.DefaultTable;
import infovis.table.FilteredTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotVisualPanel;
import infovis.table.visualization.ScatterPlotVisualization;
import infovis.visualization.inter.InteractorFactory;

/**
 * Simple example of the InfoVis Toolkit.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class Example3 {
    /** The main program.
     * 
     * @param args argument list
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        String fileName = "data/table/salivary.tqd";
        if (args.length != 0) {
            fileName = args[0];
        }
        DefaultTable table = new DefaultTable();
        AbstractReader reader =
            TableReaderFactory.createTableReader(fileName, table);
        if (reader == null || !reader.load()) {
            System.err.println("cannot load " + fileName);
            return;
        }

        // Create a visualization as Scatter Plot
        ScatterPlotVisualization plot = new ScatterPlotVisualization(table);
        
        // Install the default interactors on the visualization
        InteractorFactory.installInteractor(plot);

        // The controls will be added to this JTabbedPane
        JTabbedPane tabs = new JTabbedPane();

        // Create a filter to hide internal columns from the different panels
        ColumnFilter filter = new InternalFilter();
        FilteredTable filteredTable = new FilteredTable(table, filter);

        // Create a Dynamic Query Panel to interactively filter on the attribute values
        DynamicQueryPanel dq = new DynamicQueryPanel(plot, filteredTable); 
        
        // Create a control panel for changing the visual settings of the plot
        ScatterPlotVisualPanel panel = new ScatterPlotVisualPanel(plot, filter, dq);
        
        // Create a pane showing the detailed values of the selected items
        JComponent details = DetailTable.createDetailJTable(
                filteredTable,
                plot.getSelection());
        
        // Add the panels to the tab
        tabs.add("Details", details);
        tabs.add("Filters", dq);
        tabs.add("Visual", panel);

        JFrame frame = new JFrame("Scatterplot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new VisualizationPanel(plot),
                tabs);
        split.setResizeWeight(1);
        frame.getContentPane().add(split);
        frame.setVisible(true); 
        //frame.pack(); 
    }
}
