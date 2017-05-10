/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.AbstractDoubleColumn;
import infovis.column.DoubleColumn;
import infovis.column.IntColumn;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

/**
 * <b>MCIGraphReader</b> reads graph using the MCL graph format
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory GraphReaderFactory mci
 */
public class MCIGraphReader extends AbstractGraphReader {
    protected AbstractDoubleColumn weightColumn;
    private StringBuffer buffer = new StringBuffer();
    private String token;
    
    private static final Logger LOG = Logger.getLogger(MCIGraphReader.class);
    
    /**
     * Creates an AdjGraphReader.
     * @param in the input stream
     * @param name the name
     * @param graph the graph
     */
    public MCIGraphReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
    }
    
    /**
     * @return the weightColumn
     */
    public AbstractDoubleColumn getWeightColumn() {
        if (weightColumn == null) {
            weightColumn = DoubleColumn.findColumn(graph.getEdgeTable(),"weight");
        }
        return weightColumn;
    }
    
    /**
     * @param weightColumn the weightColumn to set
     */
    public void setWeightColumn(DoubleColumn weightColumn) {
        this.weightColumn = weightColumn;
    }
    
    /**
     * {@inheritDoc}
     */
    public Float getBbox() {
        return null;
    }
    
    protected String nextToken() throws IOException {
        int c = read();
        while (Character.isSpaceChar(c) || c=='\n' || c == '\r') {
            c = read();
        }
        if (c == -1) {
            token = null;
        }
        else {
            buffer.setLength(0);
            do {
                buffer.append((char)c);
                c = read();
            }
            while (c != -1 && ! Character.isSpaceChar(c) && c != '\n' && c != '\r');
            token = buffer.toString();
        }
        return token;
    }
    
    protected boolean expect(String name) throws IOException {
        if (nextToken()==null || !name.equals(token)) {
            LOG.error("Expected '"+name+"' got "+token);
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean load() throws WrongFormatException {
        graph.setDirected(false);
        
        try {
            while (nextToken() != null) {
                if ("(mclheader".equals(token)) 
                    break;
            }
            if (token == null) return false;
            if (! expect("mcltype")) return false;
            if (! expect("matrix")) return false;
            if (! expect("dimensions")) return false;
            if (nextToken() == null) {
                LOG.error("Expected dimensions at EOF");
                return false;
            }
            int index = token.indexOf('x');
            if (index == -1) {
                LOG.error("expected {width}x{height} got '"+token+"'");
                return false;
            }
            int w = Integer.parseInt(token.substring(0,index));
            int h = Integer.parseInt(token.substring(index+1));
            if (!expect(")")) return false;
            IntArrayList rows = null;
            IntArrayList cols = null;
            while(nextToken() != null) {
                if ("(mcldoms".equals(token)) {
                    if (w != h) {
                        LOG.warn("Width and height are different");
                    }
                    rows = cols = new IntArrayList(h);
                    while (nextToken() != null) {
                        if ("$".equals(token)) break;
                        rows.add(Integer.parseInt(token));
                    }
                    if (! expect(")")) return false;

                    if (rows.size() != h) {
                        LOG.warn("Not enough rows in domains, "+rows.size()+" read instead of "+h);
                    }
                }
                else if ("(mclcols".equals(token)) {
                    cols = new IntArrayList();
                    while (nextToken() != null) {
                        if ("$".equals(token)) break;
                        cols.add(Integer.parseInt(token));
                    }
                    if (! expect(")")) return false;
                    if (cols.size() != w) {
                        LOG.warn("Not enough columns "+cols.size()+" read instead of "+w);
                    }
                }
                else if ("(mclrows".equals(token)) {
                    rows = new IntArrayList();
                    while (nextToken() != null) {
                        if ("$".equals(token)) break;
                        rows.add(Integer.parseInt(token));
                    }
                    if (! expect(")")) return false;
                    if (rows.size() != h) {
                        LOG.warn("Not enough rows "+rows.size()+" read instead of "+h);
                    }
                }
                else if ("(mclmatrix".equals(token)) {
                    if (! expect("begin")) return false;

                    OpenIntIntHashMap row2vertex = new OpenIntIntHashMap(h);
                    IntColumn id = IntColumn.findColumn(graph.getVertexTable(), "id");
                    if (rows != null) {
                        for (int r = 0; r < h; r++) {
                            int v = graph.addVertex();
                            int row = rows.get(r);
                            row2vertex.put(row, v);
                            id.setExtend(v, row);
                        }
                    }
                    else {
                        for (int r = 0; r < h; r++) {
                            int v = graph.addVertex();
                            row2vertex.put(r, v);
                            id.setExtend(v, r);
                        }
                    }
                    OpenIntIntHashMap col2vertex;
                    if (cols == rows) {
                        col2vertex = row2vertex;
                    }
                    else if (cols != null) {
                        col2vertex = new OpenIntIntHashMap(w);
                        for (int r = 0; r < h; r++) {
                            int v = graph.addVertex();
                            int col = rows.get(r);
                            col2vertex.put(col, v);
                            id.setExtend(v, col);
                        }
                    }
                    else {
                        col2vertex = new OpenIntIntHashMap(w);
                        for (int c = 0; c < w; c++) {
                            int v = graph.addVertex();
                            col2vertex.put(c, v);
                            id.setExtend(v, c);
                        }
                    }
                    while (nextToken() != null) {
                        if (")".equals(token)) {
                            return true;
                        }
                        int row = Integer.parseInt(token);
                        int v;
                        if (!row2vertex.containsKey(row)) {
                            LOG.error("Unknown row "+row);
                            v = graph.addVertex();
                            row2vertex.put(row, v);
                            id.setExtend(v, row);
                        }
                        else {
                            v = row2vertex.get(row);
                        }
                        while (nextToken() != null) {
                            if ("$".equals(token)) break;
                            index = token.indexOf(':');
                            int col;
                            double value = 0;
                            if (index == -1) {
                                col = Integer.parseInt(token);
                            }
                            else {
                                col = Integer.parseInt(token.substring(0,index));
                                value = Double.parseDouble(token.substring(index+1));
                            }
                            int v2;
                            if (!col2vertex.containsKey(col)) {
                                LOG.error("Unknown column"+col);
                                v2 = graph.addVertex();
                                col2vertex.put(col, v);
                                id.setExtend(v, col);
                            }
                            else {
                                v2 = col2vertex.get(col);
                            }
                            int edge = graph.findEdge(v, v2);
                            getWeightColumn().addExtend(edge, value);
                        }
                    }
                    break;
                }
            }
        }
        catch(IOException e) {
            LOG.error("Error reading MCI graph", e);
        }
        return false;
    }
}
