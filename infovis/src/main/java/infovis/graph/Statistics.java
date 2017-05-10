/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.graph.algorithm.DijkstraShortestPath;
import infovis.graph.algorithm.DijkstraShortestPath.Predecessor;
import infovis.utils.RowIterator;

import java.util.ArrayList;
import java.util.Iterator;

import cern.colt.list.DoubleArrayList;

/**
 * Class Statistics
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class Statistics {

    /**
     * For each vertex, the net fraction of edges that actually exist in the neighborhood of distance 1 around
     * each vertex vs all possible edges that could exist are computed
     * The clustering coefficient measures the degree to which a node's neighbors are neighbors with each other
     * Note: You can use cern.jet.stat.Descriptive to compute various statistics from the DoubleArrayList
     * @param graph the graph for which the clustering coefficients are to be computed
     * @return the set of clustering coefficients for each vertex
     */
    public static DoubleArrayList clusteringCoefficients(Graph graph) {
        DoubleArrayList clusteringValues = new DoubleArrayList();
        for (Iterator vIt = graph.vertexIterator(); vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            ArrayList neighbors = new ArrayList(Graphs.getNeighbors(graph, v));
            int numNeighbors = neighbors.size();
            if (numNeighbors == 1) {
                clusteringValues.add(1.0);
                continue;
            }
            double numNeighborNeighborEdges = 0;
            for (int v1Idx = 0; v1Idx < numNeighbors - 1; v1Idx++) {
                Vertex v1 = (Vertex) neighbors.get(v1Idx);
                for (int v2Idx = v1Idx + 1; v2Idx < numNeighbors; v2Idx++) {
                    Vertex v2 = (Vertex) neighbors.get(v2Idx);
                    if (Graphs.isNeighborOf(graph, v1, v2)) {
                        numNeighborNeighborEdges += 1.0;
                    }
                }
            }
            double numPossibleEdges =
                neighbors.size() * (neighbors.size() - 1) / 2.0;
            double clusteringCoefficient =
                numNeighborNeighborEdges / numPossibleEdges;
            clusteringValues.add(clusteringCoefficient);
        }
        return clusteringValues;
    }
    
    /**
     * The set of average shortest path distances for each vertex.  
     * For each vertex, the shortest path distance
     * to every other vertex is measured and the average is computed.
     * Note: You can use cern.jet.stat.Descriptive to compute various statistics from the DoubleArrayList
     * @param graph the graph whose average distances are to be computed
     * @return the set of average shortest path distances for each vertex (to every other vertex); However, if the
     * graph is not strongly connected null will be returned since the graph diameter is infinite
     */
    public static DoubleArrayList averageDistances(Graph graph) {
        DoubleArrayList distances = new DoubleArrayList();
        DijkstraShortestPath shortestPath = new DijkstraShortestPath(graph);
        for (RowIterator vIt = graph.vertexIterator(); vIt.hasNext();) {
            int v = vIt.nextRow();
            double averageShortestPath = 0;
//          int ctr = 0;
            for (RowIterator nIt = graph.vertexIterator();
                nIt.hasNext();
                ) {
                int neighbor = nIt.nextRow();
                if (neighbor == v) {
                    continue;
                }
                Predecessor p = shortestPath.shortestPath(v, neighbor);
                if (p == null)
                    return null;
                averageShortestPath += p.getWeight();
            }
            averageShortestPath /= (double) (graph.getVerticesCount() - 1.0);
            distances.add(averageShortestPath);
        }
        return distances;
    }
    
    /**
     * Computes the diameter (maximum shortest path length between any vertex pair)
     * of the graph, ignoring edge weights.
     * @param g the graph
     * @return the diameter
     */
    public static int diameter(Graph g) {
        DijkstraShortestPath usp = new DijkstraShortestPath(g);
        //      This is practically fast, but it would be the best if we have an
        // implementation of All Pairs Shortest Paths(APSP) algorithm.
        double diameter = 0;

        for (RowIterator iter = g.vertexIterator(); iter.hasNext();) {
            int v1 = iter.nextRow();
            for (RowIterator jiter = iter.copy(); jiter.hasNext();) {
                int v2 = jiter.nextRow();
                Predecessor n = usp.shortestPath(v1, v2);
                if (n != null && n.getWeight() > diameter)
                    diameter = n.getWeight();
            }
        }
        return (int) diameter;
    }
    
}
