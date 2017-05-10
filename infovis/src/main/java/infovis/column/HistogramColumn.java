/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import hep.aida.IAxis;
import hep.aida.ref.Histogram1D;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cern.colt.list.IntArrayList;

/**
 * Column computing and maintaining the histogram of a specified
 * {@link NumberColumn}.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 */
public class HistogramColumn extends IntColumn implements ChangeListener {
    private static final long serialVersionUID = -8899880106410570039L;
    protected NumberColumn    column;
    protected Histogram1D     histogram;
    private transient boolean valid;
    

    /**
     * Constructor.
     * 
     * @param column
     *            the column.
     * @param bins
     *            the number of bins.
     */
    public HistogramColumn(NumberColumn column, int bins) {
        super("#BinsFor_" + column.getName(), bins);
        setSize(bins);
        this.column = column;
        column.addChangeListener(this);
    }

    /**
     * Constructor.
     * 
     * @param column
     *            the column.
     */
    public HistogramColumn(NumberColumn column) {
        this(column, 200);
    }

    /**
     * Returns the column from which this histogram is built.
     * 
     * @return the column from which this histogram is built.
     */
    public NumberColumn getColumn() {
        return column;
    }

    /**
     * Sets the column from which the histogram is built.
     * 
     * @param col
     *            the column from which the histogram is built.
     */
    public void setColumn(NumberColumn col) {
        if (column == col) {
            return;
        }
        //super.clear();
        column.removeChangeListener(this);
        column = col;
        column.addChangeListener(this);
        valid = false;
    }

    protected void validate() {
        if (valid) {
            return;
        }
        valid = true;
        try {
            disableNotify();
            if (column.getMinIndex() == -1) {
                for (int i = 0; i < size(); i++) {
                    setValueUndefined(i, true);
                }
                histogram = null;
            }
            else {
                double min = column.getDoubleMin();
                double max = column.getDoubleMax();
                if (min == max) {
                    max = min + 1;
                }
                else {
                    double d = (max - min)/size();
                    max += d/10;
                }
                histogram = new Histogram1D(
                        getName(), 
                        size(),
                        min,
                        max);
                NumberColumn col = column;
                for (RowIterator i = col.iterator(); i.hasNext();) {
                    histogram.fill(col.getDoubleAt(i.nextRow()));
                }

                IAxis axis = histogram.xAxis();
                int prev = 0;
                for (int i = 0; i < size(); i++) {
                    int h = (int) histogram.binHeight(i);
                    if (h == 0 && 
                        column.coerce(axis.binLowerEdge(i))
                        == column.coerce(axis.binLowerEdge(i-1))) { 
                        super.set(i, prev);
                    }
                    else {
                        super.set(i, h);
                        prev = h;
                    }
                }
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return super.size();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        if (size() != newSize) {
            if (newSize < 0) {
                throw new IllegalArgumentException("size should be >=0 instead of "+newSize);
            }
            try {
                disableNotify();
                if (newSize < size()) {
                    modified(newSize,size()-1);                    
                }
                else {
                    modified(size(), newSize-1);
                }
                value.setSize(newSize);
                min_max_updated = false;
                valid = false;
            }
            finally {
                enableNotify();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == column) {
            valid = false;
            histogram = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int get(int index) {
        validate();
        return super.get(index);
    }
    
    /**
     * @return the histogram
     */
    public Histogram1D getHistogram() {
        return histogram;
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        validate();
        return super.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int[] toArray() {
        validate();
        return super.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public int[] toArray(int[] a) {
        validate();
        return super.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    public void sort(RowComparator comp) {
        validate();
        super.sort(comp);
    }

    /**
     * {@inheritDoc}
     */
    public void stableSort(RowComparator comp) {
        validate();
        super.stableSort(comp);
    }

    /**
     * {@inheritDoc}
     */
    public IntArrayList getValueReference() {
        validate();
        return super.getValueReference();
    }

    /**
     * {@inheritDoc}
     */
    public void set(int index, int element) {
        readonly();
    }
    
    /**
     * Releases listeners.
     */
    public void dispose() {
        column.removeChangeListener(this);
        super.clear();
        column = null;
        super.dispose();
    }
}
