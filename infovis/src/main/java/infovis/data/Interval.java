/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

/**
 * Class Interval
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public interface Interval {
    double getMin();
    double getMax();
    boolean isEmpty();
    boolean isNegative();
    boolean isPositive();
    void set(double min, double max);
    void set(Interval other);
    void intersect(Interval i);
    void union(Interval i);
    boolean contains(double v);
    boolean contains(Interval i);
    boolean intersects(Interval i);
    void add(double v);
    void add(Interval i);
    void neg();
    double center();
    double length();
    void abs();
    double distance(Interval other);
    double distance(double v);
}
