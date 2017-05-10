/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import infovis.utils.RowIterator;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * <b>DoubleInterval</b> class is the [min,max] interval.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DoubleInterval extends Number implements Interval, Cloneable, Serializable {
    protected double min;
    protected double max;
    
    public static final DoubleInterval ZERO = new DoubleInterval(0, 0);
    
    /**
     * Creates an interval with the specified min and max values.
     * @param min the min value
     * @param max the max value
     */
    public DoubleInterval(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates the interval -Double.MAX_VALUE, Double.MAX_VALUE.
     */
    public DoubleInterval() {
        this(-Double.MAX_VALUE, Double.MAX_VALUE);
    }
    
    /**
     * Copies the specified interval.
     * @param other the interval
     */
    public DoubleInterval(Interval other) {
        this(other.getMin(), other.getMax());
    }
    
    /**
     * @return the minimum of the interval
     */
    public double getMin() {
        return min;
    }

    /**
     * @return the maximum of the interval
     */
    public double getMax() {
        return max;
    }
    
    public void set(double min, double max) {
        this.min = min;
        this.max = max;
    }
    
    public void set(Interval other) {
        min = other.getMin();
        max = other.getMin();
    }
    
    
    /**
     * @return true if the interval is empty (min &gt; max)
     */
    public boolean isEmpty() {
        return min > max;
    }
    
    /**
     * @return true if the interval is is pure number (min == max)
     */
    public boolean isNumber() {
        return min == max;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isCategorical() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator categories() {
        return null;
    }
    
    public boolean isNegative() {
        return max < 0;
    }
    
    public boolean isPositive() {
        return min > 0;
    }
    
    public void intersect(Interval other) {
        min = Math.max(getMin(), other.getMin());
        max = Math.min(getMax(), other.getMax());
    }
    
    public void union(Interval other) {
        min = Math.min(getMin(), other.getMin());
        max = Math.max(getMax(), other.getMax());
    }
    
    public boolean contains(double v) {
        return min <= v && v <= max; 
    }
    
    public boolean contains(Interval i) {
        return min <= i.getMin() && max >= i.getMax();
    }
    
    public boolean intersects(Interval i) {
        return ! (max < i.getMin() || min > i.getMax());
    }
    
    public void add(double v) {
        min = Math.min(min, v);
        max = Math.max(max, v);
    }
    
    public void add(Interval other) {
        min += other.getMin();
        max += other.getMax();
    }
    
    /**
     * {@inheritDoc}
     */
    public double doubleValue() {
        return center();
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
    
    
    /**
     * {@inheritDoc}
     */
    public void aggregate(SymbolicValue s) {
        if (s instanceof Interval) {
            Interval i = (Interval) s;
            add(i);
        }
        if (s instanceof Number) {
            Number n = (Number) s;
            add(n.doubleValue());
        }
        else {
            throw new UnsupportedOperationException("Cannot aggregate with class "+s.getClass().getName());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof Interval) {
            Interval i = (Interval) obj;
            return i.getMin() == getMin() 
                && i.getMax() == getMax();
            
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (int)(Double.doubleToRawLongBits(min) * Double.doubleToRawLongBits(max)); 
    }
    
    public void neg() {
        double tmp = min;
        min = -max;
        max = -tmp;
    }
    
    public static Interval neg(Interval i) {
        if (i.isPositive()) return i;
        DoubleInterval o = new DoubleInterval(i);
        o.neg();
        return o;
    }
    
    public double center() {
        if (isEmpty()) {
            return Double.NaN;
        }
        return (max + min)/2;
    }

    public double length() {
        return max - min;
    }

    public void abs() {
        if (min >= 0) {
            return;
        }
        else if (max < 0) {
            neg();
        }
        else {
            double tmp = Math.max(-min, max);
            min = 0;
            max = tmp;
        }
    }
    
    public static Interval abs(Interval i) {
        if (i.isPositive()) return i;
        DoubleInterval ret = new DoubleInterval(i);
        ret.abs();
        return ret;
    }

    public double distance(Interval other) {
        return Math.max(
                Math.abs(min - other.getMin()), 
                Math.abs(max - other.getMax()));
    }
    
    public double distance(double d) {
        return Math.max(Math.abs(d-min), Math.abs(d-max));
    }
    
    public static Interval add(Interval a, Interval b) {
        return new DoubleInterval(a.getMin() + b.getMin(), a.getMax() + b.getMax());
    }
    
    public static Interval sub(Interval a, Interval b) {
        return new DoubleInterval(a.getMin()-b.getMax(), a.getMax()-b.getMin());
    }
    
    public static Interval mul(Interval a, Interval b) {
        double x = a.getMin()*b.getMin();
        double y = a.getMax()*b.getMax();
        double z = a.getMax()*b.getMin();
        double p = a.getMax()*b.getMax();
        
        double min = x;
        if (y < min) min = y;
        if (z < min) min = z;
        if (p < min) min = p;
        
        double max = x;
        if (y > max) max = y;
        if (z > max) max = z;
        if (p > max) max = p;
        
        return new DoubleInterval(min, max);
    }
    
    public static DoubleInterval div(DoubleInterval a, DoubleInterval b) {
        if (b.getMin() <= 0 && b.getMax() >= 0) {
            throw new java.lang.ArithmeticException("Division by an interval containing 0");
        }
        double x = a.getMin()/b.getMin();
        double y = a.getMax()/b.getMax();
        double z = a.getMax()/b.getMin();
        double p = a.getMax()/b.getMax();
        
        double min = x;
        if (y < min) min = y;
        if (z < min) min = z;
        if (p < min) min = p;
        
        double max = x;
        if (y > max) max = y;
        if (z > max) max = z;
        if (p > max) max = p;
        
        return new DoubleInterval(min, max);
    }
    
    public Boolean greater(DoubleInterval a, double b) {
        if (a.getMin() > b) return Logical.TRUE;
        if (a.getMax() <= b) return Logical.FALSE;
        return Logical.UNCERTAIN;
    }

    public Boolean greater(DoubleInterval a, DoubleInterval b) {
        if (a.getMin() > b.getMax()) return Logical.TRUE;
        if (a.getMax() <= b.getMin()) return Logical.FALSE;
        return Logical.UNCERTAIN;
    }

    public Boolean greaterOrEqual(DoubleInterval a, DoubleInterval b) {
        if (a.getMin() >= b.getMax()) return Logical.TRUE;
        if (a.getMax() < b.getMin()) return Logical.FALSE;
        return Logical.UNCERTAIN;
    }
    
    public Boolean lower(DoubleInterval a, DoubleInterval b) {
        return greater(b, a);
    }
    
    public Boolean lowerOrEqual(DoubleInterval a, DoubleInterval b) {
        return greaterOrEqual(b, a);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "["+min+"@"+max+"]";
    }
    
    public static DoubleInterval valueOf(String value) {
        StringTokenizer tok = new StringTokenizer(value,"[]@");
        double min = Double.parseDouble(tok.nextToken());
        double max = Double.parseDouble(tok.nextToken());
        return new DoubleInterval(min, max);
    }
    
}
