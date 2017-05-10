/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import hep.aida.IAxis;

/**
 * Class Distribution
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DoubleDistribution extends DefaultDistribution {
    protected IAxis axis;

    protected DoubleDistribution(int n) {
        super(n);
    }
    
    public DoubleDistribution(IAxis axis) {
        super(axis.bins());
        this.axis = axis;
    }
    
    public DoubleDistribution(DoubleDistribution d) {
        super(0);
        height = (double[])d.height;
        axis = d.axis;
    }

    /**
     * {@inheritDoc}
     */
    protected Object clone() {
        return new DoubleDistribution(this);
    }
    
    public boolean isCompatible(Distribution a) {
        if (a.size() != size()) return false;
        if (a instanceof DoubleDistribution) {
            DoubleDistribution dd = (DoubleDistribution) a;
            return dd.getAxis().equals(getAxis());
        }
        return true;
    }
    
    
    public IAxis getAxis() {
        return axis;
    }
    
   public void addDouble(double v, double weight) {
        int cat = axis.coordToIndex(v)-1; // 0 is reserved for underflow
        add(cat, weight);
    }
    
    public void add(Distribution b, double weight) {
        if (! isCompatible(b)) {
            throw new java.lang.ArithmeticException("Incompatible distributions in arithmetic");
        }
        super.add(b, weight);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCategoryName(int cat) {
        double min = axis.binLowerEdge(cat);
        double max = axis.binUpperEdge(cat);
        return "{"+min+","+max+"}";
    }
    
}
