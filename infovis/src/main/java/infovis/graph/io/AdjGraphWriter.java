/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.AbstractDoubleColumn;
import infovis.column.DoubleColumn;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class AdjGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory GraphWriterFactory adj Adjacency
 * @infovis.factory GraphWriterFactory pairs Adjacency
 */
public class AdjGraphWriter extends AbstractWriter {
    protected Graph graph;
    protected String separator = "\t";
    protected AbstractDoubleColumn weightColumn;

    /**
     * @param name
     * @param graph
     * @throws IOException
     */
    public AdjGraphWriter(String name, Graph graph) throws IOException {
        this(open(name), name, graph);
    }

    /**
     * @param out
     * @param name
     * @param graph
     */
    public AdjGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;;
    }
    
    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }
    
    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    /**
     * @return the weightColumn
     */
    public AbstractDoubleColumn getWeightColumn() {
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
    public boolean write() {
        try {
            for (RowIterator iter = graph.edgeIterator(); iter.hasNext(); ) {
                int edge = iter.nextRow();
                
                write(Integer.toString(graph.getFirstVertex(edge)));
                write(separator);
                write(Integer.toString(graph.getSecondVertex(edge)));
                if (weightColumn != null && !weightColumn.isValueUndefined(edge)) {
                    write(separator);
                    write(Double.toString(weightColumn.getDoubleAt(edge)));
                }
                writeln();
            }
            close();
        }
        catch(IOException e) {
            return false;
        }
        return true;   
    }

}
