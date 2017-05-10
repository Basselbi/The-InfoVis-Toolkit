/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.example.visualization;

import infovis.Graph;
import infovis.Tree;
import infovis.column.AbstractBooleanColumn;
import infovis.column.BooleanColumn;
import infovis.example.ExampleRunner;
import infovis.graph.Algorithms;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.io.GraphWriterFactory;
import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.graph.visualization.layout.ACELayout;
import infovis.io.AbstractReader;
import infovis.io.AbstractWriter;
import infovis.table.Item;
import infovis.visualization.ItemRenderer;
import infovis.visualization.inter.RendererInteractorFactory;
import infovis.visualization.inter.VisualSelectionInteractor;
import infovis.visualization.render.VisualSelection;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import cern.colt.list.IntArrayList;

/**
 * Example of graph visualization with an adjacency matrix.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */
public class NodeLinkGraphExample extends VisualSelectionInteractor {
    int startX;
    int startY;
    boolean dragging = false;
    int dragThreshold = 4;
    int draggedNode;
    AbstractBooleanColumn fixed;

    /**
     * Creates the example which is also a VisualSelecctionInteractor.
     * @param renderer the children item renderer.
     */
    public NodeLinkGraphExample(ItemRenderer renderer) {
        super(renderer);
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(JComponent comp) {
        if (comp == parent) return;
        super.install(comp);
        fixed = BooleanColumn.findColumn(getTable(), "fixed");
    }

    /**
     * {@inheritDoc}
     */
    public void uninstall(JComponent comp) {
        super.uninstall(comp);
        fixed = null;
        super.uninstall(comp);
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        super.mousePressed(e);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (!dragging
                && (Math.abs(e.getX() - startX) + Math.abs(e.getY()
                        - startY)) > dragThreshold) {
            Item picked = pickTop(startX, startY, getBounds());
            if (picked != null) {
                draggedNode = picked.getId(); 
            }
            else {
                draggedNode = Tree.NIL;
            }
            if (draggedNode != Tree.NIL)
                dragging = true;
            startX = e.getX();
            startY = e.getY();
        } else if (dragging) {
            final int dx = e.getX() - startX;
            final int dy = e.getY() - startY;
            startX = e.getX();
            startY = e.getY();
            moveNodeBy(draggedNode, dx, dy);
            repaint();
        }
    }

    protected void moveNodeBy(int draggedNode, final int dx,
            final int dy) {
        Rectangle2D.Float rect = (Rectangle2D.Float) getShapeAt(draggedNode);
        if (rect == null)
            return;
        rect.x += dx;
        rect.y += dy;
        fixed.addSelectionInterval(draggedNode, draggedNode);
        setShapeAt(draggedNode, rect);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            //invalidate();
            dragging = false;
            if ((e.getModifiers()&MouseEvent.SHIFT_MASK)!=0) {
                fixed.removeIndexInterval(draggedNode, draggedNode);
            }
            draggedNode = -1;
            invalidate();
        }
        super.mouseReleased(e);
    }

    /**
     * Main program. 
     * @param args the argument list
     */
    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args,
                "NodeLinkGraphExample");
        Graph g;

        if (args.length == 0) {
            //g = Algorithms.getOneCompnentGraph();
            g = Algorithms.getGridGraph(10, 10);
        }
        else {
            g = new DefaultGraph();
            AbstractReader reader = 
            GraphReaderFactory.createGraphReader(
                example.getArg(0),
                g);
            if (reader == null || ! reader.load()) {
                System.err.println("cannot load " + example.getArg(0));
                System.exit(1);
            }
        }
        IntArrayList added = Algorithms.connectGraph(g);
        
        RendererInteractorFactory.getInstance().add(
                VisualSelection.class.getName(),
                NodeLinkGraphExample.class.getName(), null);
        NodeLinkGraphVisualization visualization =
            new NodeLinkGraphVisualization(g);
        NodeLinkGraphLayout layout = 
//            new FRLayout()
//                new PackingGraphLayout(
//                        new GemLayout());
            new ACELayout();
        layout.setFixedColumn(new BooleanColumn("fixed"));
        
        AbstractWriter writer = GraphWriterFactory.createGraphWriter("out.g", g);
        if (writer == null || ! writer.write()) {
            System.err.println("Problem writing out.g");
        }
        example.createFrame(visualization);
    }
}