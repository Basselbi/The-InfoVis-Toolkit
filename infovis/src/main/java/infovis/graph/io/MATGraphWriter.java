/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * <b>MATGraphWriter</b> writes a graph as a full
 * adjacency matrix.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * @infovis.factory GraphWriterFactory mat Matrix
 */
public class MATGraphWriter extends AbstractWriter {
    private static final Logger LOG = Logger.getLogger(MATGraphWriter.class);
    
    protected Graph graph;
    

    /**
     * @param out the output stream
     * @param name name
     * @param graph the graph to write
     */
    public MATGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    public boolean write() {
        try {
            int n = graph.getVerticesCount();
            write(Integer.toString(n));
            writeln();
            for (RowIterator v1Iter = graph.vertexIterator(); v1Iter.hasNext(); ) {
                int v1 = v1Iter.nextRow(); 
                for (RowIterator v2Iter = graph.vertexIterator(); v2Iter.hasNext(); ) {
                    int v2 = v2Iter.nextRow();
                    if (graph.getEdge(v1, v2) != -1) {
                        write('1');
                    }
                    else {
                        write('0');
                    }
                    write(' ');
                }
                writeln();
            }
            writeln();
            flush();
            return true;
        }
        catch(IOException e) {
            LOG.error("Error writing graph "+getName(), e);
        }
        return false;
    }

}
