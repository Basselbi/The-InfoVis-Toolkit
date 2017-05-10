/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;


/**
 * <b>DefaultDistribution</b> implements a default distribution.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DefaultDistribution implements Distribution {
    protected double[] height;
    
    /**
     * Constructor with a specified number of categories.
     * @param ncat the number of categories
     */
    public DefaultDistribution(int ncat) {
        height = new double[ncat];
    }
    
    /**
     * Constructor with a specified table of categories
     * @param height the height table 
     */
    public DefaultDistribution(double[] height) {
        this.height = height;
    }

    
    /**
     * {@inheritDoc}
     */
    public void add(int cat, double weight) {
        height[cat] += weight;
    }
    
    /**
     * Adds a value of a specified category with default height of 1.
     * @param cat the category.
     */
    public void add(int cat) {
        add(cat, 1);
    }

    /**
     * {@inheritDoc}
     */
    public void add(Distribution i, double weight) {
        assert(i.size() == size());
        if (weight == 0) return;
        for (int cat = 0; cat < height.length; cat++) {
            height[cat] += i.getHeight(cat) * weight;
        }
    }
    
    /**
     * Adds a specified distribution with a weight of 1.
     * @param d the distribution
     */
    public void add(Distribution d) {
        add(d, 1);
    }

    /**
     * Adds a specific distribution.
     * @param i the table
     * @param weight the weight
     */
    public void add(double[] i, double weight) {
        assert(i.length == size());
        if (weight == 0) return;
        for (int cat = 0; cat < height.length; cat++) {
            height[cat] += i[cat] * weight;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(int cat) {
        return cat < height.length && height[cat] != 0;
    }

    /**
     * {@inheritDoc}
     */
    public double getHeight(int cat) {
        if (cat < height.length) {
            return height[cat];
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        for (int cat = 0; cat < height.length; cat++) {
            if (height[cat] != 0) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return height.length;
    }
    
    /**
     * Returns the category name of a specified category.
     * @param cat the category
     * @return the name
     */
    public String getCategoryName(int cat) {
        return ""+cat;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof DefaultDistribution) {
            DefaultDistribution other = (DefaultDistribution) obj;
            if (other.height.length != height.length) return false;
            for (int i = height.length; --i >= 0; ) {
                if (height[i] != other.height[i]) return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < height.length; i++) {
            if (height[i] != 0) {
                if (sb.length() != 1) {
                    sb.append(",");
                }
                sb.append(getCategoryName(i));
                sb.append(":");
                sb.append(height[i]);
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
