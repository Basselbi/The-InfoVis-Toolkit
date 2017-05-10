/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.property;

import infovis.Tree;
import infovis.column.PropertyColumn;
import infovis.metadata.IO;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * Base class for columns computing topological values on a tree.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class TreeProperty extends PropertyColumn 
    implements TreeModelListener {
    protected Tree    tree;

    /**
     * Constructor.
     * 
     * @param name
     *            column name
     * @param tree
     *            associated tree
     */
    public TreeProperty(String name, Tree tree) {
        super(name, tree.getRowCount());
        this.tree = tree;
        tree.addTreeModelListener(this);
        getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
        //update();
    }

    /**
     * Releases listeners.
     */
    public void dispose() {
        tree.removeTreeModelListener(this);
        clear();
        tree = null;
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesChanged(TreeModelEvent e) {
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesInserted(TreeModelEvent e) {
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesRemoved(TreeModelEvent e) {
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void treeStructureChanged(TreeModelEvent e) {
        invalidate();
    }

}
