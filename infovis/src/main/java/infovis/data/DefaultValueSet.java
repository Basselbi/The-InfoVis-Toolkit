/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import infovis.utils.RowIterator;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * DefaultValueSet
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultValueSet implements ValueSet {
    protected TreeSet<Object> set = new TreeSet<Object>();
    
    public DefaultValueSet() {
    }
    
    public DefaultValueSet(ValueSet s) {
        add(s);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(Object v) {
        set.add(v);
    }

    /**
     * {@inheritDoc}
     */
    public void add(ValueSet s) {
        for (Iterator<Object> iter = s.iterator(); iter.hasNext(); ) {
            Object v = iter.next();
            add(v);
        }        
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object v) {
        return set.contains(v);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Object> iterator() {
        return set.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return set.size();
    }
    
    
}
