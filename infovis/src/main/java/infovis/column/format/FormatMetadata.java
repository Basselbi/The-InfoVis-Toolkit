/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.format;

import java.util.Iterator;

/**
 * Class FormatMetadata
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface FormatMetadata {
    /**
     * Add a key/value to this metadata associated to a type
     * @param type the type
     * @param key the key 
     * @param value the value
     */
    void addMetadata(String type, String key, String value);
    /**
     * @return maps of metadata associated with the format metadata
     */
    Iterator formatMetadataIterator();
}
