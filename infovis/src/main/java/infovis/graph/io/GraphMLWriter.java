/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.graph.io;

import infovis.Column;
import infovis.Graph;
import infovis.Table;
import infovis.io.AbstractXMLWriter;
import infovis.utils.RowIterator;

import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Writer for the GraphML format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.29 $
 * @infovis.factory GraphWriterFactory graphml GraphML
 */
public class GraphMLWriter extends AbstractXMLWriter {
    private Logger           logger = Logger.getLogger(GraphMLWriter.class);
    protected Graph          graph;
    /** DefaultTable of edge labels */
    protected ArrayList      edgeLabels;
    //protected AttributesImpl attrs  = new AttributesImpl();
    protected boolean        interlaced;
    protected Column         vertexIdColumn;
    protected Column         edgeIdColumn;

    /**
     * Constructor.
     * @param out the output stream
     * @param name the name
     * @param graph the graph
     */
    public GraphMLWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
    }

    /**
     * Returns the id of the specified vertex.
     * @param node the vertex
     * @return the vertex id
     */
    public String getVertexId(int node) {
        if (vertexIdColumn == null || vertexIdColumn.isValueUndefined(node)) {
            return "n" + node;
        }
        else {
            return vertexIdColumn.getValueAt(node);
        }
    }

    /**
     * Returns the id of the specified edge.
     * @param edge the edge
     * @return the id
     */
    public String getEdgeId(int edge) {
        if (edgeIdColumn == null || edgeIdColumn.isValueUndefined(edge)) {
            return "e" + edge;
        }
        else {
            return edgeIdColumn.getValueAt(edge);
        }
    }
    /**
     * Adds a column name to save as attribute.
     * @param name the name
     */
    public void addEdgeLabel(String name) {
        if (edgeLabels == null) {
            edgeLabels = new ArrayList();
        }

        edgeLabels.add(name);
    }

    /**
     * Adds all the edge attributes in the edge labels. 
     */
    public void addAllEdgeLabels() {
        int col;
        Table edges = graph.getEdgeTable();

        for (col = 0; col < edges.getColumnCount(); col++) {
            Column c = edges.getColumnAt(col);

            if (c.isInternal() || namedType(c) == null) {
                continue;
            }

            addEdgeLabel(c.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addAllColumnLabels() {
        int col;
        Table vertices = graph.getVertexTable();

        for (col = 0; col < vertices.getColumnCount(); col++) {
            Column c = vertices.getColumnAt(col);

            if (c.isInternal() || namedType(c) == null) {
                continue;
            }

            addColumnLabel(c.getName());
        }
    }

    /**
     * Returns the label of the edge column at the specified column index.
     * @param col the column index
     * @return the label
     */
    public String getEdgeLabelAt(int col) {
        return (String) edgeLabels.get(col);
    }

    /**
     * @return the list of edge labels to write
     */
    public ArrayList getEdgeLabels() {
        return edgeLabels;
    }

    /**
     * Writes the data associated with a specified edge.
     * 
     * @param edge
     *            the edge.
     * 
     * @exception SAXException
     *                passed from the underlying XMLWriter.
     */
    protected void writeEdge(int edge) throws Exception {
        Table edgeTable = graph.getEdgeTable();

        //indent();
        startTag("edge");
        attribute("source", getVertexId(graph.getFirstVertex(edge)));
        attribute("target", getVertexId(graph.getSecondVertex(edge)));
//        attrs.clear();
//        // attrs.addAttribute("", "id", "id", "ID", getEdgeId(edge)); not required
//        attrs.addAttribute("", "source", "source", "IDREF",
//                getVertexId(graph.getFirstVertex(edge)));
//        attrs.addAttribute("", "target", "taget", "IDREF",
//                getVertexId(graph.getSecondVertex(edge)));
//        startElement("", "edge", "", attrs);
//        depth++;
        for (int col = 0; edgeLabels != null && col < edgeLabels.size(); col++) {
            String label = getEdgeLabelAt(col);
            Column c = edgeTable.getColumn(label);
            if (c == edgeIdColumn)
                continue;

            if (!c.isValueUndefined(edge)) {
                //indent();
//                attrs.clear();
//                attrs.addAttribute("", "key", "key", "IDREF", c.getName());
//                startElement("data", attrs);
                startTag("data");
                attribute("key", c.getName());
                pcdata(c.getValueAt(edge));
                endTag();
            }
        }

//        depth--;
        //indent();
        endTag();
    }

    /**
     * Writes the data associated with a specified vertex.
     * 
     * @param vertex
     *            the vertex.
     * 
     * @exception Exception
     */
    protected void writeVertex(int vertex) throws Exception {
        ArrayList columnLabels = getColumnLabels();
        //indent();
//        attrs.clear();
//        attrs.addAttribute("", "id", "id", "ID", getVertexId(vertex));
//        startElement("", "node", "", attrs);
        startTag("node");
        attribute("id", getVertexId(vertex));
//        depth++;
        for (int col = 0; columnLabels != null && col < columnLabels.size(); col++) {
            String label = getColumnLabelAt(col);
            Column c = graph.getVertexTable().getColumn(label);
            if (c == vertexIdColumn)
                continue;

            if (!c.isValueUndefined(vertex)) {
                //indent();
//                attrs.clear();
//                attrs.addAttribute("", "key", "key", "IDREF", c.getName());
//                startElement("", "data", "", attrs);
                startTag("data");
                attribute("key", c.getName());
                pcdata(c.getValueAt(vertex));
                endTag();
            }
        }
//        depth--;
        //indent();
        endTag();
        if (interlaced) {
            for (RowIterator iter = graph.outEdgeIterator(vertex);
                iter .hasNext();) {
                int edge = iter.nextRow();
                writeEdge(edge);
            }
        }
    }

    /**
     * @see infovis.io.AbstractWriter#write()
     */
    public boolean write() {
        ArrayList columnLabels = getColumnLabels();
        ArrayList edgeLabels = getEdgeLabels();

        int col;

        if (columnLabels == null) {
            addAllColumnLabels();
            columnLabels = getColumnLabels();
        }

        if (edgeLabels == null) {
            addAllEdgeLabels();
            edgeLabels = getEdgeLabels();
        }
//        depth = 0;
        try {
            declaration();
            //dtd("graphml", "http://graphml.graphdrawing.org/dtds/1.0rc/graphml.dtd", "graphml.dtd");
//            depth++;
            //indent();
            startTag("graphml");
            attribute("xmlns","http://graphml.graphdrawing.org/xmlns");
            attribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            attribute("xsi:schemaLocation","http://graphml.graphdrawing.org/xmlns\n"+ 
                             "              http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
            startTag("graph");
            if (graph.isDirected()) {
                attribute("edgedefault", "directed");
            }
            else {
                attribute("edgedefault", "undirected");
            }
//                attrs.addAttribute(
//                        "",
//                        "edgedefault",
//                        "edgedefault",
//                        "CDATA",
//                        "directed");
//            }
//            else {
//                attrs.addAttribute(
//                        "",
//                        "edgedefault",
//                        "edgedefault",
//                        "CDATA",
//                        "undirected");
//            }
//            startElement("", "graph", "", attrs);
//            attrs.clear();
//            depth++;
//            attrs.addAttribute("", "id", "id", "ID", "0");
//            attrs.addAttribute("", "for", "for", "CDATA", "node");
//            attrs.addAttribute("", "attr.name", "attr.name", "CDATA", "0");
//            attrs.addAttribute("", "attr.type", "attr.type", "CDATA", "string");

            for (col = 0; columnLabels != null && col < columnLabels.size(); col++) {
                String label = getColumnLabelAt(col);
                Column c = graph.getVertexTable().getColumn(label);
                //indent();
//                attrs.setAttribute(0, "", "id", "id", "ID", c.getName());
//                attrs.setAttribute(2, "", "attr.name", "attr.name", "CDATA", c.getName());
//                attrs.setAttribute(3, "", "attr.type", "attr.type", "CDATA", namedType(c));
//                emptyElement("", "key", "", attrs);
                startTag("key");
                attribute("id", c.getName());
                attribute("for", "node");
                attribute("attr.name", c.getName());
                attribute("attr.type", namedType(c));
                endTag();
            }

//            attrs.setAttribute(1, "", "for", "for", "CDATA", "edge");
            Table edgeTable = graph.getEdgeTable();
            for (col = 0; edgeLabels != null && col < edgeLabels.size(); col++) {
                String label = getEdgeLabelAt(col);
                Column c = edgeTable.getColumn(label);

                //indent();
//                attrs.setAttribute(0, "", "id", "id", "ID", c.getName());
//                attrs.setAttribute(2, "", "attr.name", "attr.name", "CDATA", c.getName());
//                attrs.setAttribute(3, "", "attr.type", "attr.type", "CDATA", namedType(c));
//                emptyElement("", "key", "", attrs);
                startTag("key");
                attribute("id", c.getName());
                attribute("for", "edge");
                attribute("attr.name", c.getName());
                attribute("attr.type", namedType(c));
                endTag();
            }

            for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
                int vertex = iter.nextRow();
                writeVertex(vertex);
            }

            if (!interlaced) {
                for (RowIterator iter = graph.edgeIterator(); iter.hasNext();) {
                    int edge = iter.nextRow();
                    writeEdge(edge);
                }
            }

//            depth--;
            //indent();
            endTag();//"graph");
//            depth--;
            //indent();
            endTag(); //Element("graphml");
            endDocument();
        } catch (Exception e) {
            logger.error("Error writing GraphML file ", e);
            return false;
        }
        return true;
    }

    /**
     * Returns the interlaced.
     * 
     * @return boolean
     */
    public boolean isInterlaced() {
        return interlaced;
    }

    /**
     * Sets the interlaced.
     * 
     * @param interlaced
     *            The interlaced to set
     */
    public void setInterlaced(boolean interlaced) {
        this.interlaced = interlaced;
    }

    /**
     * @return the column containing the edge ids
     */
    public Column getEdgeIdColumn() {
        return edgeIdColumn;
    }

    /**
     * Sets the column containing the edge ids.
     * @param edgeIdColumn the column
     */
    public void setEdgeIdColumn(Column edgeIdColumn) {
        this.edgeIdColumn = edgeIdColumn;
    }

    /**
     * @return the column containing the vertex ids
     */
    public Column getVertexIdColumn() {
        return vertexIdColumn;
    }

    /**
     * Sets the column containing the vertex ids.
     * @param vertexIdColumn the column
     */
    public void setVertexIdColumn(Column vertexIdColumn) {
        this.vertexIdColumn = vertexIdColumn;
    }
}