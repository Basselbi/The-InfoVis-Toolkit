/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import infovis.metadata.IO;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;
import cern.colt.list.IntArrayList;

/**
 * <b>LazyIntColumn</b> is an IntColumn that can be invalidated and
 * recompute its values in a lazy fashion.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class LazyIntColumn extends IntColumn {
    protected boolean invalid;

    /**
     * Constructor.
     * 
     * @param name column name
     * @param reserve the reserved size
     *            
     */
    public LazyIntColumn(String name, int reserve) {
        super(name, reserve);
        getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
        invalid = true;
    }
    
    /**
     * Constructor.
     * 
     * @param name column name
     *            
     */
    public LazyIntColumn(String name) {
        this(name, 10);
    }

    /**
     * Method called when the tree is changed.
     * 
     */
    protected abstract void update();

    /**
     * {@inheritDoc}
     */
    public int get(int index) {
        validate();
        return super.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        validate();
        return super.size();
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
     * Validate the contents of this column if needed.
     * 
     */
    public void validate() {
        if (invalid) {
            invalid = false;
            update();
        }
    }

    /**
     * Sets the column to invalid.
     * 
     */
    public void invalidate() {
        invalid = true;
    }

    /**
     * @return Returns the invalid.
     */
    public boolean isInvalid() {
        return invalid;
    }
}
