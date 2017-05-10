/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.visualization;

import infovis.Column;
import infovis.Visualization;
import infovis.column.HistogramColumn;
import infovis.column.NumberColumn;
import infovis.table.DefaultTable;
import infovis.visualization.Orientation;
import infovis.visualization.render.DefaultVisualLabel;
import infovis.visualization.render.VisualLabel;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * Visualization of histogram.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * TODO add a filtered histogram on top.
 */
public class HistogramVisualization extends ColumnVisualization {
    protected HistogramColumn histogram;
    protected double binSize = 10;
    /** Property binSize. */
    public static final String PROPERTY_BIN_SIZE = "binSize";
    
    /**
     * Constructor.
     * @param column the column to build a histogram.
     * @param bins number of bins.
     */
    public HistogramVisualization(NumberColumn column, int bins) {
        super(new DefaultTable(), new HistogramColumn(column, bins));
        setVisualColumn(VISUAL_COLOR, null);
        histogram = (HistogramColumn)super.getColumn();
        VisualLabel vl = VisualLabel.get(this);
        vl.setColumn(histogram);
        if (vl instanceof DefaultVisualLabel) {
            DefaultVisualLabel dvl = (DefaultVisualLabel) vl;
            dvl.setOrientation(ORIENTATION_SOUTH);
            dvl.setOutlined(true);
        }
    }
    
    /**
     * Constructor.
     * @param column the column to build a histogram.
     */
    public HistogramVisualization(NumberColumn column) {
        this(column, 50);
    }

    /**
     * @return Returns the histogram.
     */
    public HistogramColumn getHistogram() {
        return histogram;
    }
    
    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return histogram.getColumn();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        histogram.setColumn((NumberColumn)column);
    }
    
    /**
     * Change the number of bins of the histogram.
     * @param bins the new bin number
     */
    public void setBins(int bins) {
        histogram.setSize(bins);
    }
    
    /**
     * Returns the number of bins of this histogram.
     * @return the number of bins of this histogram.
     */
    public int getBins() {
        return histogram.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        int maxBins = Integer.MAX_VALUE;
        NumberColumn num = histogram.getColumn();
        if (num.coerce(0.5)==0) { // integer column
            int minIndex = num.getMinIndex();
            if (minIndex == -1) {
                setBins(0);
                return;
            }
            long min = num.getLongAt(minIndex);
            long max = num.getLongAt(num.getMaxIndex());
            long d = max - min + 1;
            if (d < Integer.MAX_VALUE) {
                maxBins = (int)d;
            }
        }
        if (Orientation.isHorizontal(getOrientation())) {
            setBins(Math.min(maxBins, (int)(bounds.getWidth()/binSize)));
        }
        else {
            setBins(Math.min(maxBins, (int)(bounds.getHeight()/binSize)));
        }
        super.computeShapes(bounds, vis);
    }
    
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (Orientation.isHorizontal(getOrientation())) {
            return new Dimension((int)(binSize * histogram.size()), 50);
        }
        else {
            return new Dimension(50, (int)(binSize * histogram.size()));
        }
    }
    
    /**
     * @return the binSize
     */
    public double getBinSize() {
        return binSize;
    }
    
    /**
     * @param binSize the binSize to set
     */
    public void setBinSize(double binSize) {
        if (this.binSize == binSize) return;
        if (binSize <= 0) {
            throw new RuntimeException("Invalid bin size, should be positive.");
        }
        double old = this.binSize;
        this.binSize = binSize;
        firePropertyChange(PROPERTY_BIN_SIZE, old, binSize);
    }

}
