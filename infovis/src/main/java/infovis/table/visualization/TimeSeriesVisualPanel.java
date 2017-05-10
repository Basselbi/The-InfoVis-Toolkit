/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.*;

import infovis.*;
import infovis.column.ColumnFilter;
import infovis.column.NumberColumn;
import infovis.column.filter.ComposeOrFilter;
import infovis.column.filter.NamesCollection;
import infovis.panel.DefaultVisualPanel;

/**
 * Class TimeSeriesVisualPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class TimeSeriesVisualPanel extends DefaultVisualPanel
    implements ListSelectionListener {
    protected JList fromList;
    protected JList toList;
    protected JButton add;
    protected JButton remove;
    protected JButton up;
    protected JButton down;
 // enables support for add/remove actions
    protected NamesCollection nameFilter; 

    
    public TimeSeriesVisualPanel(
        TimeSeriesVisualization visualization,
        ColumnFilter filter) {
        super(visualization, filter);
    }
    
    public TimeSeriesVisualization getTimeSeries() {
        return (TimeSeriesVisualization)getVisualization()
            .findVisualization(TimeSeriesVisualization.class);
    }

    protected void createAll() {
        super.createAll();
        addColumnList();
    }

    public void updateColumnList() {
        Table t = getTimeSeries().getTable();
        DefaultListModel model = (DefaultListModel)fromList.getModel();
        model.clear();
        for (int i = 0; i < t.getColumnCount(); i++) {
            Column c = t.getColumnAt(i);
            if (c == null
                || c.isInternal()
                || !(c instanceof NumberColumn)) {
                continue;
            }
            model.addElement(c);
        }
    }
    
    protected void addColumnList() {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(LEFT_ALIGNMENT);
        DefaultListModel model = new DefaultListModel();
        fromList = new JList(model);
        updateColumnList();
        JScrollPane fromSP = new JScrollPane(fromList);
        setTitleBorder(fromSP, "All");
        box.add(fromSP);
        fromList.getSelectionModel().addListSelectionListener(this);
        fromList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
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
        
        toList = new JList(getTimeSeries().getDataColumnList());
        JScrollPane toSP = new JScrollPane(toList);
        setTitleBorder(toSP, "Visible");
        toList.getSelectionModel().addListSelectionListener(this);
        toList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        box.add(toSP);
        setTitleBorder(box, "Columns");
        add(box);
    }
    
    public void actionPerformed(ActionEvent e) {
        Table t = getVisualization().getTable();
        if (e.getSource() == add) {
        	DefaultListModel tolm = (DefaultListModel)toList.getModel();
        	DefaultListModel fromlm = (DefaultListModel)fromList.getModel();
            for (int i = fromlm.getSize()-1; i >= 0; i--) {
                if (fromList.isSelectedIndex(i)) {
                	Column element = (Column)fromlm.get(i);
                	if(tolm.contains(element))
                		continue;
                	tolm.add(0, element);
                    if(nameFilter == null){
                    	nameFilter = new NamesCollection();
                     	this.filter = new ComposeOrFilter(this.filter, nameFilter);
                    }
                	nameFilter.remove(element);

                	getVisualization().invalidate();
                }
           }        	
                      
        } else if (e.getSource() == remove) {
            DefaultListModel lm = (DefaultListModel)toList.getModel();
        	Column excluded;
            for (int i = lm.getSize()-1; i >= 0; i--) {
                 if (toList.isSelectedIndex(i)) {
                    excluded = (Column)lm.remove(i);
                    if(this.filter.filter(excluded))
                    	continue; // column already excluded
                    if(nameFilter == null){
                    	nameFilter = new NamesCollection();
                     	this.filter = new ComposeOrFilter(this.filter, nameFilter);
                    }
                	nameFilter.append(excluded);
                    	
                 	getVisualization().invalidate();
                 }
            }
        }
        else if (e.getSource() == up) {
        	// "up" is understood in the JList representation
            DefaultListModel lm = (DefaultListModel)toList.getModel();
            int found = 0;
            for (int i = lm.getSize()-1; i >= 0; i--) {
                 if (toList.isSelectedIndex(i)) {
                	 found = i;
                     if (i == 0)
                        continue;
                    Object up = lm.get(i-1);
                    Object dn = lm.get(i);
                    lm.set(i-1, dn);
                    lm.set(i, up);
                    // columns don't have the same indices in table
                    Column colUp = t.getColumn(up.toString());
                    Column colDn = t.getColumn(dn.toString());
                    int iUp = t.indexOf(colUp);
                    int iDn = t.indexOf(colDn);
                    t.setColumnAt(iUp, colDn);
                    t.setColumnAt(iDn, colUp);
                    getTimeSeries().invalidate();
                 }
            }
            // Keep selected index for repeated action on same element
            toList.setSelectedIndex(found-1); 
        }
        else if (e.getSource() == down) {
        	// "down" is understood in the JList representation
            DefaultListModel lm = (DefaultListModel)toList.getModel();            
            int found = 0;
            for (int i = lm.getSize()-1; i >= 0; i--) {
                 if (toList.isSelectedIndex(i)) {
                	 found = i;
                     if (i == lm.getSize()-1)
                        continue;
                    Object up = lm.get(i+1);
                    Object dn = lm.get(i);
                    lm.set(i+1, dn);
                    lm.set(i, up);
                    // columns don't have the same indices in table
                    Column colUp = t.getColumn(up.toString());
                    Column colDn = t.getColumn(dn.toString());
                    int iUp = t.indexOf(colUp);
                    int iDn = t.indexOf(colDn);
                    t.setColumnAt(iUp, colDn);
                    t.setColumnAt(iDn, colUp);
                    getTimeSeries().invalidate();
                 }
            }
            // Keep selected index for repeated action on same element
            toList.setSelectedIndex(found+1); 
        }
        else
            super.actionPerformed(e);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        if (e.getSource() == fromList.getSelectionModel()) {
            if (! fromList.isSelectionEmpty()) {
                toList.clearSelection();
            }
        }
        else if (e.getSource() == toList.getSelectionModel()) {
            if (! toList.isSelectionEmpty()) {
                fromList.clearSelection();
            }
//          TODO            
        }
        add.setEnabled(!fromList.isSelectionEmpty());
        remove.setEnabled(!toList.isSelectionEmpty());
        boolean move =  !toList.isSelectionEmpty();
        up.setEnabled(move);  
        down.setEnabled(move);  
    }


}
