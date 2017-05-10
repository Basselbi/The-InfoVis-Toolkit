/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Column;
import infovis.Graph;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class GMLGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * @infovis.factory GraphWriterFactory gml GML
 */
public class GMLGraphWriter extends AbstractWriter {
    protected Graph   graph;
    protected HashSet vertexAttributes = new HashSet();
    protected HashSet edgeAttributes   = new HashSet();
    protected String  label; 

    /**
     * Creates a GraphEdWriter.
     * 
     * @param out
     *            the output stream
     * @param name
     *            the name
     * @param graph
     *            the graph
     */
    public GMLGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
    }

    /**
     * Creates a GraphEdWriter.
     * 
     * @param out
     *            the output stream
     * @param graph
     *            the graph
     */
    public GMLGraphWriter(OutputStream out, Graph graph) {
        this(out, graph.getName(), graph);
    }

    /**
     * Tell the writer that the specified vertex attribute should be written.
     * 
     * @param att
     *            the attribute name
     */
    public void addVertexAttribute(String att) {
        vertexAttributes.add(att);
    }

    /**
     * Tell the writer that the specified edge attribute should be written.
     * 
     * @param att
     *            the attribute name
     */
    public void addEdgeAttribute(String att) {
        edgeAttributes.add(att);
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    // TODO accessors

    /**
     * {@inheritDoc}
     */
    public boolean write() {
        try {
            RowIterator allocIter = graph.newEdgeIterator();

            write("graph [");
            writeln();
            String name = graph.getName();
            if (name != null) {
                write("\tlabel " + quoteString(name));
                writeln();
            }
            if (graph.isDirected()) {
                write("\tdirected 1");
            }
            else {
                write("\tdirected 0");
            }
            writeln();
            StringBuffer props = new StringBuffer();
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
                int vertex = iter.nextRow();
                write("\tnode [");
                writeln();
                write("\t\tid "+Integer.toString(vertex + 1));
                writeln();
                if (label != null) {
                    Column c = graph.getVertexTable().getColumn(label);
                    if (c != null && !c.isValueUndefined(vertex)) {
                        write("\t\tlabel ");
                        write(quoteString(c.getValueAt(vertex)));
                        writeln();
                    }
                }
                for (Iterator aIter= vertexAttributes.iterator(); aIter.hasNext(); ) {
                    String att = (String)aIter.next();
                    Column c = graph.getVertexTable().getColumn(att);
                    if (c != null && !c.isValueUndefined(vertex)) {
                        write("\t\t");
                        write(quoteString(att));
                        write(" ");
                        write(quoteString(c.getValueAt(vertex)));
                        writeln();
                    }
                }
                write("\t]");
                writeln();
            }
            for (RowIterator eiter = graph.edgeIterator(); eiter.hasNext();) {
                    int edge = eiter.nextRow();
                    int vertex = graph.getFirstVertex(edge);
                    int v2 = graph.getSecondVertex(edge);
                    write("\tedge [");
                    writeln();
                    write("\t\tsource "+Integer.toString(vertex+1));
                    writeln();
                    write("\t\ttarget "+Integer.toString(v2 + 1));
                    writeln();
                    write("\t]");
                    writeln();
            }
            write("]");
            writeln();
            flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
