/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.example.io;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import infovis.example.ExampleRunner;
import infovis.table.DefaultTable;
import infovis.table.io.CSVTableReader;
import infovis.table.io.CSVTableWriter;
import infovis.table.io.SODASTableReader;

/**
 * Class SODASTableReaderExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class SODASTableReaderExample {
    /**
     * Main program.
     * @param args argument list
     * @throws Exception when file does not exist
     */
    public static void main(String args[]) throws Exception {
        if (args.length < 1 || args.length > 4) {
            System.err.println("Syntax: <file>");
            System.exit(1);
        }
        File file = new File(args[args.length - 1]);
        DefaultTable table = new DefaultTable();
        SODASTableReader reader;
        reader = new SODASTableReader(new FileInputStream(file), table);

        if (reader.load()) {
            System.out.println("DefaultTable loaded successfully");
        }
        else {
            System.out.println("DefaultTable not loaded successfully");
        }
        JFrame frame = new JFrame("SODAS File "+file.toString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTable jtable = new JTable(table);
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane pane = new JScrollPane(
                jtable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.setVisible(true);
    }
}
