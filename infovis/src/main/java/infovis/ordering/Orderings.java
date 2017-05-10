/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.ordering;

import infovis.Table;
import infovis.column.NumberColumn;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;
import cern.colt.function.DoubleFunction;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Class Orderings
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class Orderings {

    /**
     * Computes an order based on the covariance between columns.
     * Computes the covariance matrix and derives a distance from
     * that by taking the 1 minus the absolute value. Then, computes
     * the permutation that minimizes the sum of distances for
     * successive columns.
     * @param column the columns
     * @param ord the order used to compute the TSP 
     * @return the covariance between successive columns
     */
    public static double[] covarianceOrder(NumberColumn[] column, Ordering ord) {
        DoubleMatrix2D cov = covariance(column);
        Statistic.correlation(cov);

        DoubleMatrix2D dist = cov.copy();
        dist.assign(new DoubleFunction() {
            public double apply(double v) {
                return 1 - Math.abs(v);
            }
        });

        Permutation perm = ord.computeOrdering(dist);
        NumberColumn[] res = (NumberColumn[])column.clone();
        double[] c = new double[res.length-1];
        for (int k=res.length; --k >= 0; ) {
            column[k] = res[perm.getDirect(k)];
            if (k != res.length) {
                c[k] = cov.get(perm.getInverse(k), perm.getInverse(k+1));
            }
        }
        
        return c;
    }
    
    static DoubleMatrix2D covariance(NumberColumn[] cols) {
        int columns = cols.length;
        DoubleMatrix2D covariance = new DenseDoubleMatrix2D(columns,columns);
        double sums[] = new double[columns];
        int rows = 0;
        for (int i = columns; --i>=0; ) {
            sums[i] = sum(cols[i]);
            rows = Math.max(rows, cols[i].size());
        }
        
        for (int i = columns; --i >= 0; ) {
            for (int j = i+1; --j>=0; ) {
                double sumOfProducts = dotProduct(cols[i], cols[j]);
                double cov = (sumOfProducts - sums[i]*sums[j]/rows) / rows;
                covariance.setQuick(i,j,cov);
                covariance.setQuick(j,i,cov); // symmetric
            }
        }
        return covariance;
    }
    
    static double sum(NumberColumn col) {
        double sum = 0;
        for (RowIterator iter = col.iterator(); iter.hasNext(); ) {
            sum += col.getDoubleAt(iter.nextRow());
        }
        return sum;
    }
    
    static double dotProduct(NumberColumn c1, NumberColumn c2) {
        double sum = 0;
        int length = Math.min(c1.size(), c2.size());
        for (int k = length; --k >= 0; ) {
            if (c1.isValueUndefined(k) || c2.isValueUndefined(k)) 
                continue;
            sum += c1.getDoubleAt(k) * c2.getDoubleAt(k);
        }

        return sum;
    }

}
