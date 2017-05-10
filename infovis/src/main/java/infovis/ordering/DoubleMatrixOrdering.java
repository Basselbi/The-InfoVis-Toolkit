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
import cern.colt.matrix.doublealgo.Statistic;

/**
 * <b>DoubleMatrixOrdering</b> computes the ordering or
 * a distance matrix with a specified distance function
 * and ordering algorithm. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DoubleMatrixOrdering {
    /**
     * Computes the row and column ordering of a specified
     * matrix using a specified distance function and
     * an ordering algorithm.
     * @param matrix the matrix
     * @param dist the distance function
     * @param symmetric true if the matrix is symmetric
     * @param order the order object
     * @return two permutations for the rows and columns
     */
    public static Permutation[] computeOrderings(
            DoubleMatrix2D matrix, 
            Statistic.VectorVectorFunction dist,
            boolean symmetric,
            Ordering order) {
        Permutation[] ret = new Permutation[2];
        DoubleMatrix2D distance = Statistic.distance(matrix, dist);
        Permutation col = order.computeOrdering(distance);
        ret[0] = col;
        if (symmetric) {
            ret[1] = new Permutation(col.iterator());
        }
        else {
            distance = Statistic.distance(matrix.viewDice(), dist);
            Permutation row = order.computeOrdering(distance);
            ret[1] = row;
        }
        return ret;
    }
    
}
