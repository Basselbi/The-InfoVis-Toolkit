/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * <b>CollectionFormat</b> is a format for ObjectColumn containing
 * collections. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class CollectionFormat extends Format {
    protected Class collectionClass;
    /**
     * Creates a collection format for ArrayList collections.
     */
    public CollectionFormat() {
        this(ArrayList.class);
    }
    
    /**
     * Creates a collection format for a specified class of collection.
     * @param collectionClass the collection class
     */
    public CollectionFormat(Class collectionClass) {
        assert(Collection.class.isAssignableFrom(collectionClass));
        this.collectionClass = collectionClass;
    }
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        boolean first = true;
        if (obj instanceof Collection) {
            Collection coll = (Collection) obj;
            for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
                Object type = (Object) iterator.next();
                if (type == null) continue;
                if (first) {
                    first = false;
                }
                else {
                    toAppendTo.append(", ");
                }
                String str = type.toString();
                str = str.replace(",", "\\,");
                toAppendTo.append(str);
            }
        }
        pos.setBeginIndex(toAppendTo.length());
        
        return toAppendTo;
    }
    
    private static String cleanup(String str) {
        return str.replace("\\,", ",");
    }
    
    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        int index = pos.getIndex();
        if (index != 0) {
            source = source.substring(index);
        }
        Collection coll;
        try {
            coll = (Collection)collectionClass.newInstance();
        }
        catch(Exception e) {
            return null;
        }
        int start = 0;
        int next = source.indexOf(','); 
        while (next != -1) {
            if (next != 0 && source.charAt(next-1) == '\\') {
                next = source.indexOf(',', next+1);
                continue;
            }
            coll.add(cleanup(source.substring(start, next-1)));
            start = next+1;
            next = source.indexOf(',', start);
        }
        coll.add(cleanup(source.substring(start)));
        return coll;
    }
}
