/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.BooleanColumn;
import infovis.column.FilterColumn;
import infovis.table.Item;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;

/**
 * Class AbstractVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public abstract class AbstractVisualization implements Visualization {
    protected Component parent;
    
    /**
     * Constructor.
     */
    public AbstractVisualization() {
    }
    
    /**
     * {@inheritDoc}
     */
    public LabeledItem createLabelItem(Item row) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    public Visualization findVisualization(Class cls) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void fireVisualColumnDescriptorChanged(String name) {
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle2D getBounds() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public FilterColumn getFilter() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ItemRenderer getItemRenderer() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Component getParent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Permutation getPermutation() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowAtIndex(int index) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowIndex(int row) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Table getRulerTable() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public BooleanColumn getSelection() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Shape getShapeAt(int row) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Table getTable() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Column getVisualColumn(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public VisualColumnDescriptor getVisualColumnDescriptor(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getVisualColumnIterator() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Column c) {
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
    }

    /**
     * {@inheritDoc}
     */
    public ArrayList pickAll(
            Rectangle2D hitBox,
            Rectangle2D bounds,
            ArrayList pick) {
        return pick;
    }

    /**
     * {@inheritDoc}
     */
    public Item pickTop(double x, double y, Rectangle2D bounds) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Item pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        paint(graphics, bounds);
    }

    /**
     * {@inheritDoc}
     */
    public void repaint() {
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor inter) {
    }

    /**
     * {@inheritDoc}
     */
    public void setItemRenderer(ItemRenderer ir) {
    }

    /**
     * {@inheritDoc}
     */
    public void setParent(Component parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    public void setPermutation(Permutation perm) {
    }

    /**
     * {@inheritDoc}
     */
    public void setShapeAt(int row, Shape s) {
    }

    /**
     * {@inheritDoc}
     */
    public void setVisualColumn(String name, Column column) {
    }

    /**
     * {@inheritDoc}
     */
    public void validateShapes(Rectangle2D bounds) {
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        return pick;
    }

    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(short orientation) {
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
    }

}
