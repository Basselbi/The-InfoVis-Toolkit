/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.io.ObjectStreamException;

/**
 * Column returning ONE for all its rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ColumnOne extends ConstantColumn {
    /**
     * 
     */
    private static final long serialVersionUID = -8907259254614059838L;
    /**
     * An instance of this column.
     */
    public static final ColumnOne INSTANCE = new ColumnOne();

    /**
     * Creates a column of 1 with the specified size.
     * @param name the name
     * @param size the size
     */
    public ColumnOne(String name, int size) {
        super(name, size);
    }
    /**
     * Creates a column of 1 with the specified size.
     * @param size the size
     */
    public ColumnOne(int size) {
        this("#one", size);
    }
    
    /**
     * Creates a column of 1 with a default size of 1.
     */
    public ColumnOne() {
        this(1);
    }
    
    /**
     * Returns the constant 1 for each row.
     * @param row the row
     * @return the constant 1 for each row
     */
    public double getDoubleAt(int row) {
        return 1;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }    

}
