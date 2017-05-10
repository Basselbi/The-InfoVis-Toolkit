/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.ShapeColumn;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.text.MutableAttributeSet;

/**
 * Class GraphEdWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 * @infovis.factory GraphWriterFactory g GraphEd
 */
public class GraphEdWriter extends AbstractWriter {
    /** Prefix of GraphEd properties. */
    public static final String GE_PROPERTY_PREFIX = "ge::";
    /** Name of the ID column. */
    protected Graph            graph;
    protected ShapeColumn      shapes;

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
    public GraphEdWriter(OutputStream out, String name, Graph graph) {
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
    public GraphEdWriter(OutputStream out, Graph graph) {
        this(out, graph.getName(), graph);
    }

    /**
     * {@inheritDoc}
     */
    public boolean write() {
        try {
            String name = graph.getName();
            RowIterator allocIter = graph.newEdgeIterator();
            
            if (name == null) {
                name = "Infovis";
            }
            write ("GRAPH "+ quoteString(name) +" = ");
            if (graph.isDirected()) {
                write("DIRECTED");
            }
            else {
                write("UNDIRECTED");
            }
            writeln();
            MutableAttributeSet cp = graph.getClientProperty();
            if (cp != null && cp.getAttributeCount() != 0) {
                write("{$");
               for (Enumeration iter = cp.getAttributeNames(); 
                   iter.hasMoreElements(); ) {
                   String key = (String)iter.nextElement();
                   if (key.startsWith(GE_PROPERTY_PREFIX)) {
                       write(key.substring(GE_PROPERTY_PREFIX.length()));
                       write(" ");
                       Object o = cp.getAttribute(key);
                       if (o instanceof String) {
                        String s = (String) o;
                        write(quoteString(s));
                       }
                       else {
                           write(o.toString());
                       }
                       write(" ");
                   }
               }
               write("$}\n");
            }
            StringBuffer props = new StringBuffer(); 
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                write(Integer.toString(vertex+1));
                write(" ");

                if (shapes != null) {
                    Shape s = (Shape)shapes.get(vertex);
                    if (s != null) {
                        Rectangle2D b = s.getBounds2D();
                        double x = b.getX();
                        double y = b.getY();
//                        double w = b.getWidth();
//                        double h = b.getHeight();
                        props.append("NP "+x+" "+y+" ");
                        //NS "+w+" "+h+" ");
                    }
                }
                
                if (props.length() != 0) {
                    write("{$");
                    write(props.toString());
                    write("$}");
                    props.setLength(0);
                }
                write(" \"\"");
            
                for (RowIterator eiter = 
                    graph.isDirected() 
                        ? graph.outEdgeIterator(vertex, allocIter)
                        : graph.edgeIterator(vertex, allocIter);
                    eiter.hasNext(); ) {
                    int edge = eiter.nextRow();
                    int v2 = graph.getOtherVertex(edge, vertex);
                    if (! graph.isDirected() &&
                            v2 < vertex) {
                        continue;
                    }
                    writeln();
                    write(Integer.toString(v2+1));
                    
                    if (props.length() != 0) {
                        write("{$");
                        write(props.toString());
                        write("$}\n");
                        props.setLength(0);
                    }
                    
                    write(" \"\"");
                }
                write(";\n");
            }
            write("END\n");
            flush();
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @return the shape column
     */
    public ShapeColumn getShapes() {
        return shapes;
    }

    /**
     * Sets the shape column.
     * 
     * @param column
     *            the column
     */
    public void setShapes(ShapeColumn column) {
        shapes = column;
    }
}
