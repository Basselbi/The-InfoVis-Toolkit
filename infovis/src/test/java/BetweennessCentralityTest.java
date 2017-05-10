import infovis.column.AbstractDoubleColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.property.BetweennessCentrality;
import junit.framework.Assert;
import junit.framework.TestCase;
import edu.uci.ics.jung.graph.DirectedGraph;

/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class BetweennessCentralityTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class BetweennessCentralityTest extends TestCase {
        @Override
        protected void setUp() {}

//        private static <V,E> E getEdge(Graph<V,E> g, int v1Index, int v2Index, BidiMap<V,Integer> id) {
//            V v1 = id.getKey(v1Index);
//            V v2 = id.getKey(v2Index);
//            return g.findEdge(v1, v2);
//        }

        public void testRanker() {
            DefaultGraph graph = new DefaultGraph(false);
            for(int i=0; i<9; i++) {
                graph.addVertex();
            }

            graph.addEdge(0,1);
            graph.addEdge(0,6);
            graph.addEdge(1,2);
            graph.addEdge(1,3);
            graph.addEdge(2,4);
            graph.addEdge(3,4);
            graph.addEdge(4,5);
            graph.addEdge(5,8);
            graph.addEdge(7,8);
            graph.addEdge(6,7);

            AbstractDoubleColumn bc = 
                BetweennessCentrality.getColumn(graph);

//            System.out.println("ranking");
//            for (int i = 0; i < 9; i++) 
//              System.out.println(String.format("%d: %f", i, bc.getVertexRankScore(i)));
            
            Assert.assertEquals(bc.get(0)/28.0,0.2142,.001);
            Assert.assertEquals(bc.get(1)/28.0,0.2797,.001);
            Assert.assertEquals(bc.get(2)/28.0,0.0892,.001);
            Assert.assertEquals(bc.get(3)/28.0,0.0892,.001);
            Assert.assertEquals(bc.get(4)/28.0,0.2797,.001);
            Assert.assertEquals(bc.get(5)/28.0,0.2142,.001);
            Assert.assertEquals(bc.get(6)/28.0,0.1666,.001);
            Assert.assertEquals(bc.get(7)/28.0,0.1428,.001);
            Assert.assertEquals(bc.get(8)/28.0,0.1666,.001);
//
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(0,1)),
//                    10.66666,.001);
//
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(0,1)),10.66666,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(0,6)),9.33333,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(1,2)),6.5,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(1,3)),6.5,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(2,4)),6.5,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(3,4)),6.5,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(4,5)),10.66666,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(5,8)),9.33333,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(6,7)),8.0,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(7,8)),8.0,.001);
        }
        
        public void testRankerDirected() {
            DefaultGraph graph = new DefaultGraph(true);
            for(int i=0; i<5; i++) {
                graph.addVertex();
            }

            graph.addEdge(0,1);
            graph.addEdge(1,2);
            graph.addEdge(3,1);
            graph.addEdge(4,2);

            AbstractDoubleColumn bc = 
                BetweennessCentrality.getColumn(graph);

            Assert.assertEquals(bc.get(0),0,.001);
            Assert.assertEquals(bc.get(1),2,.001);
            Assert.assertEquals(bc.get(2),0,.001);
            Assert.assertEquals(bc.get(3),0,.001);
            Assert.assertEquals(bc.get(4),0,.001);

//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(0,1)),2,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(1,2)),3,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(3,1)),2,.001);
//            Assert.assertEquals(bc.getEdgeRankScore(graph.findEdge(4,2)),1,.001);
        }
    }
