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
import infovis.utils.RowIterator;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Class JungWrapper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class JungWrapper implements Graph<Vertex, Edge> {
    infovis.Graph graph;

    /**
     * Creates a Jung wrapper around an infovis Graph. 
     * @param graph the graph
     * @return the JUNG wrapper
     */
    public static Graph<Vertex,Edge> wrap(infovis.Graph graph) {
        if (graph.isDirected()) {
            return new DirectedJungWrapper(graph);
        }
        else {
            return new UndirectedJungWrapper(graph);
        }
    }
    /**
     * Creates a wrapper around an infovis graph.
     * @param graph the graph
     */
    protected JungWrapper(infovis.Graph graph) {
        this.graph = graph;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(
            Edge edge, 
            Collection<? extends Vertex> vertices) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override    
    public boolean addEdge(Edge edge, Vertex v1, Vertex v2) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(
            Edge arg0,
            Vertex arg1,
            Vertex arg2,
            EdgeType arg3) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vertex getDest(Edge edge) {
        return graph.getSecondVertex(edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Vertex> getEndpoints(Edge edge) {
        return new Pair<Vertex>(
                graph.getFirstVertex(edge),
                graph.getSecondVertex(edge));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Edge> getInEdges(final Vertex vertex) {
        if (!containsVertex(vertex))
            return null;
        
        Collection<Edge> col = new AbstractCollection<Edge>() {
            /**
             * {@inheritDoc}
             */
            public int size() {
                return graph.getInDegree(vertex);
            }
            
            /**
             * {@inheritDoc}
             */
            public Iterator<Edge> iterator() {
                return graph.inEdgeIterator(vertex);
            }
        };
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vertex getOpposite(Vertex vertex, Edge edge) {
        return graph.getOtherVertex(edge, vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Edge> getOutEdges(final Vertex vertex) {
        Collection<Edge> col = new AbstractCollection<Edge>() {
            /**
             * {@inheritDoc}
             */
            public int size() {
                return graph.getOutDegree(vertex);
            }
            
            /**
             * {@inheritDoc}
             */
            public Iterator<Edge> iterator() {
                return graph.outEdgeIterator(vertex);
            }
        };
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPredecessorCount(Vertex vertex) {
        return graph.getInDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Vertex> getPredecessors(Vertex vertex) {
        if (! containsVertex(vertex))
            return null;
        Collection<Vertex> col = new HashSet<Vertex>();
        for (RowIterator iter = graph.inEdgeIterator(vertex); iter.hasNext(); ) {
            col.add(graph.getOtherVertex((Edge)iter.next(), vertex));
        }
        if (! graph.isDirected()) {
            for (RowIterator iter = graph.outEdgeIterator(vertex); iter.hasNext(); ) {
                col.add(graph.getOtherVertex((Edge)iter.next(), vertex));
            }
            
        }
        return Collections.unmodifiableCollection(col);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vertex getSource(Edge edge) {
        if (graph.isDirected())
            return graph.getFirstVertex(edge);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuccessorCount(Vertex vertex) {
        return graph.getOutDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Vertex> getSuccessors(Vertex vertex) {
        Collection<Vertex> col = new HashSet<Vertex>();
        for (RowIterator iter = graph.outEdgeIterator(vertex); iter.hasNext(); ) {
            col.add(graph.getOtherVertex((Edge)iter.next(), vertex));
        }
        if (! graph.isDirected()) {
            for (RowIterator iter = graph.inEdgeIterator(vertex); iter.hasNext(); ) {
                col.add(graph.getOtherVertex((Edge)iter.next(), vertex));
            }
        }
        return Collections.unmodifiableCollection(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegree(Vertex vertex) {
        return graph.getInDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDest(Vertex vertex, Edge edge) {
        return graph.getSecondVertex(edge).equals(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPredecessor(Vertex v1, Vertex v2) {
        return graph.getEdge(v1, v2) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSource(Vertex vertex, Edge edge) {
        return graph.getFirstVertex(edge).equals(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccessor(Vertex v1, Vertex v2) {
        return isPredecessor(v2, v1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegree(Vertex v) {
        return graph.getOutDegree(v);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addEdge(
            Edge e,
            Collection<? extends Vertex> vertices,
            EdgeType et) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(Vertex v) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(Edge edge) {
        return graph.getEdgeTable().isRowValid(edge.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(Vertex v) {
        return graph.getVertexTable().isRowValid(v.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int degree(Vertex v) {
        return graph.getDegree(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Edge findEdge(Vertex v1, Vertex v2) {
        Edge e = graph.getEdge(v1, v2);
        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Edge> findEdgeSet(Vertex v1, Vertex v2) {
        Collection<Edge> col = new ArrayList<Edge>();
        for (RowIterator iter = graph.outEdgeIterator(v1); iter.hasNext(); ) {
            Edge e = (Edge)iter.next();
            Vertex other = graph.getOtherVertex(e, v1);
            if (other.equals(v2))
                col.add(e);
        }
        return Collections.unmodifiableCollection(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeType getDefaultEdgeType() {
        return graph.isDirected() ? EdgeType.DIRECTED : EdgeType.UNDIRECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEdgeCount() {
        return graph.getEdgesCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEdgeCount(EdgeType et) {
        if (et == EdgeType.DIRECTED && graph.isDirected())
            return getEdgeCount();
        if (et == EdgeType.UNDIRECTED && !graph.isDirected())
            return getEdgeCount();
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeType getEdgeType(Edge edge) {
        return getDefaultEdgeType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Edge> getEdges() {
        return new AbstractCollection<Edge>() {
            /**
             * {@inheritDoc}
             */
            public Iterator<Edge> iterator() {
                return graph.edgeIterator();
            }

            /**
             * {@inheritDoc}
             */
            public int size() {
                return graph.getEdgesCount();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Edge> getEdges(EdgeType et) {
        if ((et == EdgeType.DIRECTED && graph.isDirected())
                || (et == EdgeType.UNDIRECTED && !graph.isDirected())) {
            return new AbstractCollection<Edge>() {
                /**
                 * {@inheritDoc}
                 */
                public Iterator<Edge> iterator() {
                    return graph.edgeIterator();
                }
    
                /**
                 * {@inheritDoc}
                 */
                public int size() {
                    return graph.getEdgesCount();
                }
            };
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIncidentCount(Edge edge) {
        if (graph.getEdgeTable().isRowValid(edge.getId()))
            return 2;
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Edge> getIncidentEdges(final Vertex vertex) {
        return new AbstractCollection<Edge>() {
            /**
             * {@inheritDoc}
             */
            public int size() {
                return graph.getDegree(vertex);
            }
            
            /**
             * {@inheritDoc}
             */
            public Iterator<Edge> iterator() {
                return graph.edgeIterator(vertex);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Vertex> getIncidentVertices(Edge edge) {
        if (graph.getEdgeTable().isRowValid(edge.getId()))
            return new Pair<Vertex>(
                    graph.getFirstVertex(edge), 
                    graph.getSecondVertex(edge));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNeighborCount(Vertex vertex) {
        return graph.getDegree(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Vertex> getNeighbors(Vertex vertex) {
        Collection<Vertex> col = new HashSet<Vertex>();
        for (RowIterator iter = graph.edgeIterator(vertex); iter.hasNext(); ) {
            Edge e = (Edge)iter.next();
            Vertex other = graph.getOtherVertex(e, vertex);
            col.add(other);
        }
        return Collections.unmodifiableCollection(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVertexCount() {
        return graph.getVerticesCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Vertex> getVertices() {
        return new AbstractCollection<Vertex>() {
            /**
             * {@inheritDoc}
             */
            public int size() {
                return graph.getVerticesCount();
            }
            
            /**
             * {@inheritDoc}
             */
            public Iterator<Vertex> iterator() {
                return graph.vertexIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIncident(Vertex vertex, Edge edge) {
        return edge.getFirstVertex().equals(vertex) 
            || edge.getSecondVertex().equals(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNeighbor(Vertex v1, Vertex v2) {
        for (RowIterator iter = graph.edgeIterator(v1); iter.hasNext(); ) {
            Edge e = (Edge)iter.next();
            Vertex other = graph.getOtherVertex(e, v2);
            if (other.equals(v2))
                return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(Edge edge) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(Vertex vertex) {
        return false;
    }

}
