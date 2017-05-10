/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.example;
import infovis.io.AbstractReader;
import infovis.panel.VisualizationPanel;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.TimeSeriesVisualization;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;

/**
 * Simple Example using the InfoVis Toolkit.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */

public class Example1 {
    /** The main program.
     * 
     * @param args argument list
     */
    public static void main(String args[]) {
        BasicConfigurator.configure(); // Configure log4j
        String fileName = "data/table/salivary.tqd";
        if (args.length > 0) {
            fileName = args[0];
        }
        DefaultTable t = new DefaultTable(); // Create a table
        AbstractReader reader = // Create a reader for the specified file
            TableReaderFactory.createTableReader(fileName, t);
        if (reader == null || !reader.load()) { // if it works, load the file
            System.err.println("cannot load " + fileName);
            System.exit(1);
        }
        TimeSeriesVisualization visualization = // Create a visualization
            new TimeSeriesVisualization(t); // for Time Series
        VisualizationPanel panel = // Create a Swing Component to hold it
            new VisualizationPanel(visualization);
        // Associate the color with the Name attribute 
        visualization.setVisualColumn(
                TimeSeriesVisualization.VISUAL_COLOR,
                t.getColumn("Name"));
        
        // Display the visualization Panel in a simple JFrame
        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.pack();
    }
}
