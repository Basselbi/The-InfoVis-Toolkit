/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

/**
 * An <b>IntSet</b> is a set of positive integers. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface IntSet extends Cloneable {
    /**
     * Sets the specified value to <code>true</code>.
     *
     * @param     index   the value
     */
    public void set(int index);
    /**
     * Sets the ints from the specified <tt>fromIndex</tt> (inclusive) to the
     * specified <tt>toIndex</tt> (exclusive).
     *
     * @param     fromIndex   index of the first int to be set.
     * @param     toIndex index after the last int to be set
     */
    public void set(int fromIndex, int toIndex);
    /**
     * Sets the ints from the specified <tt>fromIndex</tt> (inclusive) to the
     * specified <tt>toIndex</tt> (exclusive) to the specified value.
     *
     * @param     fromIndex   index of the first int to be set.
     * @param     toIndex index after the last int to be set
     * @param     value value to set the selected ints to
     */
    public void set(int fromIndex, int toIndex, boolean value);
    /**
     * Sets or unset the specified int depending on value.
     *
     * @param     index   the index
     * @param     value   the true or false
     */
    public void set(int index, boolean value);
    /**
     * Returns the value of the int with the specified value. The value
     * is <code>true</code> if the int <code>index</code>
     * is currently in this set; otherwise, the result
     * is <code>false</code>.
     * @param index the index
     * @return <code>true</code> if the int <code>index</code>
     * is currently in this set; otherwise, the result
     * is <code>false</code>.
     */
    public boolean get(int index);
    /**
     * Adds the specified int if it is not in the set and
     * removes it if it is.
     * @param index the int
     */
    public void flip(int index);
    public void flip(int fromIndex, int toIndex);
    public void clear();
    public int cardinality();
    public int length();
    public boolean isEmpty();
    public int nextClear(int fromIndex);
    public int nextSet(int fromIndex);
    public void or(IntSet set);
    public void and(IntSet set);
    public Object clone();
    
    public RowIterator iterator();
}
