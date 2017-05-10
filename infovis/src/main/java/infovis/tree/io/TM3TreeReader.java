/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Column;
import infovis.Tree;
import infovis.column.StringColumn;
import infovis.table.io.CSVTableReader;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Reader for the UMD Treemap TM3 Format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 * 
 * @infovis.factory TreeReaderFactory tm3
 */
public class TM3TreeReader extends CSVTableReader {
    protected Tree         tree;
    protected StringColumn nameColumn;

    /**
     * Constructor for TM3TreeReader.
     * 
     * @param in
     * @param tree
     */
    public TM3TreeReader(InputStream in, Tree tree) {
        this(in, null, tree);
    }

    /**
     * Constructor for TM3TreeReader.
     * 
     * @param in
     * @param name
     * @param tree
     */
    public TM3TreeReader(InputStream in, String name, Tree tree) {
        super(in, name, tree);
        this.tree = tree;
        nameColumn = StringColumn.findColumn(tree, "name");
        setSeparator('\t');
        setLabelLinePresent(true);
        setTypeLinePresent(true);
    }

    /**
     * @see infovis.table.io.CSVTableReader#readLines()
     */
    protected void readLines() throws IOException, ParseException {
        try {
            disableNotify();
            String[] line;
            while ((line = reader.readNext()) != null) {
                int node = tree.addNode(Tree.ROOT);
                int column;
                
                for (column = 0; column < labels.size(); column++) {
                    Column col = tree.getColumn(getLabelAt(column));
                    if (col == null) {
                        throw new ParseException(
                                "cannot get column " + column,
                                0);
                    }
                    String field = line[column];

                    col.setValueOrNullAt(node, field);
                }

                int parent = Tree.ROOT;
                for (; column < line.length-1; column++) {
                    String field = line[column];
                    if (field != null && ! field.isEmpty()) {
                        parent = AbstractTreeReader.findNode(
                                field,
                                parent,
                                tree,
                                nameColumn);
                    }
                }
                // The last one is where the node should go.
                nameColumn.setExtend(node, line[line.length-1]);
                if (node != Tree.ROOT)
                    tree.reparent(node, parent);
            }
        } finally {
            enableNotify();
        }
    }

}
