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
import infovis.column.AbstractBooleanColumn;
import infovis.column.NumberColumn;
import infovis.column.ShapeColumn;
import infovis.column.StringColumn;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;
import infovis.visualization.Orientable;
import infovis.visualization.render.VisualColor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.text.MutableAttributeSet;


/**
 * <b>DOTGraphWriter</b> is a Graph Writer for the DOT format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 * @infovis.factory GraphWriterFactory dot GraphViz
 */
public class DOTGraphWriter extends AbstractWriter implements Orientable {
    /** Name of the metadata prefix for graph attributes to pass to dot. */
    public static final String DOT_PROPERTY_PREFIX = "dot::";
    /** Name of the metadata prefix for node attributes to pass to dot. */
    public static final String DOT_NODE_PROPERTY_PREFIX = "dot_node::";
    /** Name of the metadata prefix for edge attributes to pass to dot. */
    public static final String DOT_EDGE_PROPERTY_PREFIX = "dot_edge::";
    /** Name of the ID column for DOT vertices. */
    public static final String ID_COLUMN = "id";
    protected Graph graph;
    protected Column vertexIdColumn;
    protected Column edgeIdColumn;
    protected ShapeColumn shapes;
    protected Column labels;
    protected NumberColumn linkLengthColumn;
    protected AbstractBooleanColumn fixedColumn; 
    protected VisualColor colors;
    protected Dimension size;
    protected short orientation = ORIENTATION_SOUTH;
    protected String layoutRatio;
    protected Font font;
    protected VisualColor linkColors;
    protected double linkMin = 1;
    protected double linkMax = 10;
    private transient double linkScale;
    private transient double linkOffset;
    protected Map nodeProperties;
    protected Map edgeProperties;
    
