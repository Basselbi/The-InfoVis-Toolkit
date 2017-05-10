/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.ordering;

import infovis.utils.Permutation;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * <b>Ordering</b> is the interface of ordering algorithms.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public interface Ordering {
    /**
     * Computes a permutation from a specified distance matrix.
     * @param distanceMatrix the distance matrix
     * @return a permutation
     */
    Permutation computeOrdering(DoubleMatrix2D distanceMatrix);
}
