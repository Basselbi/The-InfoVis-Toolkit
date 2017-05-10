/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization.icicletree;
import infovis.Tree;
import infovis.table.Item;
import infovis.tree.DepthFirst;
import infovis.tree.visualization.IcicleTreeVisualization;
import infovis.visualization.inter.BasicVisualizationInteractor;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import cern.colt.function.IntProcedure;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public class EditableIcicleTree extends BasicVisualizationInteractor {
    int startX;
    int startY;
    boolean dragging = false;
    int dragThreshold = 4;
    int draggedNode;
    IcicleTreeVisualization tree;

    /**
     * Constructor for EditableIcicleTree.
     * @param vis the visualization
     */
    public EditableIcicleTree(IcicleTreeVisualization vis) {
        super(vis);
        this.tree = vis;
    }
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        if (! dragging &&
            (Math.abs(e.getX() - startX)+Math.abs(e.getY() - startY)) > dragThreshold) {
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
        }
        else if (dragging) {
            final int dx = e.getX() - startX;
            final int dy = e.getY() - startY;
            startX = e.getX();
            startY = e.getY();
            moveNodeBy(draggedNode, dx, dy);
            repaint();
        }
    }

    protected void moveNodeBy(int draggedNode, final int dx, final int dy) {
        DepthFirst.visitPreorder(
                tree, 
                new IntProcedure() {
            public boolean apply(int node) {
                Rectangle2D.Float rect = (Rectangle2D.Float)getShapeAt(node);
                if (rect == null)
                    return true;
                rect.x += dx;
                rect.y += dy;
                return true;
            }
        }, draggedNode);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            // avoid the node picking itelf
            moveNodeBy(draggedNode, -1000, -1000);
            Item newParent = pickTop(e.getX(), e.getY(), getBounds());
            if (newParent != null) {
                if (! tree.isAncestor(draggedNode, newParent.getId())) {
                    tree.reparent(draggedNode, newParent.getId());
                    //setVisualColumn(VISUAL_SIZE, AdditiveAggregation.buildDegreeAdditiveWeight(tree));
                }
            }
            invalidate();
            dragging = false;
            draggedNode = Tree.NIL;
        }
    }

}
