/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.jung;

import infovis.graph.DefaultGraph;
import infovis.graph.Edge;
import infovis.graph.Vertex;
import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Class DirectedJungWrapper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class DirectedJungWrapper extends JungWrapper 
    implements DirectedGraph<Vertex,Edge> {
    
    /**
     * @return a factory that create JungWrapper graphs.
     */
    public static final Factory<DirectedGraph<Vertex,Edge>> getFactory() {
        return new Factory<DirectedGraph<Vertex,Edge>> () {
            public DirectedGraph<Vertex,Edge> create() {
                return new DirectedJungWrapper(new DefaultGraph(true));  
            }
        };
    }

    /**
     * Creates a wrapper around an undirected graph.
     * @param graph the graph
     */
    public DirectedJungWrapper(infovis.Graph graph) {
        super(graph);
        assert(graph.isDirected());
    }

}
