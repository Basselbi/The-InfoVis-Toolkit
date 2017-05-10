/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.column.AbstractIntColumn;
import infovis.graph.property.Degree;
import infovis.utils.BitSet;
import infovis.utils.IntLinkedList;
import infovis.utils.IntSet;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;
import cern.colt.Sorting;
import cern.colt.list.IntArrayList;

/**
 * Class CuthillMcKee
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class CuthillMcKee extends Algorithm {
    /**
     * Constructor for CuthillMcKee.
     * @param graph the graph
     */
    public CuthillMcKee(Graph graph) {
        super(graph);
    }
    
    /**
     * Compute the Cuthill-McKee reverse ordering
     * @param graph the graph
     * @return the ordering
     */
    public static int[] computeOrdering(Graph graph) {
        AbstractIntColumn degree = Degree.getColumn(graph);
        Permutation perm = new Permutation(graph.vertexIterator());
        perm.stableSort(degree);
        IntArrayList R = new IntArrayList(perm.size());
        IntLinkedList Q = new IntLinkedList();
        IntArrayList tmp = new IntArrayList();
        IntSet inR = new BitSet(perm.size());
        RowIterator eiter = null;
        for (int i = 0; i < perm.size(); i++) {
            int v = perm.getDirect(i);
            if (inR.get(v))
                continue;
            Q.add(v);
            while (! Q.isEmpty()) {
                v = Q.removeFirst();
                if (inR.get(v))
                    continue;
                R.add(v);
                inR.set(v);
                tmp.clear();
                for (eiter = graph.edgeIterator(v, eiter); 
                    eiter.hasNext(); ) {
                    int e = eiter.nextRow();
                    int v2 = graph.getOtherVertex(e, v);
                    if (! inR.get(v2)) {
                        tmp.add(v2);
                    }
                }
                Sorting.mergeSort(tmp.elements(), 0, tmp.size(), degree);
                Q.addAll(tmp);
            }
        }
        R.reverse();
        return R.elements();
    }
    
    /**
     * Compute the Cuthill-McKee reverse ordering
     * @return the ordering
     */
    public int[] computeOrdering() {
        return computeOrdering(graph);
    }
    
    /**
     * Computes the bandwidth of a graph using a specified
     * permutation.
     * 
     * The bandwidth is the maximum distance between 
     * two adjacent vertices.
     * 
     * @param graph the graph
     * @param p the vertex permutation or null 
     * @return the bandwidth 
     */
    public static int computeBandwidth(Graph graph, Permutation p) {
        int max = 0;
        
        for (RowIterator eiter = graph.edgeIterator(); 
            eiter.hasNext(); ) {
            int e = eiter.nextRow();
            int v1 = graph.getFirstVertex(e);
            int v2 = graph.getSecondVertex(e);
            int dist;
            if (p == null) {
                dist = Math.abs(v1 - v2);   
            }
            else {
                dist = Math.abs(p.getInverse(v1) - p.getInverse(v2));
            }
            max = Math.max(max, dist);
        }
        
        return max;
    }
}
