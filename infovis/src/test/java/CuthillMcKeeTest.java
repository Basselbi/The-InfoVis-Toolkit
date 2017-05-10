import cern.jet.random.engine.RandomEngine;
import infovis.Graph;
import infovis.graph.DefaultGraph;
import infovis.graph.algorithm.CuthillMcKee;
import infovis.graph.algorithm.RandomErdosRenyi;
import infovis.utils.Permutation;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class CuthillMcKeeTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class CuthillMcKeeTest extends TestCase {

    /**
     * Standard constructor. 
     */
    public CuthillMcKeeTest() {
    }

    /**
     * Standard named constructor.
     * @param name
     */
    public CuthillMcKeeTest(String name) {
        super(name);
    }

    /**
     * Tests the CuthillMcKee algorithm.
     */
    public void testCuthillMcKee() {
        DefaultGraph graph = new DefaultGraph(false);
        for (int i = 0; i < 8; i++ )
            graph.addVertex();
        graph.addEdge(0, 4);
        graph.addEdge(4, 2);
        graph.addEdge(2, 1);
        graph.addEdge(1, 7);
        graph.addEdge(1, 5);
        graph.addEdge(7, 5);
        graph.addEdge(6, 3);
        int[] cmk = CuthillMcKee.computeOrdering(graph);
        int[] R = {7,4,6,8,2,3,5,1}; 
        for (int i = 0; i < cmk.length; i++) {
            assertEquals(cmk[i],R[i]-1);
        }
        assertTrue(
                CuthillMcKee.computeBandwidth(graph, null) 
                >= CuthillMcKee.computeBandwidth(graph, new Permutation(cmk)));
    }
    
    void testGraph(Graph graph) {
        int[] cmk = CuthillMcKee.computeOrdering(graph);
        int b1 = CuthillMcKee.computeBandwidth(graph, null);
        int b2 = CuthillMcKee.computeBandwidth(graph, new Permutation(cmk));
//        System.out.println("b1 = "+b1);
//        System.out.println("  b2 = "+b2);
        assertTrue(b1 >= b2);
        
    }
    
    /**
     * Test a large number of random graphs
     */
    public void testRandomGraphs() {
        RandomEngine r = RandomEngine.makeDefault();
        for (int i = 10; i < 100; i++) {
            Graph graph = RandomErdosRenyi.generate(i, 0.4, r);
            assertEquals(graph.getVerticesCount(), i);
//            System.out.println(
//                    "Density = "
//                    +(2.0*graph.getEdgesCount())
//                    /(graph.getVerticesCount()*(graph.getVerticesCount()+1)));
            testGraph(graph);
        }
    }
}
