/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.Table;
import infovis.column.NumberColumn;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Class MCIGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * @infovis.factory GraphWriterFactory mci GraphEd
 */
public class MCIGraphWriter extends AbstractWriter {
    protected Graph            graph;
    protected NumberColumn     weightColumn;
    
    public static final Logger LOG = Logger.getLogger(MCIGraphWriter.class);
    
    /**
     * Creates a writer.
     * @param name
     * @param graph
     * @throws IOException
     */
    public MCIGraphWriter(String name, Graph graph) throws IOException {
        super(name, graph.getEdgeTable());
        this.graph = graph;
    }

    /**
     * Creates a writer.
     * @param out
     * @param name
     * @param graph
     */
    public MCIGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
    }
    
    /**
     * @return the weightColumn
     */
    public NumberColumn getWeightColumn() {
        return weightColumn;
    }
    
    /**
     * @param weightColumn the weightColumn to set
     */
    public void setWeightColumn(NumberColumn weightColumn) {
        this.weightColumn = weightColumn;
    }

    /**
     * {@inheritDoc}
     */
    public boolean write() {
        try {
            int n = graph.getVerticesCount();
            RowIterator newIter = graph.newEdgeIterator();
            write(
                    "(mclheader\n"
                    +"mcltype matrix\n"
                    +"dimensions "+n+"x"+n+"\n"
                    +")\n");
            write("(mcldoms\n");
            for (RowIterator vIter = graph.vertexIterator(); vIter.hasNext(); ) {
                int v1 = vIter.nextRow();
                write(Integer.toString(v1));
                write(" ");
            }
            write("$\n)\n");
            write(
                    "(mclmatrix\n"
                    +"begin\n");
            for (RowIterator vIter = graph.vertexIterator(); vIter.hasNext(); ) {
                int v1 = vIter.nextRow();
                boolean hasLoop = false;
                write(Integer.toString(v1));
                write(" ");
                for (RowIterator eIter = graph.edgeIterator(v1, newIter); 
                    eIter.hasNext(); ) {
                    int e = eIter.nextRow();
                    int v2 = graph.getOtherVertex(e, v1);
                    if (v1 == v2) {
                        hasLoop = true;
                        continue;
                    }
                    write("  ");
                    write(Integer.toString(v2));
                    if (weightColumn != null && !weightColumn.isValueUndefined(e)) {
                        write(":");
                        write(weightColumn.getValueAt(e));
                    }
                }
                if (hasLoop) {
                    write("  ");
                    write(Integer.toString(v1));                    
                }
                write(" $\n");
                
            }
            write(")\n");
            flush();
        }
        catch(IOException e) {
            LOG.error("Error writing MCI File", e);
            return false;
        }
        return true;
    }

}
