/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * <b>GraphEdReader</b> is a graph reader for the GraphEd format
 * described in: <a href="http://citeseer.ist.psu.edu/140899.html">An Interchange File Format for Graphs
 * by  Michael Himsolt</a>.
 * 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory GraphReaderFactory g
 */
public class GraphEdReader extends AbstractGraphReader {
    private static Logger LOG = Logger.getLogger(GraphEdReader.class);
    /**
     * Creates a GraphEdReader.
     * @param in the input stream
     * @param name the name
     * @param graph the graph
     */
    public GraphEdReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
    }

    /**
     * {@inheritDoc}
     */
    public Float getBbox() {
        if (nodeShapes != null) {
            return nodeShapes.getBounds();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean load() throws WrongFormatException {
        GraphEdLexer  lexer = new GraphEdLexer(getIn());
        GraphEdParser parser = new GraphEdParser(lexer);    
        
        
        parser.setGraphReader(this);
        try {
            parser.graph();
        } catch (Exception e) {
            LOG.error("Cannot parse file " + getName(), e);
            return false;
        } finally {
            try {
                getIn().close();
            } catch (IOException e) {
                LOG.error("Error closing file " + getName(), e);
            }
        }
        return true;
    }

}
