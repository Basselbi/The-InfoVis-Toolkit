/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.property;

import infovis.Graph;
import infovis.column.AbstractDoubleColumn;
import infovis.column.DoubleColumn;
import infovis.graph.Edge;
import infovis.graph.Vertex;
import infovis.graph.jung.JungWrapper;

/**
 * Class BetweennessCentrality
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class BetweennessCentrality extends DoubleColumn {
    protected Graph graph;
    /** Name of the optional Column containing the number of outgoing edges. */
    public static final String CENTRALITY_COLUMN = "[Betweenness Centrality]";
    
    protected String vertexColumnName;
    protected String edgeColumnName;

    /**
     * Create a BetweennessCentrality column manager
     * @param graph
     * @param name column name
     * @param edge edge column name
     */
    public BetweennessCentrality(Graph graph, String name, String edge) {
        super(name);
        this.graph = graph;
        this.vertexColumnName = name;
        this.edgeColumnName = name;
    }

    /**
     * Create a BetweennessCentrality column manager
     * @param graph
     * @param name column name
     */
    public BetweennessCentrality(Graph graph, String name) {
        this(graph, name, null);
    }

    
    /**
     * @param name
     * @param graph
     */
    protected BetweennessCentrality(Graph graph) {
        this(graph, CENTRALITY_COLUMN, null);
        this.graph = graph;
    }
    

    /**
     * Updates the column
     */
//    @Override
    protected void update() {
//        try {
//            setReadOnly(false);
            edu.uci.ics.jung.graph.Graph<Vertex, Edge> g = JungWrapper.wrap(graph);
            edu.uci.ics.jung.algorithms.importance.BetweennessCentrality<Vertex, Edge> bc 
                = new edu.uci.ics.jung.algorithms.importance.BetweennessCentrality<Vertex, Edge>(
                        g, true, edgeColumnName != null);
            bc.setRemoveRankScoresOnFinalize(false);
            bc.evaluate();
            
            for (Vertex v : g.getVertices()) {
                double d = bc.getVertexRankScore(v);
                setExtend(v.getId(), d);
            }
            if (edgeColumnName != null) {
                AbstractDoubleColumn ec = DoubleColumn.findColumn(
                        graph.getEdgeTable(), 
                        edgeColumnName);
                for (Edge e : g.getEdges()) {
                    double d = bc.getEdgeRankScore(e);
                    ec.setExtend(e.getId(), d);
                }
            }
//          }
//          finally {
//            setReadOnly(true);
//        }
        
    }

    /**
     * Returns the out degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     * @param vertexColumnName name of the vertex column to create/update
     * @param edgeColumnName name of the edge column to create/update or null
     *
     * @return the betweenness centrality column associated with the graph.
     */
    public static AbstractDoubleColumn getColumn(
            Graph graph, 
            String vertexColumnName, 
            String edgeColumnName) {
        AbstractDoubleColumn bcColumn =
            AbstractDoubleColumn.getColumn(
                    graph.getVertexTable(),
                    vertexColumnName);
        if (bcColumn == null) {
            BetweennessCentrality centrality = new BetweennessCentrality(
                    graph, 
                    vertexColumnName,
                    edgeColumnName);
            centrality.update();
            bcColumn = centrality;
            graph.getVertexTable().addColumn(centrality);
        }
        return bcColumn;
    }
    
    /**
     * Returns the out degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     * @param vertexColumnName name of the vertex column to create/update
     *
     * @return the betweenness centrality column associated with the graph.
     */
    public static AbstractDoubleColumn getColumn(
            Graph graph, 
            String vertexColumnName) {
        return getColumn(graph, vertexColumnName, null);
    }

    /**
     * Returns the out degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     *
     * @return the betweenness centrality column associated with the graph.
     */
    public static AbstractDoubleColumn getColumn(
            Graph graph) {
        return getColumn(graph, CENTRALITY_COLUMN, null);
    }

}
