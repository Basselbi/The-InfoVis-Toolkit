/**
 * 
 */
package infovis.tree.visualization;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import infovis.Column;
import infovis.Table;
import infovis.Tree;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.column.StringColumn;
import infovis.tree.DefaultTree;
import infovis.tree.io.AbstractTreeReader;
import infovis.utils.RowIterator;

/**
 * This class provides a control panel allowing on the fly user defined
 * tree structures.
 *  
 * @author Mohammad Ghoniem
 * @version $Revision$
 *
 */
public class FlexibleTreeStructureControlPanel extends TreeVisualPanel 
	implements ListSelectionListener {

    protected JList columnList;
    protected JList hierarchyList;
    protected JButton add;
    protected JButton remove;
    protected JButton up;
    protected JButton down;

	/**
	 * @param visualization
	 * @param filter
	 */
	public FlexibleTreeStructureControlPanel(Visualization visualization,
			ColumnFilter filter) {
		super(visualization, filter);
	}
	
    protected void createAll() {
        addColumnList();
    }


    protected void updateColumnList() {
        Table t = getTreeVisualization().getTree();
        DefaultListModel model = (DefaultListModel)columnList.getModel();
        model.clear();
        for (int i = 0; i < t.getColumnCount(); i++) {
            Column c = t.getColumnAt(i);
            if (c == null
                || c.isInternal()
                || !(c instanceof StringColumn)) {
                continue;
            }
            model.addElement(c);
        }
    }
    
    protected void addColumnList() {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(LEFT_ALIGNMENT);
        DefaultListModel model = new DefaultListModel();
        columnList = new JList(model);
        updateColumnList();
        JScrollPane fromSP = new JScrollPane(columnList);
        setTitleBorder(fromSP, "All");
        box.add(fromSP);
        columnList.getSelectionModel().addListSelectionListener(this);
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        Box vbox = Box.createVerticalBox();
        up = new JButton("Up");
        up.setEnabled(false);
        up.addActionListener(this);
        vbox.add(up);
        
        add = new JButton("Add");
        add.setEnabled(false);
        add.addActionListener(this);
        vbox.add(add);
        
        remove = new JButton("Remove");
        remove.setEnabled(false);
        remove.addActionListener(this);
        vbox.add(remove);
        
        down = new JButton("Down");
        down.setEnabled(false);
        down.addActionListener(this);
        vbox.add(down);
        box.add(vbox);
        
        // assuming hierarchy is initially empty
        // TODO: manage when this is not the case
        hierarchyList = new JList(new DefaultListModel());
        JScrollPane toSP = new JScrollPane(hierarchyList);
        setTitleBorder(toSP, "Hierarchy");
        hierarchyList.getSelectionModel().addListSelectionListener(this);
        hierarchyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        box.add(toSP);
        setTitleBorder(box, "Hierarchy Editor");
        add(box);
    }
    
    public void actionPerformed(ActionEvent e) {
        DefaultTree t = (DefaultTree)getTreeVisualization().getTree();
        if (e.getSource() == add) {
        	// additional levels are appended after existing ones
        	// use "up" and "down" actions for relocation
        	DefaultListModel tolm = (DefaultListModel)hierarchyList.getModel();
        	DefaultListModel fromlm = (DefaultListModel)columnList.getModel();
        	Column element;
            for (int i = 0; i < fromlm.getSize(); i++) {
                if (columnList.isSelectedIndex(i)) {
                	element = (Column)fromlm.get(i);
                	// column already exists in hierarchy, nothing to do
                	if(tolm.contains(element))
                		continue; 
                	tolm.addElement(element);
                	// column does not exist, adding an extra level in tree
            		Column currentLevel;
            		StringColumn nameColumn = StringColumn.findColumn(t, "name");
            		int node = Tree.ROOT;
            		int parent = Tree.ROOT;
            		int newparent = Tree.ROOT;
            		RowIterator liter = t.leafIterator();
            		while(liter.hasNext()){
            			node = liter.nextRow();
            			parent = t.getParent(node);
           				currentLevel = (Column)tolm.get(tolm.getSize()-1);
           				// name of structural node
               			String branch=currentLevel.getValueAt(node);
               				if(branch != null){
               					newparent = AbstractTreeReader.findNode(branch, parent, t, nameColumn);
               					if(!t.isAncestor(node, newparent))
               						t.reparent(node, newparent);
               			}
            		}
                }
            	getTreeVisualization().invalidate();
           }        	          
        } else if (e.getSource() == remove) {
        	// assuming removal at any level
        	// buggy
        	DefaultListModel lm = (DefaultListModel)hierarchyList.getModel();
        	Set<Integer> suppressedRows = new HashSet<Integer>();
        	for (int i = lm.getSize()-1; i >= 0; i--) {
        		if (hierarchyList.isSelectedIndex(i)) {

        			lm.remove(i);
        			int node = Tree.ROOT;
        			int parent = Tree.ROOT;
        			int ancestor = Tree.ROOT;
        			RowIterator liter = t.leafIterator();
        			int depth = 0;
        			while(liter.hasNext()){
        				node = liter.nextRow();
        				parent = t.getParent(node);
        				depth = t.getDepth(parent);
        				while(depth > i+1){
            				parent = t.getParent(parent);
        					depth = t.getDepth(parent);
        				}
        				if(depth == i+1){
        					ancestor = t.getParent(parent);
        					RowIterator childrenIterator = t.childrenIterator(parent);
        					while(childrenIterator.hasNext()){
        						//reassign children to ancestor in upper level
        						t.reparent(childrenIterator.nextRow(), ancestor);
        					}
    						// mark parent for deletion
        					suppressedRows.add(parent);
        				}
        			}
        			break;
        		}
        	}
        	for (int node : suppressedRows)
        		t.removeRow(node);

        	getTreeVisualization().invalidate();
        } else if (e.getSource() == up) {
        	//TODO
        }
        else if (e.getSource() == down) {
        	//TODO
        }
        else
            super.actionPerformed(e);
    }

    
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        if (e.getSource() == columnList.getSelectionModel()) {
            if (! columnList.isSelectionEmpty()) {
                hierarchyList.clearSelection();
            }
        }
        else if (e.getSource() == hierarchyList.getSelectionModel()) {
            if (! hierarchyList.isSelectionEmpty()) {
            	columnList.clearSelection();
            }
//          TODO            
        }
        add.setEnabled(!columnList.isSelectionEmpty());
        remove.setEnabled(!hierarchyList.isSelectionEmpty());
        boolean move =  !hierarchyList.isSelectionEmpty();
        up.setEnabled(move);  
        down.setEnabled(move);  
    }

}
