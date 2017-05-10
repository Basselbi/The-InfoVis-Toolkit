/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.FilterColumn;
import infovis.utils.RowFilter;
import infovis.visualization.ItemRenderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * <b>VisualFilter</b> is a renderer that filters the items
 * according to a column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class VisualFilter extends AbstractVisualColumn implements RowFilter {
    /** The default name of this visual. */
    public static final String VISUAL = Visualization.VISUAL_FILTER;
    protected RowFilter filter;
    protected Column column;
    protected RowFilter extraFilter;

    /**
     * Return the VisualFilter in the specified visualization.
     * @param vis the visualization
     * @return the VisualFilter
     */
    public static VisualFilter get(Visualization vis) {
        return (VisualFilter)findNamed(VISUAL, vis);
    }
    
    /**
     * Return the VisualFilter in the specified visualization.
     * @param vis the visualization
     * @return the VisualFilter
     */
    public static VisualFilter get(String name, Visualization vis) {
        return (VisualFilter)findNamed(name, vis);
    }
    
    /**
     * Creates a VisualFilter with the specified child.
     * @param child the child
     */
    public VisualFilter(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
    }
    
    /**
     * Creates a visual filter with the specified name and child.
     * @param name the name
     * @param child the child
     */
    public VisualFilter(String name, ItemRenderer child) {
        super(name);
        addRenderer(child);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        setColumn(getDefault());
        return this;
    }
    
    protected Column getDefault() {
        return FilterColumn.findColumn(
                visualization.getTable(),
                Table.FILTER_COLUMN);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return column;
    }

    /**
     * @return the RowFilter
     */
    public RowFilter getRowFilter() {
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (filter == column) return;
        super.setColumn(column);
        filter = (RowFilter)column;
        this.column = column;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        if (filter == null) return false;
        return filter.isFiltered(row)
            || (extraFilter != null && extraFilter.isFiltered(row));
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (! isFiltered(row)) {
            super.paint(graphics, row, shape);
        }
        else {
            // filtered
            row = 1;
        }
    }

    /**
     * @return the extra filter
     */
    public RowFilter getExtraFilter() {
        return extraFilter;
    }
    
    /**
     * Sets the extra filter.
     * @param extraFilter the filter
     */
    public void setExtraFilter(RowFilter extraFilter) {
        if (this.extraFilter == extraFilter) return;
        this.extraFilter = extraFilter;
        repaint();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        if (isFiltered(row)) return false;
        return super.pick(hitBox, row, shape);
    }
}
