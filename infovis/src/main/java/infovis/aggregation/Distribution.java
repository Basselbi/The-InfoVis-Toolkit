/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.aggregation;

import hep.aida.IAxis;
import hep.aida.ref.FixedAxis;
import infovis.column.NumberColumn;
import infovis.utils.RowIterator;

import java.util.Arrays;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cern.jet.stat.Descriptive;

/**
 * Class Distribution
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class Distribution extends Number {
    protected IAxis axis;
    protected int bins[];
    protected double sum;
    protected int count;
    
    public Distribution(IAxis axis) {
        this.axis = axis;
        bins = new int[axis.bins()];
    }
    
    /**
     * Computes the distribution axis from the min, max, standard deviation and count. 
     * @param min the min value
     * @param max the max value
     * @param sdev the standard deviation
     * @param count the count
     * @return the axis
     */
    public static IAxis computeAxis(double min, double max, double sdev, int count) {
        // see: 
        // D. Scott. On optimal and data-based histograms. Biometrika, 66:605–610, 1979.
        double binWidth = 3.49 * sdev * Math.pow(count, -1.0/3.0); 
        return new FixedAxis((int)Math.ceil((max-min)/binWidth), min, max);
    }

    /**
     * Returns the Axis associated with the distribution of this column.
     * 
     * @param c the column
     * @return the Axis or null if the column is empty
     */
    public static IAxis computeAxis(NumberColumn c) {
        IAxis axis = (IAxis)c.getMetadata().getAttribute(Distribution.class.getName());
        if (axis == null) {
            int size = 0;
            double sum = 0;
            double sumOfSquares = 0;
            double min = 0;
            double max = 0;
            for (RowIterator iter = c.iterator(); iter.hasNext(); ) {
                double v = c.getDoubleAt(iter.nextRow());
                sum += v;
                sumOfSquares += v*v;
                if (size == 0) {
                    min = v;
                    max = v;
                }
                else if (v < min) {
                    min = v;
                }
                else if (v > max) {
                    max = v;
                }
                size++;
            }
            if (size == 0) return null; // no data
            double variance = Descriptive.sampleVariance(size, sum, sumOfSquares);
            axis = computeAxis(min, max, Math.sqrt(variance), size);
            c.getMetadata().addAttribute(Distribution.class.getName(), axis);
            c.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    NumberColumn c = (NumberColumn)e.getSource();
                    c.getMetadata().removeAttribute(Distribution.class.getName());
                    c.removeChangeListener(this);
                }
            });
        }
        return axis;
    }
    
    public static boolean checkCompatibility(IAxis a1, IAxis a2) {
        if (a1 != a2) return false;
        if (a1 == null || a2 == null) return false;
        if (a1.bins() != a2.bins()) return false;
        
        for ( int i = 0; i < a1.bins(); i ++ ) {
            if ( a1.binUpperEdge(i) != a2.binUpperEdge(i)
                    ||  a1.binLowerEdge(i) != a2.binLowerEdge(i) ) {
                return false;
            }
        }
        return true;
    }
    
    public int[] getBins() {
        return bins;
    }
    
    public int getBin(int index) {
        return bins[index];
    }
    
    public int binIndexOf(double x) {
        return axis.coordToIndex(x);
    }
    
    public void fill(double x) {
        if (Double.isNaN(x)) {
            return;
        }
        int coordToIndex = binIndexOf(x);
        if (coordToIndex < 0 || coordToIndex >= axis.bins()) {
            return; // ignore out of bounds values
        }
        count++;
        bins[coordToIndex]++;
        sum += x;
    }
    
    public void reset() {
        Arrays.fill(bins, 0);
        sum = 0;
        count= 0;
    }
    
    public double getMean() {
        if (count != 0) return sum / count;
        return 0;
    }
    
    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
    
    /**
     * @return the sum
     */
    public double getSum() {
        return sum;
    }
    
    /**
     * @return the axis
     */
    public IAxis getAxis() {
        return axis;
    }
    
    public void add(Distribution d) {
        if (! checkCompatibility(getAxis(), d.getAxis())) {
            throw new IllegalArgumentException("Incompatible axes");
        }
        int[] dbins = d.getBins();
        for (int i = 0; i < bins.length; i++) {
            bins[i] += dbins[i];
        }
        sum += d.getSum();
        count += d.getCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public double doubleValue() {
        return getMean();
    }
    
    /**
     * {@inheritDoc}
     */
    public float floatValue() {
        return (float)doubleValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public int intValue() {
        return (int)doubleValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public long longValue() {
        return (long)doubleValue();
    }
}
