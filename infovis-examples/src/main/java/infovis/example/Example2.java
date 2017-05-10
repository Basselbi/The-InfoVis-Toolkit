/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example;
import infovis.io.AbstractReader;
import infovis.panel.*;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.TreemapVisualization;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.BasicConfigurator;

/**
 * Simple Example of Treemap visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class Example2 {
    /** The main program.
     * 
     * @param args argument list
     */
    public static void main(String[] args) {
        BasicConfigurator.configure(); // Configure log4j
        String fileName = "data/tree/election.tm3";
        if (args.length > 0) {
            fileName = args[0];
        }
        DefaultTree t = new DefaultTree();
        AbstractReader reader =
            TreeReaderFactory.createTreeReader(fileName, t);
        if (reader == null || !reader.load()) {
            System.err.println("cannot load " + fileName);
        }

        TreemapVisualization visualization =
            new TreemapVisualization(t, null);
            
        JSplitPane split = ControlPanelFactory.createScrollVisualization(
                visualization);
        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(split);
        frame.setVisible(true);
        frame.pack();
    }
}
