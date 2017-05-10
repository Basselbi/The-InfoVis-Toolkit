/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import cern.colt.list.ByteArrayList;

/**
 * Class ByteFloatColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ByteFloatColumn extends AbstractFloatColumn {
    private static final long serialVersionUID = -3398555390778655726L;
    protected ByteArrayList value;
    
    /**
     * Creates a new ByteFloatColumn object.
     * 
     * @param name
     *            the column name.
     */
    public ByteFloatColumn(String name) {
        this(name, 10);
    }
    
    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public ByteFloatColumn(String name, int reserve) {
        super(name);
        value = new ByteArrayList(reserve);
    }
    
    /**
     * Converts a fixed point byte into a float between -1 and 1
     * @param b the byte
     * @return the float
     */
    public static float byte2float(byte b) {
        return b / 127.0f;
    }
    
    /**
     * Converts a float value between -1 and 1 to a byte.
     * @param f the float value
     * @return the byte value
     */
    public static byte float2byte(float f) {
        assert(-1 <= f && f <= 1);
        return (byte)(f * 127);
    }

    /**
     * {@inheritDoc}
     */
    public float get(int index) {
        assert ((index >= 0) && (index < size()));
        return byte2float(value.getQuick(index));
    }

    /**
     * {@inheritDoc}
     */
    public void set(int index, float element) {
        assert ((index >= 0) && (index < size()));
        value.setQuick(index, float2byte(element));
        set(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setExtend(int index, float element) {
        assert (index >= 0);
        if (index >= size()) {
            if (index == size()) {
                value.setSize(index+1);
            }
            else {
                setSize(index + 1);
            }
        }
        set(index, element);
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return value.elements().length;
    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
        value.ensureCapacity(minCapacity);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return value.size();
    }



    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        try {
            disableNotify();
            super.setSize(newSize);
            value.setSize(newSize);
        }
        finally {
            enableNotify();
        }
    }
}
