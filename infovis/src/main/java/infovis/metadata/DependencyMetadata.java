/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.metadata;

import java.util.ArrayList;
import java.util.Iterator;

import infovis.Column;
import infovis.utils.NullRowIterator;

/**
 * Class DependencyMetadata
 * 
 * Manage dependent columns
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class DependencyMetadata extends AbstractMetadata
    implements Constants {
    public static final String DEPENDENT_COLUMNS = "DEPENDENT_COLUMNS";
    
    public static ArrayList findDependentColumns(Column col) {
        ArrayList dep = (ArrayList)getValue(col, DEPENDENT_COLUMNS);
        if (dep == null) {
            dep = new ArrayList();
            setValue(col, DEPENDENT_COLUMNS, dep);
        }
        return dep;
    }
    
    public static Iterator dependentIterator(Column col) {
        ArrayList dep = (ArrayList)getValue(col, DEPENDENT_COLUMNS);
        if (dep == null) {
            return NullRowIterator.sharedInstance();
        }
        return dep.iterator();
    }
    
    public static void addDependentColumn(Column col, Column dep) {
        findDependentColumns(col).add(dep);
    }
    
    public static void removeDependentColumn(Column col, Column dep) {
        findDependentColumns(col).remove(dep);
    }
    
    public static boolean isDependentColumn(Column col, Column dep) {
        return findDependentColumns(col).indexOf(dep) != -1;
    }
}
