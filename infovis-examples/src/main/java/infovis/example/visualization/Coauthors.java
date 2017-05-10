/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.example.visualization;

import infovis.Column;
import infovis.example.ExampleRunner;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.graph.visualization.layout.GraphVizLayout;
import infovis.panel.color.ColorScheme;
import infovis.utils.RowIterator;
import infovis.visualization.ColorVisualization;
import infovis.visualization.color.EqualizedOrderedColor;
import infovis.visualization.render.DefaultVisualLabel;
import infovis.visualization.render.VisualColor;
import infovis.visualization.render.VisualLabel;
import infovis.visualization.render.VisualSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

/**
 * Class Coauthors
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class Coauthors extends ExampleRunner {
    public Coauthors(String args[]) {
        super(args, "Coauthors");
        String infilename = args[0];
        File infile = new File(infilename);
        int dot = infile.getName().lastIndexOf('.');
        String filename = "coauthors";
        if (dot != -1) {
            filename = infile.getName().substring(0, dot);
        }
        
        DefaultGraph graph = new DefaultGraph();
        GraphReaderFactory.readGraph(infilename, graph);
        NodeLinkGraphVisualization visualization = new NodeLinkGraphVisualization(
                graph);
        
        GraphVizLayout layout = new GraphVizLayout("fdp");
        visualization.setLayout(layout);
//        visualization.getClientProperty().addAttribute("dot::overlap", "false");
//        visualization.getClientProperty().addAttribute("dot::model", "subset");

        DefaultVisualLabel vl = (DefaultVisualLabel)VisualLabel.get(visualization);
        // vl.setJustification(0);
        vl.setOrientation(DefaultVisualLabel.ORIENTATION_WEST);
        vl.setClipped(false);
        vl.setDefaultColor(Color.BLACK);
        vl.setColumn(graph.getVertexTable().getColumn("author"));
        
        VisualSize.get(visualization).setDefaultSize(0);
        
        VisualColor vc = VisualColor.get(visualization);
        if (vc != null) {
            vc.setColumn(graph.getVertexTable().getColumn("pub"));
            ColorVisualization cv = vc.getColorVisualization();
            if (cv instanceof EqualizedOrderedColor) {
                 EqualizedOrderedColor eqcv = (EqualizedOrderedColor) cv;
                
                 eqcv.setUsingQuantiles(false);
                 ColorScheme cs = ColorScheme.getColorScheme("Oranges");
                 if (cs != null) {
                     eqcv.setRamp(cs.getRamp());
                 }
            }
        }
        
        VisualColor vlc = VisualColor.get(visualization.getVisualization(0));
        if (vlc != null) {
            vlc.setColumn(graph.getEdgeTable().getColumn("count"));
            ColorVisualization cv = vlc.getColorVisualization();
            if (cv instanceof EqualizedOrderedColor) {
                 EqualizedOrderedColor eqcv = (EqualizedOrderedColor) cv;
                
                 eqcv.setUsingQuantiles(false);
                 ColorScheme cs = ColorScheme.getColorScheme("HeatMap");
                 if (cs != null) {
                     eqcv.setRamp(cs.getRamp());
                 }
            }
        }

        Dimension d = visualization.getPreferredSize();
        Rectangle2D bounds = new Rectangle2D.Double(0, 0, d.width, d.height);
        BufferedImage image = null;
        try {
            image = new BufferedImage(
                    d.width,
                    d.height,
                    BufferedImage.TYPE_INT_RGB);
        } catch (OutOfMemoryError e) {
            System.err.println("Out of memory creating image");
            e.printStackTrace();
            return;
        }
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, d.width, d.height);
        visualization.paint(g2d, bounds);
        g2d.dispose();

        File pngfile = new  File(filename + ".png");
        try {
            ImageIO.write(image, "png", pngfile);
        } catch (IOException e) {
            System.err.println("Cannot write png file " + pngfile);
            e.printStackTrace();
        }
        image.flush();
        image = null;

        File htmlfile = new  File(filename + ".html");
        try {
            PrintWriter out = new PrintWriter(htmlfile);
            Column authorColumn = visualization.getColumn("author");
            out.print("<html><head><title>Bibliograph "+filename+"</title></head>\n");
            out.print("<body><p><object data=\""+pngfile.getName()+"\" type=\"image/png\" usemap=\"#biblio\">\n");
            out.print("<map name=\"biblio\">\n");
            for (RowIterator iter = visualization.iterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                Rectangle2D rect = (Rectangle2D)visualization.getShapeAt(vertex);
                out.print("<area");
                out.print(" href=\""+authorColumn.getValueAt(vertex)+".html\"");
                //out.print(" onMouseOver=\"window.status='"+authorColumn.getValueAt(vertex)+"';return true\"");
                out.print(" alt=\""+authorColumn.getValueAt(vertex)+"\"");
                out.print(" shape=\"rect\"");
                out.print(" coords=\""
                        +(int)rect.getMinX()+","
                        +(int)rect.getMinY()+","
                        +(int)rect.getMaxX()+","
                        +(int)rect.getMaxY()+"\"");
                out.print(">\n");
            }
            out.print("</map></object></body>\n");
            out.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //        AbstractWriter writer =
//            GraphWriterFactory.createGraphWriter("insitu.dot", visualization);
//         if (writer != null) {
//             DOTGraphWriter dot = (DOTGraphWriter) writer;
//             dot.setFont(vl.getFont());
//             dot.setLabels(vl.getColumn());
//             dot.setColors(vc);
//             dot.setShapes(visualization.getShapes());
//             dot.setLinkColors(vlc);
//             writer.write();
//         }
//        createFrame(visualization);
    }

    public static void main(String[] args) {
        new Coauthors(args);

    }
}
