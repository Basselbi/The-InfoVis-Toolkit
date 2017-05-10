/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.column.ColumnFilterException;
import infovis.metadata.VisualRole;
import infovis.visualization.ItemRenderer;
import infovis.visualization.VisualColumnDescriptor;
import infovis.visualization.color.Colors;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Abstract class for item renderers that are also Visual Column Descriptors.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */
public abstract class AbstractVisualColumn extends AbstractItemRenderer
        implements VisualColumnDescriptor, ColumnFilter, ChangeListener {
    protected boolean invalidate;
    protected ColumnFilter filter;

    protected AbstractVisualColumn(String name) {
        super(name);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto,
            Visualization vis) {
        super.instantiateChildren(proto, vis);
        setColumn(findDefaultColumn());
        return this;
    }
    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (visualization != null) {
            visualization.invalidate(getColumn());
        }
    }
    
    /**
     * @return a default value for the column
     */
    public Column findDefaultColumn() {
        Column col = VisualRole.getColumn(visualization.getTable(), getName());
        if (filter != null && filter.filter(col))
            return null;
        return col;
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (column != null 
                && filter != null 
                && filter.filter(column))
            throw new ColumnFilterException("Invalid column filter");
        Column old = getColumn();
        if (old != null) {
            old.removeChangeListener(this);
        }
        if (column != null) {
            column.addChangeListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColumnFilter getFilter() {
        return filter;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFilter(ColumnFilter filter) {
        this.filter = filter;
    }

    /**
     * {@inheritDoc}
     */
    public boolean filter(Column column) {
        if (filter == null) return false;
        return filter.filter(column);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInvalidate() {
        return invalidate;
    }

    /**
     * {@inheritDoc}
     */
    public void setInvalidate(boolean b) {
        invalidate = b;
    }

    /**
     * Invalidates the visualization.
     */
    public void invalidate() {
        Visualization vis = getVisualization();
        if (vis == null) {
            return;
        }
        vis.fireVisualColumnDescriptorChanged(getName());
        if (isInvalidate()) {
            vis.invalidate();
        }
        else {
            vis.repaint();
        }
    }

    /**
     * Computes a contrasted color for the specified row.
     * @param graphics the graphics
     * @param row the row
     * @return a color that contrast with the color of the row
     */
    public Color contrastColor(Graphics2D graphics, int row) {
        Visualization vis = getVisualization();
        if (vis == null)
            return contrastColor(graphics);
        VisualColor vc = VisualColor.get(vis);
        if (vc == null)
            return contrastColor(graphics);
        Color c = contrastColor(vc.getColorAt(row));
        graphics.setColor(c);
        return c;
    }

    /**
     * Computes and install a color that contast with that installed on
     * the graphics.
     * @param graphics the graphics
     * @return the color
     */
    public static Color contrastColor(Graphics2D graphics) {
        Color c = graphics.getColor();

        c = contrastColor(c);
        graphics.setColor(c);
        return c;
    }

    private static Color contrastColor(Color c) {
        float luminance;
        if (c == null) {
            luminance = 1;
        }
        else {
            luminance = Colors.getLuminance(c);
        }

        if (luminance < 0.2) {
            return Color.WHITE;
        }
        else {
            return Color.BLACK;
        }
    }

}