    /**
     * Creates a DOTGraphWriter on a specified stream.
     * @param out the stream
     * @param name the name
     * @param graph the graph
     */
    public DOTGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
        vertexIdColumn = StringColumn.findColumn(graph.getVertexTable(), ID_COLUMN);
        edgeIdColumn = StringColumn.findColumn(graph.getEdgeTable(), ID_COLUMN);
//        fixedColumn = BooleanColumn.findColumn(graph.getVertexTable(), "fixed");
    }
    
    /**
     * Creates a DOTGraphWriter on a specified stream.
     * @param out the stream
     * @param graph the graph
     */
    public DOTGraphWriter(OutputStream out, Graph graph) {
        this(out, graph.getName(), graph);
    }
    
    protected void computeLinkScale() {
        if (linkLengthColumn == null
                || linkLengthColumn.getMinIndex()==linkLengthColumn.getMaxIndex()) {
            linkScale = 0;
        }
        else {
            double min = linkLengthColumn.getDoubleMin();
            double max = linkLengthColumn.getDoubleMax();
            if (min == max) {
                linkScale = 0;
            }
            else {
                linkScale = (linkMax - linkMin) / (max - min);
                linkOffset = min;
            }
        }
    }
    
    protected double getLinkLength(int edge) {
        if (linkLengthColumn == null || linkLengthColumn.isValueUndefined(edge)) {
            return 1;
        }
        double v = linkLengthColumn.getDoubleAt(edge);
        return (v - linkOffset) * linkScale + linkMin;
    }
    
    protected String getVertexId(int vertex) {
        if (vertexIdColumn.isValueUndefined(vertex)) {
            String id = Integer.toString(vertex);
            vertexIdColumn.setValueOrNullAt(vertex, id);
            return id;
        }
        return vertexIdColumn.getValueAt(vertex);
    }
    
    protected String getEdgeId(int edge) {
        if (edgeIdColumn.isValueUndefined(edge)) {
            String id = Integer.toString(edge);
            edgeIdColumn.setValueOrNullAt(edge, id);
            return id;
        }
        return edgeIdColumn.getValueAt(edge);
    }
    
    protected void writeVertex(int vertex) throws IOException {
        write("\""+getVertexId(vertex)+"\"");
    }
    
    protected void writeEdge(int edge) throws IOException {
        int vertex = graph.getFirstVertex(edge);
        int otherVertex = graph.getSecondVertex(edge);

        writeVertex(vertex);
        if (graph.isDirected()) {
            write("->");
        }
        else {
            write("--");
        }
        writeVertex(otherVertex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean write() {
        computeLinkScale();
        try {
            String name = getName();
            if (name == null) {
                name = "Infovis";
            }
            if (graph.isDirected()) {
                write ("digraph "+name +" {\n");
            }
            else {
                write("graph "+name+" {\n");
            }
            MutableAttributeSet cp = graph.getMetadata();
            nodeProperties = new TreeMap();
            edgeProperties = new TreeMap();
            if (cp != null) {
               for (Enumeration iter = cp.getAttributeNames(); iter.hasMoreElements(); ) {
                   String key = (String)iter.nextElement();
                   if (key.startsWith(DOT_PROPERTY_PREFIX)) {
                       write(key.substring(DOT_PROPERTY_PREFIX.length()));
                       write("=");
                       write(cp.getAttribute(key).toString());
                       write(";\n");
                   }
                   else if (key.startsWith(DOT_NODE_PROPERTY_PREFIX)) {
                       Column c = graph.getVertexTable().getColumn(cp.getAttribute(key).toString());
                       if (c != null) {
                           nodeProperties.put(key.substring(DOT_NODE_PROPERTY_PREFIX.length()), c);
                       }
                   }
                   else if (key.startsWith(DOT_EDGE_PROPERTY_PREFIX)) {
                       Column c = graph.getEdgeTable().getColumn(cp.getAttribute(key).toString());
                       if (c != null) {
                           edgeProperties.put(key.substring(DOT_EDGE_PROPERTY_PREFIX.length()), c);
                       }
                   }
               }
            }
            write(" ");
            write(" node [label=\"\", shape=box,");
            if (font != null) {
                write("fontsize="+font.getSize()+",");
                write("fontname="+font.getName()+",");
            }
            write("];\n");
            if (size != null) {
                write(" ");
                write("size=\""+size.width/72.0+","+size.height/72.0+"\";\n");
            }
            if (orientation == ORIENTATION_EAST || orientation == ORIENTATION_WEST) {
                write(" ");
                write("rankdir=LR;\n");
            }
            if (layoutRatio != null) {
                write(" ");
                write("ratio=\""+layoutRatio+"\";\n");
            }
            if (shapes != null) {
                write(" ");
//                write("node [label=\"\", fixedsize=true]\n");
                write("node [fixedsize=true]\n");
            }
            StringBuffer props = new StringBuffer(); 
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                write(" ");
                writeVertex(vertex);
                if (labels != null) {
                    String label = labels.getValueAt(vertex);
                    if (label != null) {
                        props.append("label="+quoteString(label)+",");
                    }
                }
                for (Iterator pIter = nodeProperties.entrySet().iterator(); pIter.hasNext(); ) {
                    Map.Entry e = (Map.Entry)pIter.next();
                    Column c = (Column)e.getValue();
                    if (! c.isValueUndefined(vertex)) {
                        props.append(e.getKey().toString()+"="+quoteString(c.getValueAt(vertex))+",");
                    }
                }

                if (shapes != null) {
                    Shape s = (Shape)shapes.get(vertex);
                    if (s != null) {
                        double w = s.getBounds2D().getWidth() / 72;
                        double h = s.getBounds2D().getHeight() / 72;
                        props.append("width=\""+w+"\",height=\""+h+"\",");
                        if (fixedColumn != null 
                                && !fixedColumn.isValueUndefined(vertex) 
                                && fixedColumn.get(vertex)) {
                            double x = s.getBounds2D().getCenterX() / 72;
                            double y = s.getBounds2D().getCenterY() / 72;
                            props.append("pos=\""+x+","+y+"!\",");
                        }
                    }
                }
                
                if (colors != null) {
                    Color c = colors.getColorAt(vertex);
                    if (c != null) {
                        float[] hsb = { 0, 0, 0};
                        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                        props.append("style=filled, fillcolor=\""+hsb[0]+" "+hsb[1]+" "+hsb[2]+"\",");
                    }
                }
                
                if (props.length() != 0) {
                    write("["+props.toString()+"]");
                    props.setLength(0);
                }
                write(";\n");
            }
            
            for (RowIterator iter = graph.edgeIterator(); iter.hasNext(); ) {
                int edge = iter.nextRow();
                write(" ");
                writeEdge(edge);
                if (linkColors != null) {
                    Color c = linkColors.getColorAt(edge);
                    if (c != null) {
                        float[] hsb = { 0, 0, 0};
                        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                        props.append("color=\""+hsb[0]+" "+hsb[1]+" "+hsb[2]+"\"");
                    }
                }
                for (Iterator pIter = edgeProperties.entrySet().iterator(); pIter.hasNext(); ) {
                    Map.Entry e = (Map.Entry)pIter.next();
                    Column c = (Column)e.getValue();
                    if (! c.isValueUndefined(edge)) {
                        props.append(e.getKey().toString()+"="+quoteString(c.getValueAt(edge))+",");
                    }
                }

                if (linkLengthColumn != null && !linkLengthColumn.isValueUndefined(edge)) {
                    double l = getLinkLength(edge);
                    props.append("len=\""+l+"\",");
                }
                if (props.length() != 0) {
                    write("["+props.toString()+"]");
                    props.setLength(0);
                }
                write(";\n");
            }
            write("}\n");
            flush();
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @return the shapes
     */
    public ShapeColumn getShapes() {
        return shapes;
    }

    /**
     * Sets the shapes.
     * @param column the shapes
     */
    public void setShapes(ShapeColumn column) {
        shapes = column;
    }

    /** @return Return the size */
    public Dimension getSize() {
        return size;
    }

    /**
     * Sets the dimension.
     * @param dimension the dimension
     */
    public void setSize(Dimension dimension) {
        size = dimension;
    }
    
    /**
     * @return the fixed
     */
    public AbstractBooleanColumn getFixedColumn() {
        return fixedColumn;
    }
    
    /**
     * @param fixed the fixed to set
     */
    public void setFixedColumn(AbstractBooleanColumn fixed) {
        this.fixedColumn = fixed;
    }
    
    /**
     * @return Returns the label column
     */
    public Column getLabels() {
        return labels;
    }
    
    /**
     * Sets the label column.
     * @param labels the column
     */
    public void setLabels(Column labels) {
        this.labels = labels;
    }

    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return orientation;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(short s) {
        orientation = s;
    }

    /**
     * @return Returns the layout ratio
     */
    public String getLayoutRatio() {
        return layoutRatio;
    }

    /**
     * Sets the layout ratio.
     * @param string the ratio
     */
    public void setLayoutRatio(String string) {
        layoutRatio = string;
    }

    /**
     * @return the column of edge ids
     */
    public Column getEdgeIdColumn() {
        return edgeIdColumn;
    }
    
    /**
     * Sets the column of edge ids.
     * @param edgeIdColumn the column
     */
    public void setEdgeIdColumn(StringColumn edgeIdColumn) {
        this.edgeIdColumn = edgeIdColumn;
    }
    
    /**
     * @return the column of vertex ids.
     */
    public Column getVertexIdColumn() {
        return vertexIdColumn;
    }
    
    /**
     * Sets the column of vertex ids.
     * @param vertexIdColumn the column
     */
    public void setVertexIdColumn(StringColumn vertexIdColumn) {
        this.vertexIdColumn = vertexIdColumn;
    }

    /**
     * @return the column of colors
     */
    public VisualColor getColors() {
        return colors;
    }

    /**
     * Sets the column of colors.
     * @param colors the column
     */
    public void setColors(VisualColor colors) {
        this.colors = colors;
    }

    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * @return the linkColors
     */
    public VisualColor getLinkColors() {
        return linkColors;
    }

    /**
     * @param linkColors the linkColors to set
     */
    public void setLinkColors(VisualColor linkColors) {
        this.linkColors = linkColors;
    }
    
    /**
     * @return the linkLengthColumn
     */
    public NumberColumn getLinkLengthColumn() {
        return linkLengthColumn;
    }
    
    /**
     * @param linkLengthColumn the linkLengthColumn to set
     */
    public void setLengthColumn(NumberColumn linkLengthColumn) {
        this.linkLengthColumn = linkLengthColumn;
    }
}
