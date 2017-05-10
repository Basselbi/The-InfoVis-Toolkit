/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Tree;
import infovis.table.Item;
import infovis.visualization.inter.DefaultVisualizationInteractor;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Interactor class for tree visualizations.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 * 
 * @infovis.factory InteractorFactory infovis.tree.visualization.TreeVisualization
 */
public class TreeInteractor extends DefaultVisualizationInteractor {
    /**
     * Constructor.
     * @param vis the Visualization
     */
    public TreeInteractor(TreeVisualization vis) {
        super(vis);
    }
    
    /**
     * Return the TreeVisualization.
     * @return the TreeVisualization.
     */
    public TreeVisualization getTreeVisualization() {
        return (TreeVisualization)getVisualization();
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(Component comp) {
        super.install(comp);
        if (comp != null) {
            comp.addMouseListener(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void uninstall(Component comp) {
        super.uninstall(comp);
        if (comp != null) {
            comp.removeMouseListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
        if (! isConsideringEvent(e)) return;
        TreeVisualization vis = getTreeVisualization();
        super.mouseClicked(e);
        if (e.getClickCount() == 1
            && e.getButton() != MouseEvent.BUTTON1) {
            vis.setVisibleRoot(Tree.ROOT);
        }
        if (e.getClickCount() != 2) {
            return;
        }
        final int x = e.getX();
        final int y = e.getY();
//        Rectangle rect = new Rectangle(x, y);
//        ArrayList pick = null;
//        pick = pickAll(rect, vis.getBounds(), pick);
//        if (! pick.isEmpty()) {
//            Item r = (Item)pick.get(0);
//            vis.setVisibleRoot(r.getId());
//        }
        Item r = vis.pickTop(x, y, vis.getBounds());
        if (r != null)
            vis.setVisibleRoot(r.getId());
        e.consume();
    }    

}
