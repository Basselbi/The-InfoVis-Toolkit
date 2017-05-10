/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.utils.RowIterator;

import java.util.HashSet;
import java.util.Set;

import cern.colt.map.OpenIntIntHashMap;

/**
 * Class Graphs
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class Graphs {
    public static OpenIntIntHashMap getNeighbors(Graph graph, int vertex) {
        OpenIntIntHashMap neighbors = new OpenIntIntHashMap();
        for (RowIterator iter = graph.edgeIterator(vertex); iter.hasNext(); ) {
            int edge = iter.nextRow();
            int v = graph.getOtherVertex(edge, vertex);
            if (neighbors.containsKey(v)) {
                neighbors.put(v, neighbors.get(v)+1);
            }
            else {
                neighbors.put(v, 1);
            }
        }
        return neighbors;
    }
    
    public static Set getNeighbors(Graph graph, Vertex vertex) {
        HashSet neighbors = new HashSet();
        for (RowIterator iter = graph.edgeIterator(vertex); iter.hasNext(); ) {
            Edge edge = (Edge)iter.next();
            Vertex v = graph.getOtherVertex(edge, vertex);
            neighbors.add(v);
        }
        return neighbors;
    }
    
    public static boolean isNeighborOf(Graph graph, int vertex, int v2) {
        for (RowIterator iter = graph.edgeIterator(vertex); iter.hasNext(); ) {
            int edge = iter.nextRow();
            int v = graph.getOtherVertex(edge, vertex);
            if (v == v2) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNeighborOf(Graph graph, Vertex vertex, Vertex v2) {
        return isNeighborOf(graph, vertex.getId(), v2.getId());
    }
}
