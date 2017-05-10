/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.graph.DefaultGraph;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;


/**
 * Class RandomErdosRenyi
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class RandomErdosRenyi {
    /**
     * Generates a random graph with the specified number of vertices
     * and random edges
     * @param vertices the number of vertices
     * @param p the probability of connection between two vertices
     * @param r the random engine or null
     * @return a random graph
     */
    public static Graph generate(
            int vertices,
            double p,
            RandomEngine r) {
        if (r == null)
            r = RandomEngine.makeDefault();
        Uniform u = new Uniform(r);
        DefaultGraph graph = new DefaultGraph();
        for (int i = 0; i < vertices; i++) {
            graph.addVertex();
        }
        for (int v1 = 0; v1 < vertices; v1++) {
            for (int v2 = v1+1; v2 < vertices; v2++) {
                if (p > u.nextDouble())
                    graph.addEdge(v1, v2);
            }
        }
        
        return graph;
    }

}
    
