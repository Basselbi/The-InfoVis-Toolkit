/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.visualization.ItemRenderer;

/**
 * <b>VisualInvisible</b> is a renderer that filters the
 * items marked as "invisible" in the associated column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualInvisible extends VisualFilter {
    /** The default name of this visual. */
    public static final String VISUAL = "invisible";
    /**
     * Creates a VisualInvisible with the specified child.
     * @param child the child
     */
    public VisualInvisible(ItemRenderer child) {
        super(VISUAL, child);
    }
    
    /**
     * Creates a visual invisible with the specified name and child.
     * @param name the name
     * @param child the child
     */
    public VisualInvisible(String name, ItemRenderer child) {
        super(name, child);
    }
    

    
    protected Column getDefault() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        if (filter == null) return false;
        if (! filter.isFiltered(row)) {
            return true;
        }
        return false;
    }
}
