/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.IntArrayList;

/**
 * Row iterator over an array of ints.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */

public class IntArrayIterator extends AbstractRowIterator {
    protected int index;
    protected int last;
    protected int[] children;

    /**
     * Creates an iterator over the contents of an int array.
     *
     * @param array the int array
     */
    public IntArrayIterator(IntArrayList array) {
        this(0, array.size(), array.elements());
    }
    /**
     * Constructor.
     * @param first the initial index (inclusive)
     * @param last the final index (exclusive)
     * @param children the array
     */
    public IntArrayIterator(int first, int last, int[] children) {
        this.index = first;
        this.last = last;
        this.children = children;
    }

    /**
     * Constructor.
     * @param index the initial index
     * @param children the array
     */
    public IntArrayIterator(int index, int[] children) {
        this(index, children.length, children);
    }

    /**
     * Constructor.
     * @param children the array
     */
    public IntArrayIterator(int[] children) {
        this(0, children);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return index < last;
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        throw new RuntimeException("Cannot remove tree node");
    }

    /**
     * {@inheritDoc}
     */
    public int nextRow() {
        if (index >= last)
            return -1;
        return children[index++];
    }

    /**
     * {@inheritDoc}
     */
    public int peekRow() {
        if (index >= last)
            return -1;
        return children[index];
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator copy() {
        return new IntArrayIterator(index, last, children);
    }

}