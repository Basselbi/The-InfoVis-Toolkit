/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.property;

import infovis.Column;
import infovis.Tree;
import infovis.tree.DepthFirst;

/**
 * Class DepthColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DepthColumn extends TreeProperty {
  /**
   * Name of the optional IntColumn referencing the depth of a node.
   */
    public static final String DEPTH_COLUMN = "[depth]";
  
    protected DepthColumn(Tree tree) {
        super(DEPTH_COLUMN, tree);
    }
    
    public static DepthColumn getColumn(Tree tree) {
        Column c = tree.getColumn(DEPTH_COLUMN);
        if (c instanceof DepthColumn) {
            return (DepthColumn)c;
        }
        return null;
    }
    
    public static DepthColumn findColumn(Tree tree) {
        DepthColumn dc = getColumn(tree);
        if (dc == null) {
            dc = new DepthColumn(tree);
            tree.addColumn(dc);
        }
        return dc;
    }
    
    protected void update() {
        try {
            setReadOnly(false);
            disableNotify();
            clear();
            DepthFirst.visit(tree, 
                    new DepthFirst.Visitor() {
                int depth = -1;
                public boolean preorder(int node) {
                    depth++;
                    setExtend(node, depth);
                    return true;
                }
                public void postorder(int node) {
                    depth--;
                }
            });
        }
        finally {
            setReadOnly(true);
            enableNotify();
            
        }
    }

}
