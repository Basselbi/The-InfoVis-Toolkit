/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;

import infovis.Graph;
import infovis.column.BooleanColumn;
import infovis.column.NumberColumn;
import infovis.example.ExampleRunner;
import infovis.graph.Algorithms;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.graph.visualization.layout.GemLayout;
import infovis.utils.RowIterator;
import infovis.visualization.LinkVisualization;
import infovis.visualization.render.DefaultVisualLabel;
import infovis.visualization.render.VisualInvisible;
import infovis.visualization.render.VisualLabel;
import infovis.visualization.render.VisualSize;

import java.awt.Color;

import cern.colt.list.IntArrayList;

/**
 * 
 * <b>SocialNetworkExample</b> is an example of social network
 * visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class SocialNetworkExample extends ExampleRunner {

    /**
     * Creates a SocialNetworkExample with the argument list and a name.
     * @param args the argument list
     * @param name the name
     */
    public SocialNetworkExample(String args[], String name) {
        super(args, name);
        DefaultGraph graph = new DefaultGraph();
        GraphReaderFactory.readGraph(getArg(0), graph);
        filterGraph(graph);
        IntArrayList added = Algorithms.connectGraph(graph);
        BooleanColumn invisible = new BooleanColumn("#invisible");
        for (int i = 0; i < added.size(); i++) {
            invisible.setExtend(added.get(i), true);
        }
        NodeLinkGraphVisualization visualization = new NodeLinkGraphVisualization(
                graph);
        LinkVisualization linkVisualization = 
            (LinkVisualization)visualization.findVisualization(LinkVisualization.class);
        VisualInvisible vf = (VisualInvisible)VisualInvisible.get(
                VisualInvisible.VISUAL, linkVisualization);
        vf.setColumn(invisible);
//        GraphVizLayout layout = new GraphVizLayout("fdp");
        NodeLinkGraphLayout layout = new GemLayout();
        DefaultVisualLabel vl = (DefaultVisualLabel)VisualLabel.get(visualization);
        // vl.setJustification(0);
        vl.setOrientation(DefaultVisualLabel.ORIENTATION_WEST);
        vl.setClipped(false);
        vl.setDefaultColor(Color.BLACK);
        VisualSize.get(visualization).setDefaultSize(10);
        visualization.getClientProperty().addAttribute("dot::overlap", "false");
        visualization.getClientProperty().addAttribute("dot::model", "subset");
        visualization.setLayout(layout);
        visualization.setVisualColumn("color", graph.getVertexTable()
                .getColumn("type"));
        // AbstractWriter writer =
        // GraphWriterFactory.createGraphWriter("nicole.dot", visualization);
        // if (writer != null) {
        // DOTGraphWriter dot = (DOTGraphWriter) writer;
        // dot.setLabels(vl.getColumn());
        // dot.setColors(VisualColor.get(visualization));
        // writer.write();
        // }
        createFrame(visualization);
    }

    /** The default arguments when none are supplied. */
    public static final String[] defaultArgs = { "data/graph/nicole.xml" };

    /**
     * The main program.
     * @param args the argument list
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            args = defaultArgs;
        }
        new SocialNetworkExample(args, "Social Network Example");

    }

    /**
     * Tests if a vertex graph is bipartite.
     * @param graph the graph
     * @param v a vertex
     * @return
     */
    public static boolean isBipartite(Graph graph, int v) {
        NumberColumn type = (NumberColumn) graph.getVertexTable().getColumn(
                "type");
        int t = type.getIntAt(v);
        for (RowIterator eiter = graph.edgeIterator(v); eiter.hasNext();) {
            int v2 = graph.getOtherVertex(eiter.nextRow(), v);
            if (type.getIntAt(v2) == t)
                return false;
        }
        return true;
    }

    /**
     * Filters the graph, removing vertices with degree &lt; 2.
     * @param graph the graph
     */
    public static void filterGraph(DefaultGraph graph) {
        boolean modified = true;
        while (modified) {
            modified = false;
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
                int v = iter.nextRow();
                assert (isBipartite(graph, v));
                if (graph.getDegree(v) < 2) {
                    iter.remove();
                    modified = true;
                }
            }
        }
    }

}
