/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import infovis.*;
import infovis.metadata.VisualRole;
import infovis.visualization.*;

/**
 * <b>VisualLabel</b> is a simple implementation of an
 * ItemRenderer that shows a label. 
 * 
 * <p>More sophisticated
 * versions may derive from this class.</p>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 */
public class VisualLabel extends AbstractVisualColumn {
    /** The visual name. */
    public static final String               VISUAL        = Visualization.VISUAL_LABEL;
    /** The column used for labeling */
    protected transient Column               labelColumn;
    /** The default font */
    protected Font                           font   = new Font("Dialog", Font.PLAIN, 10);
    /** The default color used for drawing the fonts or NULL if automatic */
    protected Color                          defaultColor  = null;

    protected static final FontRenderContext FRC           = new FontRenderContext(
            null,
            false,
            false);


    /**
     * Returns the label at the specified row in the specified visualization.
     * @param vis the visualization
     * @param row the row
     * @return a label or null
     */
    public static String getLabelAt(Visualization vis, int row) {
        VisualLabel vl = get(vis);
        if (vl == null) {
            return null;
        }
        return vl.getLabelAt(row);
    }

    /**
     * Extracts the visual label from the specified visualization.
     * @param vis the visualization
     * @return the visual label of null
     */
    public static VisualLabel get(Visualization vis) {
        return (VisualLabel) findNamed(VISUAL, vis);
    }

    /**
     * Extracts the visual label from the specified ItemRenderer tree.
     * @param ir the root item renderer
     * @return the visual label of null
     */    
    public static VisualLabel get(ItemRenderer ir) {
        return (VisualLabel) findNamed(VISUAL, ir);
    }


    /**
     * Constructor with a child and a default color.
     * @param child the child renderer or null
     * @param defaultColor the default color
     */
    public VisualLabel(ItemRenderer child, Color defaultColor) {
        super(VISUAL);
        this.defaultColor = defaultColor;
        addRenderer(child);
    }

    /**
     * Constructor with a child.
     * @param child the child renderer or null
     */
    public VisualLabel(ItemRenderer child) {
        this(child, null);
    }

    /**
     * Default constructor.
     */
    public VisualLabel() {
        super(VISUAL);
    }
    
    /**
     * Constructor with a specified name.
     * @param name the name
     */
    public VisualLabel(String name) {
        super(name);
    }

    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto,
            Visualization vis) {
        super.instantiateChildren(proto, vis);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Column findDefaultColumn() {
        return VisualRole.getLabelColumn(getVisualization().getTable());
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return labelColumn;
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (column == labelColumn)
            return;
        super.setColumn(column);
        this.labelColumn = column;
        invalidate();
    }

    /**
     * Returns the defaultFont.
     * 
     * @return Font
     */
    public Font getFont() {
        if (font == null && visualization.getParent() != null)
            return visualization.getParent().getFont();
        return font;
    }

    /**
     * Sets the defaultFont.
     * 
     * @param defaultFont
     *            The defaultFont to set
     */
    public void setFont(Font defaultFont) {
        this.font = defaultFont;
        invalidate();
    }

    /**
     * Returns the label associated with the specified row.
     * 
     * @param row
     *            the row.
     * 
     * @return the label associated with the specified row.
     */
    public String getLabelAt(int row) {
        if (labelColumn == null)
            return null;
        return labelColumn.getValueAt(row);
    }

    /**
     * Returns the width of the specified label.
     * @param label the label
     * @return a width or 0
     */
    public double getWidth(String label) {
        if (label == null)
            return 0;
        return font.getStringBounds(label, FRC).getWidth();
    }

    /**
     * Returns the height of the specified label.
     * @param label the label
     * @return a height or 0
     */
    public double getHeight(String label) {
        if (label == null)
            return 0;
        return font.getStringBounds(label, FRC).getHeight();
    }

    /**
     * @return the default color
     */
    public Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * Sets the default color for a label.
     * @param defaultColor the color
     */
    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape s) {
        String label = getLabelAt(row);
        if (label == null) {
            // Nothing to show
            return;
        }
        Rectangle2D bounds = s.getBounds2D();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        if (width < 3 || height < 3) {
            // too small, don't even try
            return;
        }
        graphics.setFont(getFont());
        graphics.setColor(getDefaultColor());
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(label, graphics);
        graphics.drawString(
                label,
                (float)(bounds.getCenterX()-labelBounds.getCenterX()),
                (float)(bounds.getCenterY()-labelBounds.getCenterY()));
    }
}
