/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph;

import infovis.DynamicTable;
import infovis.Graph;
import infovis.graph.event.GraphChangedListener;
import infovis.utils.BitSet;
import infovis.utils.FilteredRowIterator;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;

import javax.swing.text.MutableAttributeSet;


/**
 * <b>FilteredGraph</b> is a filtered graph.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class FilteredGraph implements Graph {
    protected Graph graph;
    static class Filter extends BitSet implements RowFilter {
        Filter(int size) { super(size); }
        /**
         * {@inheritDoc}
         */
        public boolean isFiltered(int row) {
            return !get(row);
        }
    };
    protected Filter vertexFilter;
    protected Filter edgeFilter;
    
    /**
     * Creates a filtered graph from a graph and a filter for
     * the vertices and edges.
     * @param g the graph
     * @param vertexFilter the vertex filter
     * @param edgeFilter the edge filter
     */
    public FilteredGraph(Graph g, RowFilter vertexFilter, RowFilter edgeFilter) {
        this.graph = g;
        setFilters(vertexFilter, edgeFilter);
    }
    
    /**
     * @return the vertexPermutation
     */
    public RowFilter getVertexFilter() {
        return vertexFilter;
    }
    
    /**
     * @return the edgePermutation
     */
    public RowFilter getEdgeFilter() {
        return edgeFilter;
    }

    /**
     * Sets the filters
     * @param vertexFilter the vertex filter or null
     * @param edgeFilter the edge filter or null
     */
    public void setFilters(RowFilter vertexFilter, RowFilter edgeFilter) {
        this.vertexFilter = new Filter(graph.getVertexTable().getLastRow()+1);
        for (RowIterator viter = graph.vertexIterator(); 
            viter.hasNext(); ) {
            int v = viter.nextRow();
            if (!vertexFilter.isFiltered(v))
                this.vertexFilter.set(v);
        }
        this.edgeFilter = new Filter(graph.getEdgeTable().getLastRow()+1);
        for (RowIterator iter = graph.edgeIterator(); 
            iter.hasNext(); ) {
            int edge = iter.nextRow();
            boolean filter = 
                edgeFilter != null 
                ? edgeFilter.isFiltered(edge)
                : false;
            if (! filter) {
                filter = isVertexFiltered(graph.getFirstVertex(edge))
                    || isVertexFiltered(graph.getSecondVertex(edge));
            }
            if (! filter)
                this.edgeFilter.set(edge);
        }
    }
    
    
    /**
     * Returns <code>true</code> if the specified
     * vertex is filtered.
     * @param vertex the vertex
     * @return <code>true</code> if the vertex
     * is filtered
     */
    public boolean isVertexFiltered(int vertex) {
        return vertexFilter.isFiltered(vertex);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return graph.getName();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        graph.setName(name);
    }
    
    protected void readonly() {
        throw new UnsupportedOperationException("Filtered graph cannot be modified");
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        readonly();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean isDirected() {
        return graph.isDirected();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDirected(boolean directed) {
        graph.setDirected(directed);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getVerticesCount() {
        return vertexFilter.cardinality();
    }
    
    /**
     * {@inheritDoc}
     */
    public int addVertex() {
        readonly();
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex add() {
        readonly();
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getVertex(int v) {
        if (isVertexFiltered(v)) return null;
        return graph.getVertex(v);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeVertex(int vertex) {
        readonly();
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeVertex(Vertex vertex) {
        readonly();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getEdgesCount() {
        return edgeFilter.cardinality();
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getEdge(int e) {
        if (edgeFilter.isFiltered(e)) return null;
        return graph.getEdge(e);
    }
    
    /**
     * {@inheritDoc}
     */
    public int addEdge(int v1, int v2) {
        readonly();
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge addEdge(Vertex v1, Vertex v2) {
        readonly();
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeEdge(Edge e) {
        readonly();
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeEdge(int edge) {
        readonly();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getFirstVertex(int edge) {
        if (edgeFilter.isFiltered(edge)) return -1;
        return graph.getFirstVertex(edge);
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getFirstVertex(Edge e) {
        int v = getFirstVertex(e.getId());
        if (v == -1) return null;
        return getVertex(v);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOtherVertex(int edge, int vertex) {
        if (edgeFilter.isFiltered(edge) 
                || vertexFilter.isFiltered(vertex))
            return -1;
        return graph.getOtherVertex(edge, vertex);
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex getOtherVertex(Edge edge, Vertex vertex) {
        int v = getOtherVertex(edge.getId(), vertex.getId());
        if (v == -1) return null;
        return getVertex(v);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSecondVertex(int edge) {
        if (edgeFilter.isFiltered(edge)) return -1;
        return graph.getSecondVertex(edge);
    }

    /**
     * {@inheritDoc}
     */
    public Vertex getSecondVertex(Edge e) {
        int v = getSecondVertex(e.getId());
        if (v == -1) return null;
        return getVertex(v);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOutEdgeAt(int vertex, int index) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getOutEdgeAt(Vertex vertex, int index) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getInEdgeAt(int vertex, int index) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getInEdgeAt(Vertex vertex, int index) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getEdge(int v1, int v2) {
        if (vertexFilter.isFiltered(v1) || vertexFilter.isFiltered(v2))
            return -1;
        int e = graph.getEdge(v1, v2);
        if (edgeFilter.isFiltered(e))
            return -1;
        return e;
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge getEdge(Vertex v1, Vertex v2) {
        int e = getEdge(v1.getId(), v2.getId());
        if (e == -1)
            return null;
        return getEdge(e);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator() {
        return edgeFilter.iterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator newEdgeIterator() {
        return new FilteredRowIterator(graph.edgeIterator(0), edgeFilter);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(int vertex, RowIterator it) {
        if (isVertexFiltered(vertex)) {
            return null;
        }
        if (it == null || !(it instanceof FilteredRowIterator)) {
            it = newEdgeIterator();
        }
        FilteredRowIterator fit = (FilteredRowIterator)it; 
        RowIterator sub = fit.getIterator();
        sub = graph.edgeIterator(vertex, sub);
        fit.setState(-1);
        return it;
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator(int vertex) {
        return edgeIterator(vertex, null);
    }

	/**
	 * @param l
	 * @see infovis.Graph#addGraphChangedListener(infovis.graph.event.GraphChangedListener)
	 */
	public void addGraphChangedListener(GraphChangedListener l) {
		// TODO Auto-generated method stub
		// 
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#edgeIterator(infovis.graph.Vertex)
	 */
	public RowIterator edgeIterator(Vertex vertex) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see infovis.Graph#findEdge(int, int)
	 */
	public int findEdge(int v1, int v2) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see infovis.Graph#findEdge(infovis.graph.Vertex, infovis.graph.Vertex)
	 */
	public Edge findEdge(Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#getDegree(int)
	 */
	public int getDegree(int vertex) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#getDegree(infovis.graph.Vertex)
	 */
	public int getDegree(Vertex vertex) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 * @see infovis.Graph#getEdgeTable()
	 */
	public DynamicTable getEdgeTable() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#getInDegree(int)
	 */
	public int getInDegree(int vertex) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#getInDegree(infovis.graph.Vertex)
	 */
	public int getInDegree(Vertex vertex) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#getOutDegree(int)
	 */
	public int getOutDegree(int vertex) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#getOutDegree(infovis.graph.Vertex)
	 */
	public int getOutDegree(Vertex vertex) {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 * @see infovis.Graph#getVertexTable()
	 */
	public DynamicTable getVertexTable() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @param it
	 * @return
	 * @see infovis.Graph#inEdgeIterator(int, infovis.utils.RowIterator)
	 */
	public RowIterator inEdgeIterator(int vertex, RowIterator it) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#inEdgeIterator(int)
	 */
	public RowIterator inEdgeIterator(int vertex) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#inEdgeIterator(infovis.graph.Vertex)
	 */
	public RowIterator inEdgeIterator(Vertex vertex) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @param it
	 * @return
	 * @see infovis.Graph#outEdgeIterator(int, infovis.utils.RowIterator)
	 */
	public RowIterator outEdgeIterator(int vertex, RowIterator it) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#outEdgeIterator(int)
	 */
	public RowIterator outEdgeIterator(int vertex) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param vertex
	 * @return
	 * @see infovis.Graph#outEdgeIterator(infovis.graph.Vertex)
	 */
	public RowIterator outEdgeIterator(Vertex vertex) {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @param l
	 * @see infovis.Graph#removeGraphChangedListener(infovis.graph.event.GraphChangedListener)
	 */
	public void removeGraphChangedListener(GraphChangedListener l) {
		// TODO Auto-generated method stub
		// 
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 * @see infovis.Graph#vertexIterator()
	 */
	public RowIterator vertexIterator() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 * @see infovis.Metadata#getClientProperty()
	 */
	public MutableAttributeSet getClientProperty() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 * @see infovis.Metadata#getMetadata()
	 */
	public MutableAttributeSet getMetadata() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException();
	}
    
    
}
