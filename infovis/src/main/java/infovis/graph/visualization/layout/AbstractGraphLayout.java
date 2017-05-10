/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.Column;
import infovis.Graph;
import infovis.Visualization;
import infovis.column.AbstractBooleanColumn;
import infovis.column.NumberColumn;
import infovis.column.ShapeColumn;
import infovis.graph.visualization.GraphVisualization;
import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.utils.RectPool;
import infovis.utils.RowIterator;
import infovis.visualization.AbstractLayout;
import infovis.visualization.LinkVisualization;
import infovis.visualization.render.VisualSize;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Abstract implementation of NodeLinkGraphLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 */
public abstract class AbstractGraphLayout extends AbstractLayout implements
        NodeLinkGraphLayout {
    /** Property graph. */
    public static final String                     PROPERTY_GRAPH = "graph";
    /** Property shapePacker. */
    public static final String                     PROPERTY_SHAPE_PACKER = "shapePacker";
    /** Property fixedColumn. */
    public static final String                     PROPERTY_FIXED_COLUMN = "fixedColumn";
    /** Property linkLengthColumn. */
    public static final String                     PROPERTY_LINK_LENGTH_COLUMN = "linkLengthColumn";
    protected Graph                                graph;
    protected ShapePacker                          shapePacker;
    protected transient AbstractBooleanColumn      fixedColumn;
    protected transient NumberColumn               linkLengthColumn; 
    protected transient NodeLinkGraphVisualization visualization;
    protected transient Rectangle2D                bounds;
    protected transient ShapeColumn                shapes;
    protected transient VisualSize                 vs;
    protected transient VisualSize                 vl;
    protected Dimension                            preferredSize;

    protected AbstractGraphLayout() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIncremental() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization() {
        return visualization;
    }

    /**
     * @return the GraphVisualization
     */
    public GraphVisualization getGraphVisualization() {
        return (GraphVisualization) visualization;
    }

    /**
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * @param graph
     *            the graph to set
     */
    public void setGraph(Graph graph) {
        if (this.graph == graph)
            return;
        Graph old = this.graph;
        this.graph = graph;
        firePropertyChange(PROPERTY_GRAPH, old, graph);
    }
    
    /**
     * @return the packer
     */
    public ShapePacker getShapePacker() {
        return shapePacker;
    }
    
    /**
     * {@inheritDoc}
     */
    public NumberColumn getLinkLengthColumn() {
        return linkLengthColumn;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setLinkLengthColumn(NumberColumn length) {
        if (this.linkLengthColumn == length) return;
        Column old = this.linkLengthColumn;
        this.linkLengthColumn = length;
        invalidateVisualization();
        firePropertyChange(PROPERTY_LINK_LENGTH_COLUMN, old, length);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setShapePacker(ShapePacker packer) {
        if (packer == shapePacker) return;
        ShapePacker old = shapePacker;
        shapePacker = packer;
        invalidateVisualization();
        firePropertyChange(PROPERTY_SHAPE_PACKER, old, shapePacker);
    }

    protected void setVisualization(Visualization vis) {
        // Visualization old = visualization;
        if (visualization == null) {
            visualization = (NodeLinkGraphVisualization) vis
                    .findVisualization(NodeLinkGraphVisualization.class);
            setGraph(visualization);
            // firePropertyChange(PROPERTY_VISUALIZATION, old, visualization);
        }
        else if (vis == null) {
            shapes = null;
            vs = null;
            vl = null;
            // firePropertyChange(PROPERTY_VISUALIZATION, old, visualization);
        }
        else {
            assert (visualization == (NodeLinkGraphVisualization) vis
                    .findVisualization(NodeLinkGraphVisualization.class));
        }
        shapes = visualization.getShapes();
        vs = VisualSize.get(vis);
        vl = (VisualSize)VisualSize.findNamed("length", visualization.getLinkVisualization());
        if (vl != null) {
            setLinkLengthColumn((NumberColumn)vl.getColumn());
        }
        else {
            setLinkLengthColumn(null);
        }
    }

    protected void unsetVisualization() {
        // visualization = null;
        // setGraph(null);
        // shapes = null;
        // fixed = null;
    }

    protected void invalidateVisualization() {
        if (visualization != null)
            visualization.invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        try {
            setVisualization(vis);
            this.bounds = bounds;
            if (shapePacker == null) {
                computeShapes(bounds, visualization);
            }
            else {
                
            }
        } finally {
            unsetVisualization();
        }
    }

    /**
     * Computes the sizes of the nodes.
     * 
     * @param bounds
     *            the visualization bounds
     * @param vis
     *            the visualization
     */
    public void computeShapes(Rectangle2D bounds, NodeLinkGraphVisualization vis) {
        recomputeSizes();
    }

    /**
     * {@inheritDoc}
     */
    public void incrementLayout(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFinished() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        return preferredSize;
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
        preferredSize = null;
    }

    protected void recomputeSizes() {
        for (RowIterator iter = visualization.iterator(); iter.hasNext();) {
            int v = iter.nextRow();

            Rectangle2D.Float rect = getRectAt(v);
            if (rect == null) {
                rect = createRect();
            }
            rect = initRect(rect);
            setRectSizeAt(v, rect);
            setShapeAt(v, rect);
        }
    }

    /**
     * @see infovis.visualization.DefaultVisualization#getRectAt(int)
     */
    protected Rectangle2D.Float getRectAt(int row) {
        return visualization.getRectAt(row);
    }

    /**
     * Allocates a new rectangle.
     */
    protected Rectangle2D.Float createRect() {
        return RectPool.allocateRect();
    }

    /**
     * Initialize the specified rectangle to any desired default value such as a
     * random one.
     * 
     * @param rect
     *            the rectangle
     * @return the initialized rectangle
     */
    protected Rectangle2D.Float initRect(Rectangle2D.Float rect) {
        return rect;
    }

    /**
     * @see infovis.visualization.DefaultVisualization#getShapes()
     */
    public ShapeColumn getShapes() {
        return visualization.getShapes();
    }

    /**
     * Sets the size of the specified rectangle.
     * 
     * @param row
     *            the row
     * @param rect
     *            the rectangle
     */
    protected void setRectSizeAt(int row, Rectangle2D.Float rect) {
        vs.setRectSizeAt(row, rect);
    }

    // public double getSizeAtNO(int row) {
    // return vs.getSizeAta(row);
    // }

    /**
     * @see infovis.visualization.DefaultVisualization#setShapeAt(int, Shape)
     */
    protected void setShapeAt(int row, Shape s) {
        visualization.setShapeAt(row, s);
    }

    /**
     * @see infovis.visualization.DefaultVisualization#getBounds()
     */
    public Rectangle2D getBounds() {
        return visualization.getBounds();
    }

    /**
     * @see infovis.visualization.DefaultVisualization#getOrientation()
     */
    public short getOrientation() {
        return visualization.getOrientation();
    }

    /**
     * @see infovis.visualization.DefaultVisualization#getParent()
     */
    public Component getParent() {
        return visualization.getParent();
    }

    /**
     * @see infovis.graph.visualization.NodeLinkGraphVisualization#getLinkShapes()
     */
    public ShapeColumn getLinkShapes() {
        return visualization.getLinkShapes();
    }

    /**
     * @see infovis.graph.visualization.NodeLinkGraphVisualization#getLinkVisualization()
     */
    public LinkVisualization getLinkVisualization() {
        return visualization.getLinkVisualization();
    }

    /**
     * @return the fixedColumn
     */
    public AbstractBooleanColumn getFixedColumn() {
        return fixedColumn;
    }

    /**
     * @param fixedColumn the fixedColumn to set
     */
    public void setFixedColumn(AbstractBooleanColumn fixedColumn) {
        if (this.fixedColumn == fixedColumn) return;
        Column old = this.fixedColumn;
        this.fixedColumn = fixedColumn;
        firePropertyChange(PROPERTY_FIXED_COLUMN, old, fixedColumn);
    }
    
    /**
     * Computes the bounds of the visualization.
     * @return the bounds or null
     */
    public Rectangle2D computeBounds() {
        if (visualization == null) return null;
        Rectangle2D.Double bounds = null;
        for (RowIterator iter = visualization.iterator(); iter.hasNext();) {
            int v = iter.nextRow();

            Rectangle2D rect = getRectAt(v);
            if (rect != null) {
                if (bounds == null) {
                    bounds = new Rectangle2D.Double();
                    bounds.setFrame(rect);
                }
                else {
                    bounds.add(rect);
                }
            }
        }
        return bounds;
    }

}
