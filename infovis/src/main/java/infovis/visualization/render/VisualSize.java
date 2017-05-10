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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Float;

/**
 * Class VisualSize
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.19 $
 */
public class VisualSize extends AbstractVisualColumn {
    /** Name of this visual. */
    public static final String VISUAL = Visualization.VISUAL_SIZE;
    /** Default minimum size. */
    public static double DEFAULT_MIN_SIZE = 1;
    /** Default maximum size. */
    public static double DEFAULT_MAX_SIZE = 50;
    /** Default size. */
    public static double DEFAULT_DEFAULT_SIZE = 5;
    
    protected double minSize;
    protected double maxSize;
    protected double defaultSize;
    protected NumberColumn sizeColumn;
    
    protected boolean rescaling = true;
    protected transient double smin;
    protected transient double smax;
    protected transient double sscale;
    
    protected transient VisualLabel vl;
    
    /**
     * Extracts the visual size from the specified visualization.
     * @param vis the visualization
     * @return the visual size of null
     */
    public static VisualSize get(Visualization vis) {
        return (VisualSize)findNamed(VISUAL, vis);
    }
    
    /**
     * Creates a prototype VisualSize with a specified child.
     * @param child the child
     */
    public VisualSize(ItemRenderer child) {
        this(child, DEFAULT_DEFAULT_SIZE, DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE);
    }

    /**
     * Creates a prototype VisualSize with a child and default values.
     * @param child the child
     * @param def the default size
     * @param min the minimum size
     * @param max the maximum size
     */
    public VisualSize(ItemRenderer child, double def, double min, double max) {
        super(VISUAL);
        filter = NotNumberFilter.sharedInstance();
        defaultSize = def;
        minSize = min;
        maxSize = max;
        addRenderer(child);
    }
    
    /**
     * Creates a prototype VisualSize with a specified name.
     * @param name the name
     */
    public VisualSize(String name) {
        super(name);
        filter = NotNumberFilter.sharedInstance();
        defaultSize = DEFAULT_DEFAULT_SIZE; 
        minSize = DEFAULT_MIN_SIZE;
        maxSize = DEFAULT_MAX_SIZE;
    }
    
    /**
     * Default constructor.
     */
    public VisualSize() {
        this((ItemRenderer)null);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return sizeColumn;
    }

    /**
     * @return <code>true</code> if the sizes are rescaled
     */
    public boolean isRescaling() {
        return rescaling;
    }
    
    /**
     * Sets if the sizes are rescaled.
     * @param rescaling the boolean value
     */
    public void setRescaling(boolean rescaling) {
        if (this.rescaling == rescaling) return;
        this.rescaling = rescaling;
        invalidate();
    }
    
    /**
     * @return the column used to specify the size
     */
    public NumberColumn getSizeColumn() {
        return sizeColumn;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        if (column == sizeColumn) return;
        super.setColumn(column);
        sizeColumn = (NumberColumn)column;
        if (sizeColumn == null && DEFAULT_DEFAULT_SIZE == 0) {
            vl = VisualLabel.get(visualization);
            if (vl != null) {
                vl.setInvalidate(true);
            }
        }
        else {
            vl = null;
        }
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        super.install(graphics);
        if (sizeColumn != null  && isRescaling()) {
        	int min = sizeColumn.getMinIndex();
        	int max = sizeColumn.getMaxIndex();
        	if (min == -1) {
        		smin = 0;
        		smax = 0;
        		sscale = 0;
        	}
        	else {
	            smin = sizeColumn.getDoubleAt(min);
	            smax = sizeColumn.getDoubleAt(max);
	            if (smin == smax) {
	                sscale = 0;
	            }
	            else {
	                sscale = (maxSize - minSize) / (smax - smin);
	            }
        	}
        }
        else {
    		smin = 0;
    		smax = 0;
            sscale = 1;
        }
    }

    /**
     * Returns the size associated with the specified row.
     *
     * @param row the row.
     *
     * @return the size associated with the specified row.
     */
    public double getSizeAt(int row) {
        if (sizeColumn == null || sscale == 0) {
            return defaultSize;
        }
        if (sizeColumn.isValueUndefined(row)) {
            return 0;
        }
        return (sizeColumn.getDoubleAt(row) - smin) * sscale + minSize;            
    }
    
    /**
     * Returns the width of the specified item.
     * @param row the item's row
     * @return the width
     */
    public double getWidthAt(int row) {
        if (sizeColumn != null 
                || vl == null 
                || defaultSize != 0) {
            return getSizeAt(row);
        }

        String label = vl.getLabelAt(row);
        if (label == null) {
            return getSizeAt(row);
        }
        return vl.getWidth(label)+2;
    }

    /**
     * Returns the height of the specified item.
     * @param row the item's row
     * @return the height
     */
    public double getHeightAt(int row) {
        if (sizeColumn != null 
                || vl == null 
                || defaultSize != 0) {
            return getSizeAt(row);
        }
        String label = vl.getLabelAt(row);
        if (label == null) {
            return getSizeAt(row)+2;
        }
        return vl.getHeight(label);
    }
    
    /**
     * Computes the size of the specified rectangle.
     * @param row the item's row
     * @param rect the rectangle
     */
    public void setRectSizeAt(int row, Float rect) {
        rect.width = (float)getWidthAt(row);
        rect.height = (float)getHeightAt(row);
    }

    /**
     * Returns the maxSize.
     *
     * @return double
     */
    public double getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the minSize.
     *
     * @return double
     */
    public double getMinSize() {
        return minSize;
    }

    /**
     * Returns the defaultSize.
     *
     * @return double
     */
    public double getDefaultSize() {
        return defaultSize;
    }

    /**
     * Sets the maxSize.
     *
     * @param maxSize The maxSize to set
     */
    public void setMaxSize(double maxSize) {
        this.maxSize = maxSize;
        invalidate();
    }

    /**
     * Sets the minSize.
     *
     * @param minSize The minSize to set
     */
    public void setMinSize(double minSize) {
        this.minSize = minSize;
        invalidate();
    }

    /**
     * Sets the defaultSize.
     *
     * @param defaultSize The defaultSize to set
     */
    public void setDefaultSize(double defaultSize) {
        if (this.defaultSize == defaultSize) return;
        this.defaultSize = defaultSize;
        if (defaultSize == 0 && sizeColumn == null) {
            vl = DefaultVisualLabel.get(getVisualization());
            if (vl != null) {
                vl.setInvalidate(true);
                vl.invalidate();
            }
        }
        else if (vl != null) {
            vl.setInvalidate(false);
            vl = null;
        }
        invalidate();
    }

}
