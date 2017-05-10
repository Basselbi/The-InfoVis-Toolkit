/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Graph;
import infovis.Table;
import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.Edge;
import infovis.graph.Vertex;
import infovis.graph.visualization.layout.GraphVizLayout;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;
import infovis.visualization.Layout;
import infovis.visualization.LinkVisualization;
import infovis.visualization.NodeAccessor;
import infovis.visualization.magicLens.Fisheye;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import cern.colt.list.IntArrayList;

/**
 * Node-Link Visualization for graphs.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.38 $
 * 
 * @infovis.factory VisualizationFactory "Graph Node Link" infovis.Graph
 */
public class NodeLinkGraphVisualization extends GraphVisualization 
    implements NodeAccessor {
    protected NodeLinkGraphLayout layout;
    protected boolean             paintingLinks = true;
    protected boolean             debugging     = true;
    protected LinkVisualization   linkVisualization;
    protected transient Dimension preferredSize;

    /**
     * Creates a NodeLinkGraphVisualization from a specified 
     * graph .
     * @param graph the graph
     */
    public NodeLinkGraphVisualization(Graph graph) {
        super(graph, graph.getVertexTable());
        linkVisualization = new LinkVisualization(
                graph.getEdgeTable(),
                this,
                this);
        linkVisualization.setOrientation(ORIENTATION_INVALID);
    }

    /**
     * Creates a NodeLinkGraphVisualization from the 
     * graph associated with a specified Table.
     * @param table the table associated with a graph
     */
    public NodeLinkGraphVisualization(Table table) {
        this(DefaultGraph.getGraph(table));
    }
    
    /**
     * {@inheritDoc}
     */
    public int addVertex() {
        int v = super.addVertex();
        if (permutation != null) {
            permutation.add(v);  // make it visible
        }
        return v;
    }
    
    /**
     * {@inheritDoc}
     */
    public Vertex add() {
        return getVertex(addVertex());
    }
    
    /**
     * {@inheritDoc}
     */
    public int addEdge(int v1, int v2) {
        int edge = super.addEdge(v1, v2);
        if (permutation != null) {
            permutation.add(v1);
            permutation.add(v2);
            Permutation lPermutation = linkVisualization.getPermutation();
            if (lPermutation != null) {
                lPermutation.add(edge);
            }
        }
        return edge;
    }
    
    /**
     * {@inheritDoc}
     */
    public Edge addEdge(Vertex v1, Vertex v2) {
        return getEdge(addEdge(v1.getId(), v2.getId()));
    }

    /**
     * {@inheritDoc}
     */
    public void paintItems(Graphics2D graphics, Rectangle2D bounds) {
        if (isPaintingLinks())
            linkVisualization.paint(graphics, bounds);
        super.paintItems(graphics, bounds);
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate() {
        super.invalidate();
        // linkVisualization.invalidate();
        preferredSize = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setFisheye(Fisheye fisheye) {
        super.setFisheye(fisheye);
        linkVisualization.setFisheye(fisheye);
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        if (index == 0)
            return linkVisualization;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void validateShapes(Rectangle2D bounds) {
        super.validateShapes(bounds);
        if (!layout.isFinished()) {
            layout.incrementLayout(bounds, this);
            repaint();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return getNodeLinkGraphLayout();
    }
    
    /**
     * @return the NodeLinkGraphVisualization associated with this visualization.
     */
    public NodeLinkGraphLayout getNodeLinkGraphLayout() {
        if (layout == null) {
            setLayout(new GraphVizLayout());
        }
        return layout;
    }

    /**
     * {@inheritDoc}
     */
    public void setLayout(NodeLinkGraphLayout layout) {
        if (this.layout == layout)
            return;
        firePropertyChange(PROPERTY_LAYOUT, this.layout, layout);
        this.layout = layout;
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        if (preferredSize == null)
            preferredSize = super.getPreferredSize();
        return preferredSize;
    }

    /**
     * @return true if the links are painted.
     */
    public boolean isPaintingLinks() {
        return paintingLinks;
    }

    /**
     * Specifies whether the links are painted or not.
     * @param b true if the links should be painted
     */
    public void setPaintingLinks(boolean b) {
        if (paintingLinks != b) {
            paintingLinks = b;
            repaint();
        }
    }
    
    /**
     * @return the linkVisualization
     */
    public LinkVisualization getLinkVisualization() {
        return linkVisualization;
    }

    /**
     * @return Returns the linkShapes.
     */
    public ShapeColumn getLinkShapes() {
        return linkVisualization.getShapes();
    }

    // NodeAccessor interface
    /**
     * {@inheritDoc}
     */
    public int getStartNode(int link) {
        return getGraph().getFirstVertex(link);
    }

    /**
     * {@inheritDoc}
     */
    public int getEndNode(int link) {
        return getGraph().getSecondVertex(link);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator vertexIterator() {
        return iterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getVerticesCount() {
        // TODO Auto-generated method stub
        return getRowCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getEdgesCount() {
        return linkVisualization.getRowCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator edgeIterator() {
        return linkVisualization.iterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRowValid(int row) {
        return super.isRowValid(row) && getRowIndex(row) != -1; 
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPermutation(Permutation perm) {
        super.setPermutation(perm);
        if (perm == null) {
            linkVisualization.setPermutation(null);
        }
        else {
            IntArrayList goodEdges = new IntArrayList();
            boolean shouldFilter = false;
            for (RowIterator iter = edgeIterator(); iter.hasNext(); ) {
                int edge = iter.nextRow();
                int v1 = getFirstVertex(edge);
                int v2 = getSecondVertex(edge);
                if (isRowValid(v1) && isRowValid(v2)) {
                    goodEdges.add(edge);
                }
                else {
                    shouldFilter = true;
                }
            }
            if (shouldFilter) {
                linkVisualization.setPermutation(new Permutation(goodEdges));
            }
            else {
                linkVisualization.setPermutation(null);
            }
        }
    }
    
}
