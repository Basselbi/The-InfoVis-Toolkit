/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.column.format.ColorFormat;
import infovis.metadata.ValueCategory;
import infovis.metadata.VisualRole;

/**
 * Column containing colors.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * @infovis.factory ColumnFactory "color" DENSE
 */
public class ColorColumn extends IntColumn {
    private static final long serialVersionUID = -6127011888416071260L;

    /**
     * Create a ColorColumn given its name.
     * @param name the name
     */
    public ColorColumn(String name) {
        this(name, 10);

    }

    /**
     * Create a ColorColumn given its name and a reserved size.
     * @param name the name
     * @param reserve the reserved size
     */
    public ColorColumn(String name, int reserve) {
        super(name, reserve);
        VisualRole.setVisualRole(this, VisualRole.VISUAL_ROLE_COLOR);
        setFormat(ColorFormat.getInstance());
    }

}