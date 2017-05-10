/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Table;
import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.table.Item;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;
import infovis.visualization.linkShapers.DefaultLinkShaper;
import infovis.visualization.magicLens.LinkExcentricItem;
import infovis.visualization.render.VisualFilter;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;

import org.apache.log4j.Logger;

/**
 * <b>LinkVisualization</b> is a visualization for links between items managed
 * by another visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.19 $
 */
public class LinkVisualization extends StrokingVisualization implements Layout {
    /** linkShaper property. */
    public static final String  PROPERTY_LINK_SHAPER = "linkShaper";
    /** nodeShaper property. */
    public static final String  PROPERTY_NODE_SHAPER = "nodeShaper";
    /** showingSelected property. */
    public static final String  PROPERTY_SHOWING_SELECTED = "showingSelected";

    protected Visualization     nodeVisualization;
    protected ShapeColumn       nodeShapes;
    protected LinkShaper        linkShaper;
    protected NodeAccessor      nodeAccessor;
    protected boolean           showingSelected;

    protected volatile Point2D  startPos;
    protected volatile Point2D  endPos;
    private static final Logger logger               = Logger.getLogger(LinkVisualization.class);

    /**
     * Creates a LinkVisualization on a specified table
     * over the items of a specified node visualization.
     * @param table the Tale
     * @param ir the ItemRenderer
     * @param nodeVisualization the visualization to manage
     * @param accessor the node accessor to get the position
     * of link endpoints 
     */
    public LinkVisualization(
            Table table,
            ItemRenderer ir,
            Visualization nodeVisualization,
            NodeAccessor accessor) {
        super(table, ir);
        setShowExcentric(false);
        setNodeVisualization(nodeVisualization);
        setNodeAccessor(accessor);
        VisualFilter vf = VisualFilter.get(this);
        vf.setExtraFilter(new RowFilter() {
            public boolean isFiltered(int row) {
                if (showingSelected) {
                    if (isNodeSelected(nodeAccessor.getStartNode(row))
                            || isNodeSelected(nodeAccessor.getEndNode(row)))
                        return false;
                    else
                        return true;
                }
                return LinkVisualization.this.nodeVisualization
                        .isFiltered(nodeAccessor.getStartNode(row))
                        || LinkVisualization.this.nodeVisualization
                                .isFiltered(nodeAccessor.getEndNode(row));
            }
        });
    }

    /**
     * Creates a LinkVisualization on a specified table
     * over the items of a specified node visualization.
     * @param table the Tale
     * @param nodeVisualization the visualizatin to manage
     * @param accessor the node accessor to get the position
     * of link endpoints 
     */
    public LinkVisualization(
            Table table,
            Visualization nodeVisualization,
            NodeAccessor accessor) {
        this(table, null, nodeVisualization, accessor);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        setNodeVisualization(null);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Link";
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }

    /**
     * Returns true if the specified node is selected.
     * @param node the node
     * @return true if the node is selected.
     */
    public boolean isNodeSelected(int node) {
        return (!nodeVisualization.getSelection().isValueUndefined(node))
                && nodeVisualization.getSelection().get(node);
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        ShapeColumn sc = (ShapeColumn) nodeVisualization
                .getVisualColumn(VISUAL_SHAPE);
        if (nodeShapes != sc) {
            if (nodeShapes != null) {
                nodeShapes.removeChangeListener(this);
            }
            nodeShapes = sc;
            if (nodeShapes != null) {
                nodeShapes.addChangeListener(this);
            }
            else
                return;
            getLinkShaper().init(this, nodeShapes);
        }
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int link = iter.nextRow();
            setShapeAt(link, getLinkShaper().computeLinkShape(
                    link,
                    getNodeAccessor(),
                    getShapeAt(link)));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (isInvalidated()) {
            computeShapes(null);
        }
        return getShapes().getBounds().getBounds().getSize();
    }

    /**
     * {@inheritDoc}
     */
    public LabeledItem createLabelItem(Item row) {
        return new LinkExcentricItem(this, row);
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
        if (e.getSource() == nodeShapes) {
            super.invalidate();
        }
    }

    private boolean inInvalidate;

    /**
     * {@inheritDoc}
     */
    public void invalidate() {
        if (isInvalidated())
            return;
        if (inInvalidate) {
            logger.error("Cycle in LinkVisualization.invalidate()");
            return;
        }
        try {
            inInvalidate = true;
            super.invalidate();
            nodeVisualization.invalidate();
        } finally {
            inInvalidate = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return nodeVisualization.getOrientation();
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(short orientation) {
        nodeVisualization.setOrientation(orientation);
    }

    /**
     * @return the node visualization
     */
    public Visualization getNodeVisualization() {
        return nodeVisualization;
    }

    /**
     * Change the node visualization
     * @param visualization the visualization
     */
    public void setNodeVisualization(Visualization visualization) {
        if (nodeShapes != null) {
            nodeShapes.removeChangeListener(this);
            nodeShapes = null;
        }
        nodeVisualization = visualization;
    }

    /**
     * @return the node accessor
     */
    public NodeAccessor getNodeAccessor() {
        return nodeAccessor;
    }

    /**
     * Sets the node accessor
     * @param accessor the accessor
     */
    public void setNodeAccessor(NodeAccessor accessor) {
        nodeAccessor = accessor;
    }

    /**
     * @return the link shaper
     */
    public LinkShaper getLinkShaper() {
        if (linkShaper == null) {
            setLinkShaper(new DefaultLinkShaper());
        }
        return linkShaper;
    }

    /**
     * Sets the link shaper.
     * @param shaper the link shaper
     */
    public void setLinkShaper(LinkShaper shaper) {
        if (shaper == linkShaper)
            return;
        if (linkShaper != null) {
            linkShaper.init(null, null);
        }
        LinkShaper old = linkShaper;
        linkShaper = shaper;
        if (linkShaper != null) {
            linkShaper.init(this, nodeShapes);
        }
        firePropertyChange(PROPERTY_LINK_SHAPER, old, shaper);
        invalidate();
    }

    /**
     * @return true if showing the selection
     */
    public boolean isShowingSelected() {
        return showingSelected;
    }

    /**
     * Sets the showingSelected property
     * @param b the value
     */
    public void setShowingSelected(boolean b) {
        if (showingSelected == b) return;
        showingSelected = b;
        firePropertyChange(PROPERTY_SHOWING_SELECTED, !b, b);
        repaint();
    }

}