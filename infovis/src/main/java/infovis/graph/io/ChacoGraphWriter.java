/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.AbstractDoubleColumn;
import infovis.io.AbstractWriter;
import infovis.io.WrongFormatException;
import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cern.colt.map.OpenIntIntHashMap;

/**
 * Class ChacoGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 * @infovis.factory GraphWriterFactory cha Chaco
 */
public class ChacoGraphWriter extends AbstractWriter {
    protected Graph graph;
    protected OpenIntIntHashMap map;
    protected int edgesCount;
    protected boolean writeLabels = false;

    
    /**
     * Creates a ChacoGraphWriter on a specified stream.
     * @param name the name
     * @param graph the graph
     */
    public ChacoGraphWriter(String name, Graph graph) throws IOException {
        this(open(name), name, graph);
    }

    /**
     * Creates a ChacoGraphWriter on a specified stream.
     * @param out the stream
     * @param name the name
     * @param graph the graph
     */
    public ChacoGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
    }
    
    /**
     * Creates a ChacoGraphWriter on a specified stream.
     * @param out the stream
     * @param graph the graph
     */
    public ChacoGraphWriter(OutputStream out, Graph graph) {
        this(out, graph.getName(), graph);
    }
    
    protected int countEdges(int vertex) {
        int count = 0;
        for (RowIterator iter = graph.edgeIterator(vertex); iter.hasNext(); ) {
            int e = iter.nextRow();
            int v2 = graph.getOtherVertex(e, vertex);
            if (v2 != vertex)
                count++;
        }
        return count;
    }
    
    protected OpenIntIntHashMap createVertexMap() {
        if (map == null) {
            edgesCount = 0;
            map = new OpenIntIntHashMap(graph.getVerticesCount());
            int num = 1;
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                int count = countEdges(vertex);
                if (count != 0) {
                    map.put(vertex, num);
                    num++;
                    edgesCount += count;
                }
            }
        }
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean write() {
        try {
            OpenIntIntHashMap map = createVertexMap();
            if (map.isEmpty()) return true;
            RowIterator allocIter = graph.newEdgeIterator();
            
            write(Integer.toString(map.size()));
            write(" ");
            write(Integer.toString(edgesCount/2));
            if (writeLabels) {
                write(" 100");// meaning each line has a vertex number
            }
            writeln();
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                if (map.containsKey(vertex)) {
                    int num = map.get(vertex);
                    if ((num % 10000) == 0) {
                        System.out.println("Sending vertex #"+num);
                    }
                    if (writeLabels) {
                        write(Integer.toString(num));
                        write(" ");
                    }
                    for (RowIterator eIter = graph.edgeIterator(vertex, allocIter); 
                        eIter.hasNext(); ) {
                        int e = eIter.nextRow();
                        int v2 = graph.getOtherVertex(e, vertex);
                        if (v2 != vertex) {
                            assert(map.containsKey(v2));
                            write(Integer.toString(map.get(v2)));
                            write(" ");
                        }
                    }
                    writeln();
                }
            }
            flush();
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Creates a reader for the output created by this writer.
     * @param in the input stream
     * @param name the name
     * @param graph the graph
     * @return
     */
    public CoordsReader createReader(InputStream in) {
        return new CoordsReader(in, name, graph);
    }
    
    /**
     * <b>CoordsReader</b> reads the coordinates output by ACE.
     * 
     * @author Jean-Daniel Fekete
     */
    public class CoordsReader extends AbstractGraphReader {
        protected AbstractDoubleColumn xColumn;
        protected AbstractDoubleColumn yColumn;
        
        /**
         * Creates a ChacoGraphReader.
         * @param in the input stream
         * @param name the name
         * @param graph the graph
         */
        public CoordsReader(InputStream in, String name, Graph graph) {
            super(in, name, graph);
        }
        
        /**
         * {@inheritDoc}
         */
        public Float getBbox() {
            return null;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean load() throws WrongFormatException {
            BufferedReader in = getBufferedReader();
            OpenIntIntHashMap map = createVertexMap();
            try {
                for (RowIterator iter = getGraph().vertexIterator(); iter.hasNext(); ) {
                    int vertex = iter.nextRow();
                    if (map.containsKey(vertex)) {
                        String line = in.readLine();
                        int index = line.indexOf(' ');
                        double x = Double.parseDouble(line.substring(0, index));
                        do {
                            index++;
                        }
                        while (line.charAt(index)==' ');
                        double y = Double.parseDouble(line.substring(index));
                        if (xColumn != null) {
                            xColumn.setExtend(vertex, x);
                        }
                        if (yColumn != null) {
                            yColumn.setExtend(vertex, y);
                        }
                        if (nodeShapes != null) {
                            Rectangle2D.Float rect = nodeShapes.getRect(vertex);
                            rect.x = (float)(x - rect.width/2);
                            rect.y = (float)(y - rect.height/2);
                            nodeShapes.set(vertex, rect);
                        }
                    }
                }
            }
            catch(Exception e) {
                return false;
            }
            return true;
        }

        /**
         * @return the xColumn
         */
        public AbstractDoubleColumn getXColumn() {
            return xColumn;
        }

        /**
         * @param column the xColumn to set
         */
        public void setXColumn(AbstractDoubleColumn column) {
            xColumn = column;
        }

        /**
         * @return the yColumn
         */
        public AbstractDoubleColumn getYColumn() {
            return yColumn;
        }

        /**
         * @param column the yColumn to set
         */
        public void setYColumn(AbstractDoubleColumn column) {
            yColumn = column;
        }
    }

}
