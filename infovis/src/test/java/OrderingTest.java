import cern.colt.GenericPermuting;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Arithmetic;
import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;
import cern.jet.random.engine.RandomGenerator;
import infovis.ordering.Ordering;
import infovis.ordering.TSPOrdering;
import infovis.utils.Permutation;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class OrderingTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class OrderingTest extends TestCase {
    
    Uniform distribution = new Uniform(new DRand());
    
    public OrderingTest(String name) {
        super(name);
    }
    
    static double[][] distMatrix = {
        { 0, 1, 2, 3, 4 },
        { 1, 0, 1, 2, 3 },
        { 2, 1, 0, 1, 2 },
        { 3, 2, 1, 0, 1 },
        { 4, 3, 2, 1, 0 }
    };
    public void testTSP() {
        TSPOrdering tsp = new TSPOrdering();
        testOrdering(tsp);
    }
    
    public void testOrdering(Ordering order) {
        DoubleMatrix2D initialDist = DoubleFactory2D.dense.make(distMatrix);
        long max = Arithmetic.longFactorial(initialDist.rows());
        for (int test = 0; test < 10; test++) {
            int[] direct = GenericPermuting.permutation(
                    distribution.nextLongFromTo(0, max-1),
                    initialDist.rows());
            
            DoubleMatrix2D dist = initialDist.viewSelection(direct, direct);
            Permutation perm = new Permutation(direct);
            System.out.println("Initial permutation "+perm);
            perm = order.computeOrdering(dist);
            Permutation directPerm = new Permutation(direct);
            directPerm.permute(perm);
            System.out.println("Reordered permutation "+directPerm);
            int dir = directPerm.getDirect(1) - directPerm.getDirect(0);
            for (int i = 1; i < directPerm.size()-1; i++) {
                assertEquals((directPerm.getDirect(i)-directPerm.getDirect(i-1)), dir);
            }
        }
    }
}
