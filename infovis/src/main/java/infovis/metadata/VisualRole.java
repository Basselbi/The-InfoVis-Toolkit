/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.metadata;

import infovis.Column;
import infovis.Metadata;
import infovis.Table;
import infovis.column.StringColumn;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;



/**
 * <b>VisualRole</b> defines constants used to assign one or several
 * visual roles to columns and methods to get and set them in the metadata.
 * 
 * <p>
 * Visual roles can be "Label" for a column aimed at labeling a data set. It can
 * be "Axis" for an axis of a scatterplot; the axis number is then available as
 * the value associated with the key.
 * 
 * <p>
 * Other axes can be useful for parallel coordinates or time series.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class VisualRole extends AbstractMetadata {
    /** Prefix for metadata names of visual roles. */
    public static final String VISUAL_ROLE_PREFIX       = "VisualRole:";

    public static final String VISUAL_ROLE_LABEL        = "label";
    public static final String VISUAL_ROLE_SHORT_LABEL  = "shortlabel";
    public static final String VISUAL_ROLE_AXIS         = "axis";
    public static final String VISUAL_ROLE_COLOR        = "color";
    public static final String VISUAL_ROLE_BORDER_COLOR = "bordercolor";
    public static final String VISUAL_ROLE_SIZE         = "size";
    public static final String VISUAL_ROLE_ALPHA        = "alpha";
    public static final String VISUAL_ROLE_AREA         = "area";
    public static final String VISUAL_ROLE_SHAPE        = "shape";

    public static String keyFor(String valueRole) {
        return VISUAL_ROLE_PREFIX + valueRole;
    }
    
    public static boolean hasVisualRole(Metadata thing, String visualRole) {
        return hasKey(thing, keyFor(visualRole));
    }
    
    public static void setVisualRole(Metadata thing, String visualRole, boolean v) {
        setValue(thing, keyFor(visualRole), v);
    }
    
    public static void setVisualRole(Metadata thing, String visualRole) {
        setVisualRole(thing, visualRole, true);
    }
    
    public static void setVisualRole(Metadata thing, String visualRole, Object v) {
        setValue(thing, keyFor(visualRole), v);
    }
        
    public static void setLabel(Metadata thing) {
        setVisualRole(thing, VISUAL_ROLE_LABEL, true);
    }
    
    public static Column getColumn(Table table, String visualRole) {
        String key = keyFor(visualRole);
        int n = table.getColumnCount();
        for (int i = 0; i < n; i++) {
            Column col = table.getColumnAt(i);
            if (! col.isInternal() 
                    && hasKey(col, key)) {
                return col;
            }
        }
        return null;
    }
    
    /** Column names that can become label names by default. */
    public static final String[] DEFAULT_LABEL_COLUMN_NAMES = { 
        "Name", 
        "Title", 
        "Label" };

    /**
     * Returns a column from a table that will be a default label or
     * null.
     * @param table the table
     * @return a column from a table that will be a default label or
     * null
     */
    public static Column findDefaultLabelColumn(Table table) {
        for (int i = 0; i < DEFAULT_LABEL_COLUMN_NAMES.length; i++) {
            String name = DEFAULT_LABEL_COLUMN_NAMES[i];
            Column c = table.getColumn(name);
            if (c != null) {
                return c;
            }
            name = name.toUpperCase();
            c = table.getColumn(name);
            if (c != null) {
                return c;
            }
            name = name.toLowerCase();
            c = table.getColumn(name);
            if (c != null) {
                return c;
            }
        }
//        for (int i = 0; i < table.getColumnCount(); i++) {
//            Column c = table.getColumnAt(i);
//            if (!c.isInternal() && c instanceof StringColumn) {
//                return c;
//            }
//        }
        return null;
    }

    
    public static Column getLabelColumn(Table table) {
        Column col = getColumn(table, VISUAL_ROLE_LABEL);
        if (col == null) {
            col = findDefaultLabelColumn(table);
        }
        return col;
    }
    
    public String[] getVisualRoles(Metadata thing) {
        ArrayList al = null;
        for (Enumeration e = thing.getMetadata().getAttributeNames(); e.hasMoreElements(); ) {
            String k = (String)e.nextElement();
            if (k.startsWith(VISUAL_ROLE_PREFIX)) {
                if (al == null) {
                    al = new ArrayList();
                }
                al.add(k.substring(VISUAL_ROLE_PREFIX.length()));
            }
        }
        if (al == null)
            return null;
        String[] a = new String[al.size()];
        al.toArray(a);
        return a;
    }
}
