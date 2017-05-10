package infovis.example.io;

import infovis.Tree;
import infovis.tree.DefaultTree;
import infovis.tree.io.DirectoryTreeReader;
import infovis.tree.io.XMLTreeWriter;
import infovis.tree.property.LeafCountColumn;
import org.apache.log4j.BasicConfigurator;

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class XMLTreeWriterExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class XMLTreeWriterExample {

    public static void main(String[] args) {
        BasicConfigurator.configure(); // Configure log4j
        final Tree tree = new DefaultTree();
        DirectoryTreeReader reader = new DirectoryTreeReader(".", tree);
        // FileReader in;
        // try {
        // in = new FileReader(args[0]);
        // }
        // catch(FileNotFoundException e) {
        // return;
        // }
        // SimpleXMLTreeReader reader = new SimpleXMLTreeReader(new
        // BufferedReader(in), args[0], tree);

        if (!reader.load()) {
            System.out.println("Problem reading directory");
        }

        LeafCountColumn leafCount = LeafCountColumn.findColumn(tree);
        leafCount.setName("leafCount"); // make it visible.

        // CSVTableWriter writer = new CSVTableWriter(
        // new OutputStreamWriter(System.out),
        // tree);
        XMLTreeWriter writer = new XMLTreeWriter(System.out, tree);
        writer.write();

    }
}
