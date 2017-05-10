/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.BooleanColumn;
import infovis.column.FilterColumn;
import infovis.table.Item;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Proxy of a Visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.37 $
 */
public class VisualizationProxy implements Visualization {
    protected Visualization visualization;
    
    /**
     * Constructor.
     * @param visualization the visualization
     */
    public VisualizationProxy(Visualization visualization) {
        setVisualization(visualization);
    }
    
    /**
     * Returns the visualization.
     * @return the visualization
     */
    public Visualization getVisualization() {
        return visualization;
    }
    
    /**
     * Sets the visualization.
     * @param vis the visualization
     */
    public void setVisualization(Visualization vis) {
        this.visualization = vis;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(getClass()))
            return this;
        return visualization.findVisualization(cls);
    }
    
    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        if (index == 0)
            return visualization;
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (visualization != null)
            visualization.addPropertyChangeListener(l);
    }
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        if (visualization != null)
            visualization.addPropertyChangeListener(propertyName, listener);
    }
    /**
     * {@inheritDoc}
     */
    public LabeledItem createLabelItem(Item row) {
        if (visualization != null)
            return visualization.createLabelItem(row);
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (visualization != null)
            visualization.dispose();
    }
    /**
     * {@inheritDoc}
     */
    public Rectangle2D getBounds() {
        if (visualization != null)
            return visualization.getBounds();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        if (visualization != null)
            return visualization.getComponent();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public FilterColumn getFilter() {
        if (visualization != null)
            return visualization.getFilter();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        if (visualization != null)
            return visualization.getInteractor();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public ItemRenderer getItemRenderer() {
        if (visualization != null)
            return visualization.getItemRenderer();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        if (visualization != null)
            return visualization.getOrientation();
        return ORIENTATION_INVALID;
    }
    /**
     * {@inheritDoc}
     */
    public Component getParent() {
        if (visualization != null)
            return visualization.getParent();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Permutation getPermutation() {
        if (visualization != null)
            return visualization.getPermutation();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        if (visualization != null)
            return visualization.getPreferredSize();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public int getRowAtIndex(int index) {
        if (visualization != null)
            return visualization.getRowAtIndex(index);
        return -1;
    }
    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        if (visualization != null)
            return visualization.getRowCount();
        return 0;
    }
    /**
     * {@inheritDoc}
     */
    public int getRowIndex(int row) {
        if (visualization != null)
            return visualization.getRowIndex(row);
        return -1;
    }
    /**
     * {@inheritDoc}
     */
    public BooleanColumn getSelection() {
        if (visualization != null)
            return visualization.getSelection();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Shape getShapeAt(int row) {
        if (visualization != null)
            return visualization.getShapeAt(row);
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Table getTable() {
        if (visualization != null)
            return visualization.getTable();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Column getVisualColumn(String name) {
        if (visualization != null)
            return visualization.getVisualColumn(name);
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Iterator getVisualColumnIterator() {
        if (visualization != null)
            return visualization.getVisualColumnIterator();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public VisualColumnDescriptor getVisualColumnDescriptor(String name) {
        if (visualization != null)
            return visualization.getVisualColumnDescriptor(name);
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void fireVisualColumnDescriptorChanged(String name) {
        if (visualization != null)
            visualization.fireVisualColumnDescriptorChanged(name);
    }
    /**
     * {@inheritDoc}
     */
    public void invalidate() {
        if (visualization != null)
            visualization.invalidate();
    }
    /**
     * {@inheritDoc}
     */
    public void invalidate(Column c) {
        if (visualization != null)
            visualization.invalidate(c);
    }
    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        if (visualization != null)
            return visualization.isFiltered(row);
        return false;
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        if (visualization != null)
            return visualization.iterator();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        if (visualization != null)
            return visualization.reverseIterator();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void validateShapes(Rectangle2D bounds) {
        if (visualization != null)
            visualization.validateShapes(bounds);
    }
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (visualization != null)
            visualization.paint(graphics, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        if (visualization != null)
            visualization.print(graphics, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public ArrayList pickAll(Rectangle2D hitBox, Rectangle2D bounds,
            ArrayList pick) {
        if (visualization != null)
            return visualization.pickAll(hitBox, bounds, pick);
        return pick;
    }
    /**
     * {@inheritDoc}
     */
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        if (visualization != null)
            return visualization.pickAll(hitBox, bounds, pick);
        return pick;
    }
    /**
     * {@inheritDoc}
     */
    public Item pickTop(double x, double y, Rectangle2D bounds) {
        if (visualization != null)
            return visualization.pickTop(x, y, bounds);
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Item pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        if (visualization != null)
            return visualization.pickTop(hitBox, bounds);
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (visualization != null)
            visualization.removePropertyChangeListener(l);
    }
    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        if (visualization != null)
            visualization.removePropertyChangeListener(
                    propertyName,
                    listener);
    }
    /**
     * {@inheritDoc}
     */
    public void repaint() {
        if (visualization != null)
            visualization.repaint();
    }
    /**
     * {@inheritDoc}
     */
    public void setPermutation(Permutation perm) {
        if (visualization != null)
            visualization.setPermutation(perm);
    }
    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor inter) {
        if (visualization != null)
            visualization.setInteractor(inter);
    }
    /**
     * {@inheritDoc}
     */
    public void setItemRenderer(ItemRenderer ir) {
        if (visualization != null)
            visualization.setItemRenderer(ir);
    }
    /**
     * {@inheritDoc}
     */
    public void setOrientation(short orientation) {
        if (visualization != null)
            visualization.setOrientation(orientation);
    }
    /**
     * {@inheritDoc}
     */
    public void setParent(Component parent) {
        if (visualization != null)
            visualization.setParent(parent);
    }
    /**
     * {@inheritDoc}
     */
    public void setShapeAt(int row, Shape s) {
        if (visualization != null)
            visualization.setShapeAt(row, s);
    }
    /**
     * {@inheritDoc}
     */
    public void setVisualColumn(String name, Column column) {
        if (visualization != null)
            visualization.setVisualColumn(name, column);
    }
    /**
     * {@inheritDoc}
     */
    public Table getRulerTable() {
        if (visualization != null)
            return visualization.getRulerTable();
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        if (visualization != null)
            return visualization.getLayout();
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isInvalidated() {
        if (visualization != null)
            return visualization.isInvalidated();
        return false;
    }
}
