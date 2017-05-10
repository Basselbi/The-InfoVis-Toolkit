/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.column.BooleanColumn;
import infovis.table.Item;
import infovis.visualization.ItemRenderer;
import infovis.visualization.render.VisualSelection;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


/**
 * Interactor class for the VisualSelection item renderer.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory RendererInteractorFactory infovis.visualization.render.VisualSelection
 */
public class VisualSelectionInteractor extends BasicVisualizationInteractor {
    protected VisualSelection renderer;
    protected Rectangle2D selRect;
    protected ArrayList toggled;
    protected transient ArrayList pick;

    /**
     * Constructor.
     * 
     * @param renderer a VisualSelection
     */
    public VisualSelectionInteractor(ItemRenderer renderer) {
        this.renderer = (VisualSelection)renderer;
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    public void install(Component comp) {
//        if (parent == comp) return;
//        super.install(comp);
////        comp.addMouseListener(this);
////        comp.addMouseMotionListener(this);
//    }
//    
//    /**
//     * {@inheritDoc}
//     */
//    public void uninstall(Component comp) {
//        comp.removeMouseListener(this);
//        comp.removeMouseMotionListener(this);
//        super.uninstall(comp);
//    }
    
    /**
     * Sets the selected item.
     * @param sel the selected item.
     */
    public void setSelection(int sel) {
        BooleanColumn selection = renderer.getSelection();
        try {
            selection.disableNotify();
            selection.clear();
            addSelection(sel);
        }
        finally {
            selection.enableNotify();
        }
    }

    /**
     * Adds an item to the current selection.
     * 
     * @param sel the item.
     */
    public void addSelection(int sel) {
        BooleanColumn selection = renderer.getSelection();
        if (sel != -1) {
            selection.addSelectionInterval(sel, sel);
        }
    }
    
    /**
     * Toggle the selection of the specified item.
     * @param sel the item.
     */
    public void ToggleSelection(int sel) {
        BooleanColumn selection = renderer.getSelection();
        if (sel != -1) {
            if (selection.isSelectedIndex(sel)) {
                selection.removeSelectionInterval(sel, sel);
            }
            else {
                selection.addSelectionInterval(sel, sel);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConsideringEvent(InputEvent e) {
        return (!e.isConsumed() && e.getModifiers()==MouseEvent.BUTTON1_MASK);
    }
    // interface MouseListener
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        if (! isConsideringEvent(e)) 
        	return;
        selRect = new Rectangle2D.Double(e.getX(), e.getY(), 1, 1);
        mouseDragged(e);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        if (! isConsideringEvent(e) || selRect == null) 
        	return;
        selRect.add(e.getX(), e.getY());
        
        pick = pickAll(selRect, getBounds(), pick);
        if (pick.isEmpty()) return;
        for (int i = 0; i < pick.size(); i++) {
            Item sel = (Item)pick.get(i);
            if ((e.getModifiers() & MouseEvent.SHIFT_MASK) == 0) {
                if (i == 0)
                    setSelection(sel.getId());
                else
                    addSelection(sel.getId());
            }
            else if (pick.size() == 1){
                ToggleSelection(sel.getId());
            }
            else {
                addSelection(sel.getId());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        if (! isConsideringEvent(e)) return;
        mouseDragged(e);
        if (pick != null && pick.isEmpty()) {
            setSelection(-1);
        }
        selRect = null;
    }
}
