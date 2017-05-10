/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import java.util.HashMap;

/**
 * <b>UniqColumn</b> is an object column that maintains
 * an inverse map from values to rows and requires values
 * to be uniq.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class UniqColumn extends ObjectColumn {
    protected HashMap valueIndex = new HashMap();
    
    /**
     * Creates a UniqColumn with the specified name and allocated size.
     * @param name the name
     * @param reserve the allocated size
     */
    public UniqColumn(String name, int reserve) {
        super(name, reserve);
    }
    
    /**
     * Creates a UniqColumn with the specified name.
     * @param name the name
     */
    public UniqColumn(String name) {
        super(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setObjectAt(int index, Object element) {
        Object o = getObjectAt(index);
        if (o == element || o != null && o.equals(element)) {
            return;
        }
        assert(o==null || ((Integer)valueIndex.get(o)).intValue()==index);
        if (valueIndex.containsKey(element)) {
            //FIXME add a more specific exception
            throw new RuntimeException("Value already defined");
        }
        valueIndex.remove(o);
        valueIndex.put(element, new Integer(index));
        super.setObjectAt(index, element);
    }
    
    /**
     * {@inheritDoc}
     */
    public int indexOf(Object o) {
        Object index = valueIndex.get(o);
        if (index == null) return -1;
        return ((Integer)index).intValue();
    }
}
