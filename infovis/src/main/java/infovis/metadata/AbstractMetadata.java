/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.metadata;

import javax.swing.text.MutableAttributeSet;

import infovis.Metadata;

/**
 * Class AbstractMetadata
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public abstract class AbstractMetadata implements Constants {
    public static Object getValue(Metadata thing, String key) {
        MutableAttributeSet md = thing.getMetadata();
        if (md == null) return null;
        return md.getAttribute(key);
    }

    public static boolean hasKey(Metadata thing, String key) {
        return getValue(thing, key) != null;
    }

    public static void setValue(Metadata thing, String key, boolean v) {
        if (v) {
            thing.getMetadata().addAttribute(key, Boolean.TRUE);
        }
        else {
            thing.getMetadata().removeAttribute(key);
        }
    }

    public static void setValue(Metadata thing, String key, Object v) {
        if (v != null) {
            thing.getMetadata().addAttribute(key, v);
        }
        else {
            thing.getMetadata().removeAttribute(key);
        }
    }
}
