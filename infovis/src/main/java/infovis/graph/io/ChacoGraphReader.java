/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.AbstractDoubleColumn;
import infovis.column.AbstractIntColumn;
import infovis.column.DoubleColumn;
import infovis.column.IntColumn;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.BufferedReader;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Reader for the Chaco format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * @infovis.factory GraphReaderFactory graph
 */
public class ChacoGraphReader extends AbstractGraphReader {
    private static final Logger LOG = Logger.getLogger(ChacoGraphReader.class);
    
    /**
     * Creates a ChacoGraphReader.
     * @param in the input stream
     * @param name the name
     * @param graph the graph
     */
    public ChacoGraphReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
    }

    /**
     * {@inheritDoc}
     */
    public Float getBbox() {
        return null;
    }
    
    protected static boolean isComment(String line) {
        return (line.charAt(0)=='#' || line.charAt(0)=='%');
    }

    /**
     * {@inheritDoc}
     */
    public boolean load() throws WrongFormatException {
        graph.setDirected(false);
        BufferedReader in = getBufferedReader();
        
        try {
            String[] fields;
            String line;
            
            while (true) {
                line = in.readLine();
                if (line == null) return false;
                if (! isComment(line)) 
                    break;
            }
            fields = line.trim().split(" ");
            if (fields.length < 2 || fields.length > 3) {
                throw new WrongFormatException(
                        "Expected 2 or 3 values at first line");
            }
            int vertices = Integer.parseInt(fields[0]);
            int edges = Integer.parseInt(fields[1])*2;
            if (vertices <= 0 || edges <= 0) {
                throw new WrongFormatException(
                        "Invalid vertices ("
                        +vertices+") or edge ("
                        +edges+")numbers in Chaco file format");
            }
            boolean hasEdgeWeight = false;
            boolean hasVertexWeight = false;
            boolean hasVertexNumber = false;
            if (fields.length == 3) {
                int options = Integer.parseInt(fields[2]);
                if ((options % 10) != 0) {
                    hasEdgeWeight = true;
                }
                options /= 10;
                if ((options % 10) != 0) {
                    hasVertexWeight = true;
                }
                options /= 10;
                if ((options % 10) != 0) {
                    hasVertexNumber = false;
                }
            }
            //TODO: read attributes
            int[] vertexMap = new int[vertices];
            AbstractIntColumn idColumn = null;
            if (hasVertexNumber) {
                idColumn = IntColumn.findColumn(
                        graph.getVertexTable(), 
                        "id");
            }
            AbstractDoubleColumn vertexWeight = null;
            if (hasVertexWeight) {
                vertexWeight = DoubleColumn.findColumn(
                        graph.getVertexTable(), 
                        "weight");
            }
            AbstractDoubleColumn edgeWeight = null;
            if (hasEdgeWeight) {
                edgeWeight = DoubleColumn.findColumn(
                        graph.getEdgeTable(), 
                        "weight");
            }
            int v;
            int edgesRead = 0;
            for (v = 0; v < vertices; v++) {
                vertexMap[v] = graph.addVertex();
            }
            for (v = 0; v < vertices; ) {
                int vertex = vertexMap[v];
                line = in.readLine();
                if (line == null) {
                    throw new WrongFormatException(
                    "Unexpected EOL while reading vertex #"+v);
                }
                if (isComment(line)) continue;
                fields = line.trim().split(" ");
                for (int i = 0; i < fields.length; i++) {
                    int edge = -1;
                    if (i == 0 && hasVertexNumber) {
                        idColumn.setValueAt(vertex, fields[i]);
                    }
                    else if (i == 0 && hasVertexWeight
                            || i == 1 && hasVertexNumber && hasVertexWeight) {
                        vertexWeight.setValueAt(vertex, fields[i]);
                    }
                    else if (!hasEdgeWeight || edge==-1) {
                        int v2 = Integer.parseInt(fields[i])-1;
                        edge = graph.findEdge(vertex, vertexMap[v2]);
                        edgesRead ++;
                    }
                    else if (edge != -1) {
                        edgeWeight.setValueAt(edge, fields[i]);
                        edge = -1;
                    }
                    else {
                        LOG.error("Logical error");
                    }
                }
                
                v++;
            }
            if (edgesRead != edges) {
                LOG.warn("Read "+edgesRead+" edges instead of "+edges);
            }
        }
        catch(Exception e) {
            LOG.error("Problem reading graph: "+getName(), e);
            return false;
        }
        return true;
    }

}
