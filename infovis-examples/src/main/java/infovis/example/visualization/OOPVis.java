package infovis.example.visualization;
import infovis.Graph;
import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.example.ExampleRunner;
import infovis.graph.DefaultGraph;
import infovis.graph.algorithm.Algorithm;
import infovis.graph.algorithm.Ordering;
import infovis.graph.io.AdjGraphReader;
import infovis.graph.io.GraphMLWriter;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.io.GraphWriterFactory;
import infovis.graph.visualization.MatrixVisualization;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.graph.visualization.layout.GemLayout;
import infovis.io.AbstractReader;
import infovis.io.AbstractWriter;
import infovis.utils.Permutation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import cern.colt.list.IntArrayList;

/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class OOPVis
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class OOPVis extends ExampleRunner   
    implements ActionListener {
    public Graph g;
    
    public OOPVis(String[] args, String name) {
        super(args, name);
        this.g = new DefaultGraph();
        this.frame = new JFrame(name);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphReaderFactory.setFrame(this.frame);
        
        AbstractReader reader = 
            GraphReaderFactory.createGraphReader(getArg(0), g);
        if (reader instanceof AdjGraphReader) {
            AdjGraphReader adj = (AdjGraphReader) reader;
            adj.setSeparator("\t");
        }
        GraphReaderFactory.tryRead(reader, this);
    }
    
    public static void main(String[] args) {
        OOPVis example = new OOPVis(args, "OOPVis");
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        if (GraphReaderFactory.READ_OK_MSG.equals(e.getActionCommand())) {
            System.out.println("Graph loaded");
            AbstractWriter writer = GraphWriterFactory.createGraphWriter("oopvis.graphml", g);
            writer.write();
            Ordering o = new Ordering();
            NumberColumn weights = null; //(NumberColumn)g.getEdgeTable().getColumn("weight");
            IntArrayList[] components = infovis.graph.Algorithms.computeConnectedComponents(g);
            Permutation comp = new Permutation(components[0]);
            
            Permutation perm = o.computeOrdering(g, weights, comp);
            System.out.println("Permutation: "+perm);
            MatrixVisualization visualization = new MatrixVisualization(g);
            visualization.setRowPermutation(perm);
            visualization.setColumnPermutation(perm);
//            NodeLinkGraphVisualization visualization = new NodeLinkGraphVisualization(g);
//            visualization.setLayout(new GemLayout());
            createFrame(visualization);

            // install a panning interactor on the visualization
//            new PanningInteractor().install(visualization);
        }
        else {
            System.err.println("cannot load " + getArg(0));
        }
    }
}
