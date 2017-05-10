/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.visualization.ItemRenderer;
import infovis.visualization.color.OrderedColor;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Class VisualAttributeCollector
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualAttributeCollector extends AbstractItemRenderer {
    protected boolean          enabled = false;
    protected NumberColumn     attribute;
    protected VisualColor      visualColor;
    protected int              minIndex;
    protected int              maxIndex;
    protected Rectangle        lens;

    /**
     * Returns the VisualAttributeCollector associated with a specified ItemRenderer.
     * @param ir the ItemRenderer
     * @return the VisualAttributeCollector associated with a specified ItemRenderer
     * or <code>null</code> if it does not exist
     */
    public static VisualAttributeCollector get(ItemRenderer ir) {
        return (VisualAttributeCollector) findWithClass(VisualAttributeCollector.class, ir);
    }

    /**
     * Returns the VisualAttributeCollector associated with a specified ItemRenderer.
     * @param vis the visualization
     * @return the VisualAttributeCollector associated with a specified visualization
     * or <code>null</code> if it does not exist
     */
    public static VisualAttributeCollector get(Visualization vis) {
        return get(vis.getItemRenderer());
    }
    
    /**
     * Constructor with a child.
     * @param child the child
     */
    public VisualAttributeCollector(ItemRenderer child) {
        super(null);
        addRenderer(child);
    }

    /**
     * Constructor with two children.
     * @param c1 child 1
     * @param c2 child 2
     */
    public VisualAttributeCollector(ItemRenderer c1, ItemRenderer c2) {
        this(c1);
        addRenderer(c2);
    }

    /**
     * Constructor with three children
     * @param c1 child 1
     * @param c2 child 2
     * @param c3 child 3
     */
    public VisualAttributeCollector(
            ItemRenderer c1,
            ItemRenderer c2,
            ItemRenderer c3) {
        this(c1);
        addRenderer(c2);
        addRenderer(c3);
    }

    /**
     * Constructor with four children.
     * @param c1 child 1
     * @param c2 child 2
     * @param c3 child 3
     * @param c4 child 4
     */
    public VisualAttributeCollector(
            ItemRenderer c1,
            ItemRenderer c2,
            ItemRenderer c3,
            ItemRenderer c4) {
        this(c1);
        addRenderer(c2);
        addRenderer(c3);
        addRenderer(c4);
    }



    /**
     * @return the lens
     */
    public Rectangle getLens() {
        return lens;
    }
    
    /**
     * @param lens the lens to set
     */
    public void setLens(Rectangle lens) {
        this.lens = lens;
    }
    
    /**
     * @return the visualColor
     */
    public VisualColor getVisualColor() {
        return visualColor;
    }
    
    /**
     * @param visualColor the visualColor to set
     */
    public void setVisualColor(VisualColor visualColor) {
        this.visualColor = visualColor;
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        minIndex = maxIndex = -1;
        if (enabled 
                && visualColor.getColorVisualization() instanceof OrderedColor) {
            attribute = (NumberColumn)visualColor.getColumn();
        }
        else {
            attribute = null;
        }
        super.install(graphics);
    }
    
    /**
     * {@inheritDoc}
     */
    public void uninstall(Graphics2D graphics) {
        super.uninstall(graphics);
        if (enabled
                && visualColor.getColorVisualization() instanceof OrderedColor) {
            OrderedColor oc = (OrderedColor)visualColor.getColorVisualization();
            if (getMinIndex() != -1) {
                double min = attribute.getDoubleAt(getMinIndex());
                double max = attribute.getDoubleAt(getMaxIndex());
                if (min == max) {
                    min -= 0.5;
                    max += 0.5;
                }
                oc.setRange(min, max);
//                System.out.println("min="+min+" max="+max);
            }
            else {
                oc.setRange(attribute.getDoubleMin(), attribute.getDoubleMax());
            }
        }
        attribute = null;
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (enabled 
                && attribute != null 
                && !attribute.isValueUndefined(row)
                && (lens == null || shape.intersects(lens))) {
            if (minIndex == -1) {
                minIndex = row;
            }
            else if (attribute.compare(row, minIndex) < 0) {
                minIndex = row;
            }
            if (maxIndex == -1) {
                maxIndex = row;
            }
            else if (attribute.compare(maxIndex, row) < 0) {
                maxIndex = row;
            }
        }
        super.paint(graphics, row, shape);
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the minValue
     */
    public int getMinIndex() {
        return minIndex;
    }

    /**
     * @return the maxValue
     */
    public int getMaxIndex() {
        return maxIndex;
    }
}
