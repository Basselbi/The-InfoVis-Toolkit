/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.column.NumberColumn;
import infovis.ordering.TSPOrdering;
import infovis.utils.Permutation;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;

/**
 * Class Ordering
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class Ordering {
    infovis.ordering.Ordering matrixOrdering;
    
    public Ordering(infovis.ordering.Ordering matrixOrdering) {
        this.matrixOrdering = matrixOrdering;
    }
    
    public Ordering() {
        this(new TSPOrdering());
    }
    
    static DoubleMatrix2D distance(DoubleMatrix2D matrix) {
        return Statistic.distance(matrix, Statistic.MANHATTAN);
    }

    
    public Permutation computeOrdering(Graph g, NumberColumn weights, Permutation comp) {
        DoubleMatrix2D matrix = DijkstraShortestPath.allShortestPaths(g, weights, null);
        if (comp != null) {
            int[] selection = comp.getDirect().elements();
            matrix = matrix.viewSelection(selection, selection);
        }
        DoubleMatrix2D dist = distance(matrix);
        Permutation p = matrixOrdering.computeOrdering(dist); 
//        System.out.println("LKH returned: "+p);
        if (p == null) return null;
        if (comp != null) {
            Permutation p1 = new Permutation(comp);
            p1.permute(p);
            p = p1;
        }
        return p;
    }
}
