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
import infovis.io.AbstractXMLWriter;
import infovis.tree.DepthFirst;

import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Writer according to the treeml DTD.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public class XMLTreeWriter extends AbstractXMLWriter {
    protected Tree tree;
    
    private static final Logger LOG = Logger.getLogger(AbstractXMLWriter.class);

    /**
     * Constructor for XMLTreeWriter.
     * 
     * @param out
     * @param tree
     */
    public XMLTreeWriter(OutputStream out, Tree tree) {
        super(out, tree);
        this.tree = tree;
    }

    /**
     * @see infovis.io.AbstractWriter#write()
     */
    public boolean write() {
        int col;

        ArrayList labels = getColumnLabels();
        if (labels == null) {
            addAllColumnLabels();
            labels = getColumnLabels();
        }

        if (labels.size() == 0)
            return false;
//        depth = 0;

        try {
            declaration();
            dtd("tree", null, "treeml.dtd");
            startTag("tree");
//            depth++;
//            indent();
            startTag("declarations");
//            depth++;
            for (col = 0; col < labels.size(); col++) {
                String label = getColumnLabelAt(col);
                Column c = tree.getColumn(label);
//                indent();
                startTag("attributeDecl");
                attribute("name", label);
                attribute("type", namedType(c));
                endTag();
            }
//            depth--;
//            indent();
            endTag();

            DepthFirst.visit(tree, new DepthFirst.Visitor() {
                public boolean preorder(int node) {
                    // AttributesImpl atts = createAttributes(node);
//                    indent();
                    try {
                        if (tree.isLeaf(node)) {
                            startTag("leaf");
                            writeAttributes(node);
                            endTag();
                            return false;
                        }
                        else {
//                            depth++;
                            startTag("branch");
                            writeAttributes(node);
                            return true;
                        }
                    } catch (Exception e) {
                        LOG.error("While writing tree", e);
                        return false;
                    }
                }

                public void postorder(int node) {
//                    depth--;
//                    indent();
                    if (!tree.isLeaf(node)) {
                        try {
                            endTag();
                        } catch (Exception e) {
                            LOG.error("While writing tree", e);
                        }
                    }
                }

            });
//            depth--;
//            indent();
            endTag();
            endDocument();
        } catch (Exception e) {
            LOG.error("While writing a tree", e);
            return false;
        }
        return true;
    }

    /**
     * Writes the attributes associated with a row to a specified XMLWriter.
     * 
     * @param row
     *            the row.
     * 
     * @exception Exception
     */
    protected void writeAttributes(int row) throws Exception {
        ArrayList labels = getColumnLabels();
//        AttributesImpl atts = new AttributesImpl();
//        atts.addAttribute("", "name", "name", "CDATA", "");
//        atts.addAttribute("", "value", "value", "CDATA", "");

//        depth++;
        for (int col = 0; col < labels.size(); col++) {
            String label = getColumnLabelAt(col);
            Column c = table.getColumn(label);
            if (!c.isValueUndefined(row)) {
//                indent();
                startTag("attribute");
                attribute("name", label);
                attribute("value", c.getValueAt(row));
                endTag();
            }
        }
//        depth--;
    }

}
