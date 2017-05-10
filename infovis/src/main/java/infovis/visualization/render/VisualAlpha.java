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
import infovis.column.NumberColumn;
import infovis.column.filter.NotNumberFilter;
import infovis.visualization.ItemRenderer;

import java.awt.*;

/**
 * <b>VisualAlpha</b> selects the transpareny of items.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class VisualAlpha extends AbstractVisualColumn {
    /** Name of the visual. */
    public static final String VISUAL = Visualization.VISUAL_ALPHA;
    private static final int RULE = AlphaComposite.SRC_OVER;
    protected double defaultAlpha = 1.0;
    protected NumberColumn alphaColumn;
    protected double amin;
    protected double amax;
    protected double scale;
    protected static final AlphaComposite[] CACHE = new AlphaComposite[65];
    
    static {
        for (int i = 0; i < 65; i++) {
            CACHE[i] = AlphaComposite.getInstance(RULE, i/64.0f);
        }
    }

    /**
     * Returns the VisualAlpha associated with a specified visualization.
     * @param vis the visualization
     * @return the VisualAlpha associated with a specified visualization
     * or <code>null</code> if it does not exist
     */
    public static VisualAlpha get(Visualization vis) {
        return (VisualAlpha)findNamed(VISUAL, vis);
    }
    
    /**
     * Simple prototype constructor.
     * @param child the childen
     */
    public VisualAlpha(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
    }
    
    /**
     * Simple prototype constructor.
     * @param c1 first child
     * @param c2 second child
     */
    public VisualAlpha(ItemRenderer c1, ItemRenderer c2) {
        super(VISUAL);
        addRenderer(c1);
        addRenderer(c2);
    }

    /**
     * {@inheritDoc}
     */
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        filter = NotNumberFilter.sharedInstance();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return alphaColumn;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (this.alphaColumn == column) return;
        super.setColumn(column);
        alphaColumn = (NumberColumn)column;
        invalidate();
    }

    /**
     * @return the current Alpha Column
     */
    public NumberColumn getAlphaColumn() {
        return alphaColumn;
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        if (alphaColumn == null 
                || alphaColumn.getMinIndex() == -1) {
            scale = 0;
        }
        else {
            amin = alphaColumn.getDoubleAt(alphaColumn.getMinIndex());
            amax = alphaColumn.getDoubleAt(alphaColumn.getMaxIndex());
            if (amin == amax)
                scale = 0;
            else
                scale = 1.0 / (amax - amin);
        }
        super.install(graphics);
    }

    /**
     * Returns the alpha value associated with a specified row.
     * @param row the row
     * @return the alpha value between 0 (translucent) and 1 (solid)
     */
    public double getAlphaAt(int row) {
        if (scale == 0)
            return defaultAlpha;
        else
            return (alphaColumn.getDoubleAt(row) - amin) * scale;
    }

    /**
     * @return the default alpha value
     */
    public double getDefaultAlpha() {
        return defaultAlpha;
    }

    /**
     * Sets the default alpha value
     * @param alpha the new value
     */
    public void setDefaultAlpha(double alpha) {
        defaultAlpha = alpha;
        invalidate();
    }
    
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (scale == 0 && defaultAlpha == 1.0) {
            super.paint(graphics, row, shape);
            return;
        }
        Composite saved = graphics.getComposite();
        try {
            graphics.setComposite(
                    CACHE[(int)(getAlphaAt(row)*64.99)]);
            super.paint(graphics, row, shape);
        } finally {
            graphics.setComposite(saved);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ItemRenderer compile() {
        if (alphaColumn != null) {
            return this;
        }
        if (defaultAlpha == 0) {
            return null;
        }
        if (getRendererCount() == 0) {
            return null;
        }
        if (defaultAlpha != 1) {
            return this;
        }
        // behave just like a group
        return super.compileGroup();
    }
}
