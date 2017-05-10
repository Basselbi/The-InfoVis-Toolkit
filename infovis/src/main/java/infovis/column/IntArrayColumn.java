/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import infovis.column.format.IntArrayFormat;
import cern.colt.list.IntArrayList;

/**
 * <b>IntArrayColumn</b> implements a column of colt IntArrayList objects.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class IntArrayColumn extends BasicObjectColumn {
    private static final long serialVersionUID = 2202568572374401232L;
    /** Empty IntArrayList. */
    public static final IntArrayList EMPTY = new IntArrayList(0);
    
    /**
     * Creates a columns with the specified name and reserved size.
     * @param name the name
     * @param reserve the reserved size
     */
    public IntArrayColumn(String name, int reserve) {
        super(name, reserve);
        setFormat(IntArrayFormat.INSTANCE);
    }

    /**
     * Creates a columns with the specified name and reserved size.
     * @param name the name
     */
    public IntArrayColumn(String name) {
        this(name, 10);
    }
    
    /**
     * Returns the IntArrayList at the specified index.
     * @param index the index
     * @return the IntArrayList or null
     */
    public IntArrayList get(int index) {
        return (IntArrayList)getObjectAt(index);
    }
    
    /**
     * Sets the element at the specified index, growing the column
     * if required.
     * @param index the index
     * @param element the element
     */
    public void setExtend(int index, IntArrayList element) {
        super.setExtend(index, element);
    }
    
    /**
     * Adds the specified element at the end of the column.
     * @param element the element
     */
    public void add(IntArrayList element) {
        super.add(element);
    }
    
    

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return IntArrayList.class;
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return EMPTY;
    }        

}
