/*****************************************************************************
 * Copyright (C) 2003-2007 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Column;
import infovis.Graph;
import infovis.Table;
import infovis.Visualization;
import infovis.column.BooleanColumn;
import infovis.column.DoubleColumn;
import infovis.column.FilterColumn;
import infovis.column.IntColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.property.Degree;
import infovis.graph.property.InDegree;
import infovis.graph.property.OutDegree;
import infovis.table.Item;
import infovis.utils.CompositeShape;
import infovis.utils.FilteredRowIterator;
import infovis.utils.InverseRowFilter;
import infovis.utils.Permutation;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;
import infovis.visualization.Layout;
import infovis.visualization.ruler.DiscreteRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import cern.colt.function.IntFunction;


/**
 * Graph Visualization using Adjacency Matrix representation.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.84 $
 * 
 * @infovis.factory VisualizationFactory "Graph Matrix" infovis.Graph
 */
public class MatrixVisualization extends GraphVisualization 
    implements Layout, PropertyChangeListener {
    /** Name of the column containing the rowSelection. */
    public static final String ROWSELECTION_COLUMN = SELECTION_COLUMN;
    /** Name of the column containing the columnSelection. */
    public static final String COLUMNSELECTION_COLUMN = "#columnSelection";
    /** Name of the column containing the rowFilter. */
    public static final String ROWFILTER_COLUMN = FILTER_COLUMN;
    /** Name of the column containing the columnFilter. */
    public static final String COLUMNFILTER_COLUMN = "#columnFilter";

    protected MatrixAxisVisualization rowVisualization;
    protected MatrixAxisVisualization columnVisualization;
    
    protected FilterColumn rowFilter;
    protected FilterEdgeDynamicQuery rowDQ;
    protected FilterColumn columnFilter;
    protected FilterEdgeDynamicQuery columnDQ;
    protected boolean squared;

    protected DoubleColumn rulersSize = new DoubleColumn("size");
    protected IntColumn rulersColor = new IntColumn("color");
    protected IntColumn rulersRow = new IntColumn("row");
    protected IntColumn rulersColumn = new IntColumn("column");

    /**
     * Constructor for MatrixVisualization.
     * 
     * @param graph
     *            the graph.
     */
    public MatrixVisualization(Graph graph) {
        super(graph);
        rowVisualization = createAxis();
        rowVisualization.setVisualColumn(
                VISUAL_FILTER, 
                FilterColumn.findColumn(getVertexTable(), ROWFILTER_COLUMN));
        rowVisualization.setVisualColumn(
                VISUAL_SELECTION, 
                BooleanColumn.findColumn(getVertexTable(), ROWSELECTION_COLUMN));
        rowVisualization.addPropertyChangeListener(this);
        setRowFilter(rowVisualization.getFilter());
        
        columnVisualization = createAxis();
        columnVisualization.setVisualColumn(
                VISUAL_FILTER, 
                FilterColumn.findColumn(getVertexTable(), COLUMNFILTER_COLUMN));
        columnVisualization.setVisualColumn(
                VISUAL_SELECTION, 
                BooleanColumn.findColumn(getVertexTable(), COLUMNSELECTION_COLUMN));
        columnVisualization.addPropertyChangeListener(this);
        setColumnFilter(columnVisualization.getFilter());
        createRulers();
        if (graph.isDirected()) {
            OutDegree.getColumn(graph); // create a maintained outDegree column.
            InDegree.getColumn(graph);
        }
        else {
            Degree.getColumn(graph);
        }
    }

    /**
     * Constructor.
     * @param table a table.
     */
    public MatrixVisualization(Table table) {
        this(DefaultGraph.getGraph(table));
    }
    
    protected MatrixAxisVisualization createAxis() {
        return new MatrixAxisVisualization(graph, this);
    }

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Graph Matrix";
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {

    }
    
    protected void createRulers() {
        this.rulers = new RulerTable();
        this.rulers.add(rulersSize);
        this.rulers.add(rulersColor);
        this.rulers.add(rulersRow);
        this.rulers.add(rulersColumn);
    }
    
    /**
     * Applies a function to each edges contained in a specified bounds.
     * 
     * <p>Continue while the function returns -1, stop otherwise.
     * Returns the last value returned by the function or -1. 
     * 
     * @param hitBox the bounds 
     * @param fn the function object
     * @return -1 or the value returned by the function object
     */
    public int foreachEdgeInBounds(Rectangle2D hitBox, IntFunction fn) {
        Rectangle2D bounds = getBounds();
        if (! bounds.contains(hitBox)) {
            if (!bounds.intersects(hitBox)) return -1;
            Rectangle2D inter = new Rectangle2D.Double();
            Rectangle2D.intersect(bounds, hitBox, inter);
            hitBox = inter;
            if (hitBox.isEmpty()) return -1; // double check
        }
        RowIterator allocIter = graph.newEdgeIterator();
        double w = (bounds.getWidth()) / getColumnPositionCount();
        double h = (bounds.getHeight()) / getRowPositionCount();

        if (squared) {
            if (w < h) {
                h = w;
            } else {
                w = h;
            }
        }
        Permutation col = getColumnPermutation();
        Permutation row = getRowPermutation();
        double xoff = bounds.getX();
        double yoff = bounds.getY();
        int ymin = (int)((hitBox.getY() - yoff) / h);
        int ymax = (int)Math.ceil((hitBox.getMaxY()-yoff) / h);
        for (int x = (int)((hitBox.getX()-xoff) / w); 
                x < (hitBox.getMaxX()-xoff) / w; x++) {
            int from = Permutation.getDirect(col, x);
            if (from == -1) continue;
            //FIXME does it work on directed graphs?
            for (RowIterator iter = graph.edgeIterator(from, allocIter); iter.hasNext(); ) {
                int edge = iter.nextRow();
                int other = graph.getOtherVertex(edge, from);
                int y = row == null ? other : row.getInverse(other);
                if (y >= ymin && y < ymax) {
                    int ret = fn.apply(edge);
                    if (ret != -1)
                        return ret;
                    }
                }
            }
        return -1;
    }
    
    public int foreachEdgeInBounds(Rectangle2D hitBox, IntFunction fn, int sample) {
        if (sample <= 1) {
            return foreachEdgeInBounds(hitBox, fn);
        }
        Rectangle2D bounds = getBounds();
        if (hitBox == null) {
            hitBox = bounds;
        }
        if (! bounds.contains(hitBox)) {
            if (!bounds.intersects(hitBox)) return -1;
            Rectangle2D inter = new Rectangle2D.Double();
            Rectangle2D.intersect(bounds, hitBox, inter);
            hitBox = inter;
            if (hitBox.isEmpty()) return -1; // double check
        }
        RowIterator allocIter = graph.newEdgeIterator();
        double w = (bounds.getWidth()) / getColumnPositionCount();
        double h = (bounds.getHeight()) / getRowPositionCount();

        if (squared) {
            if (w < h) {
                h = w;
            } else {
                w = h;
            }
        }
        Permutation col = getColumnPermutation();
        Permutation row = getRowPermutation();
        double xoff = bounds.getX();
        double yoff = bounds.getY();
        int ymin = (int)((hitBox.getY() - yoff) / h);
        int ymax = (int)Math.ceil((hitBox.getMaxY()-yoff) / h);
        for (int x = (int)((hitBox.getX()-xoff) / w); 
                x < (hitBox.getMaxX()-xoff) / w; x += sample) {
            int from = col == null ? x : col.getDirect(x);
            //FIXME does it work on directed graphs?
            for (RowIterator iter = graph.edgeIterator(from, allocIter); iter.hasNext(); ) {
                int edge = iter.nextRow();
                int other = graph.getOtherVertex(edge, from);
                int y = row == null ? other : row.getInverse(other);
                if (y >= ymin && y < ymax /* && (y % sample)==0 */) {
                    int ret = fn.apply(edge);
                    if (ret != -1)
                        return ret;
                    }
                }
            }
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public void paintItems(final Graphics2D graphics, Rectangle2D bounds) {
        Rectangle clip = graphics.getClipBounds();
        if (clip == null || clip.contains(bounds)) {
            AffineTransform at = graphics.getTransform();
            if (at.getScaleX() < 0.5 && at.getScaleY() < 0.5) {
                int sample = (int)(1.0 / at.getScaleX());
                foreachEdgeInBounds(
                        clip, 
                        new IntFunction() {
                            public int apply(int edge) {
                                paintItem(graphics, edge);
                                return -1;
                            }
                        },
                        sample);
            }
            else {
                super.paintItems(graphics, bounds);
            }
        }
        else {
            foreachEdgeInBounds(clip, new IntFunction() {
                public int apply(int edge) {
                    paintItem(graphics, edge);
                    return -1;
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization me) {
        assert(this==me);
        return new Dimension(getColumnPositionCount() * 10,
                getRowPositionCount() * 10);
    }
    
    private Rectangle2D.Float TMP_RECT = new Rectangle2D.Float();
    private Rectangle2D.Float TMP_RECT2 = new Rectangle2D.Float();
    private CompositeShape TMP_COMPOSITE;
    
    /**
     * {@inheritDoc}
     */
    public Shape getShapeAt(int edge) {
        if (getColumnPositionCount() == 0 || getRowPositionCount() == 0)
            return null;

        Rectangle2D bounds = getBounds();
        double w = (bounds.getWidth()) / getColumnPositionCount();
        double h = (bounds.getHeight()) / getRowPositionCount();

        if (squared) {
            if (w < h) {
                h = w;
            } else {
                w = h;
            }
        }
        
        int v1 = graph.getFirstVertex(edge);
        int row = getRowPosition(v1);
        if (row < 0) {
            return null;
        }

        int v2 = graph.getSecondVertex(edge);
        int col = getColumnPosition(v2);
        if (col < 0) {
            return null;
        }

        if (isDirected()) {
            TMP_RECT.setRect(
                    bounds.getX() + w * col,
                    bounds.getY() + h * row, 
                    w, h);
            return TMP_RECT;
        } else {
            Rectangle2D.Float s1 = TMP_RECT;
            s1.setRect(
                    bounds.getX() + w * col, 
                    bounds.getY() + h * row,
                    w, h);
            row = getRowPosition(v2);
            col = getColumnPosition(v1);
            if (row < 0 || col < 0) {
                return s1;
            } else {
                Rectangle2D.Float s2 = TMP_RECT2;
                s2.setRect(
                        bounds.getX() + w * col, 
                        bounds.getY() + h * row, 
                        w,
                        h);
                
                if (TMP_COMPOSITE == null) {
                    TMP_COMPOSITE   = new CompositeShape();
                    TMP_COMPOSITE.addShape(s1);
                    TMP_COMPOSITE.addShape(s2);
                }
                return TMP_COMPOSITE;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization me) {
        computeRulers(bounds);
    }
    
    /**
     * {@inheritDoc}
     */
    public Item pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        return getEdge(foreachEdgeInBounds(hitBox, new IntFunction() {
            public int apply(int edge) {
                return edge;
            }
        }));
    }
    
    /**
     * {@inheritDoc}
     */
    public ArrayList pickAll(Rectangle2D hitBox, Rectangle2D bounds, ArrayList pick) {
        validateShapes(bounds);
        if (pick == null)
            pick = new ArrayList();
        else
            pick.clear();
        final ArrayList p = pick;
        foreachEdgeInBounds(hitBox, new IntFunction() {
            public int apply(int edge) {
                p.add(getEdge(edge));
                return -1;
            }
        
        });
        return pick;
    }
    
    protected int computeRulerColor(int row) {
        if ((row&1) == 0) {
            return Color.BLACK.getRGB();
        }
        else {
            return Color.WHITE.getRGB();
        }
    }

    protected void computeRulers(Rectangle2D bounds) {
        Table rulers = getRulerTable();
        if (rulers == null) return;
        Column column = getVertexLabelColumn();
        clearRulers();
        
        int i = 0;
        double h = (bounds.getHeight()) / getRowPositionCount();
        for (RowIterator rowIter = rowIterator(); rowIter.hasNext(); i++) {
            int v = rowIter.nextRow();
            String label = (column != null) ? column.getValueAt(v) : Integer.toString(v);
            double hpos = h / 2 +  i * h;
            
            assert(i==getRowPosition(v));
            
            DiscreteRulersBuilder.createHorizontalRuler(bounds, label, hpos, rulers);
            int ruler = rulers.getRowCount()-1;
            rulersSize.setExtend(ruler, h);
            rulersColor.setExtend(ruler, computeRulerColor(i));
            rulersRow.setExtend(ruler, v);
        }
        
        i = 0;
        double w = (bounds.getWidth()) / getColumnPositionCount();
        for (RowIterator colIter = columnIterator(); colIter.hasNext(); i++) {
            int v = colIter.nextRow();
            String label = (column != null) ? column.getValueAt(v) : Integer.toString(v);
            double vpos = w / 2 +  i * w;

            assert(i==getColumnPosition(v));

            DiscreteRulersBuilder.createVerticalRuler(bounds, label, vpos, getRulerTable());
            int ruler = getRulerTable().getRowCount()-1;
            rulersSize.setExtend(ruler, w);
            rulersColor.setExtend(ruler, computeRulerColor(i));
            rulersColumn.setExtend(ruler, v);
        }
    }

    // Methods maintained for compatibility
    
    /**
     * Returns the row visualization.
     * 
     * @return the row visualization
     */
    public MatrixAxisVisualization getRowVisualization() {
        return rowVisualization;
    }
    
    /**
     * Returns the column visualization.
     * @return the column visualization.
     */
    public MatrixAxisVisualization getColumnVisualization() {
        return columnVisualization;
    }
    
    /**
     * Returns the vertexLabelColumn.
     * 
     * @return the vertexLabelColumn.
     */
    public Column getVertexLabelColumn() {
        return getRowVisualization().getVisualColumn(VISUAL_LABEL);
    }

    /**
     * Sets the vertexLabelColumn.
     * 
     * @param vertexLabelColumn
     *            The vertexLabelColumn to set.
     * 
     * @return <code>true</code> if the column has been set.
     */
    public void setVertexLabelColumn(Column vertexLabelColumn) {
        getRowVisualization().setVisualColumn(VISUAL_LABEL, vertexLabelColumn);
        getColumnVisualization().setVisualColumn(VISUAL_LABEL, vertexLabelColumn);
    }

    // Row permutation management
    /**
     * Returns the row selection.
     * 
     * @return the row selection
     */
    public BooleanColumn getRowSelection() {
        return getRowVisualization().getSelection();
    }

    /**
     * Sets the row selection.
     * 
     * @param rowSelection
     *            The row selection to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public void setRowSelection(BooleanColumn rowSelection) {
        getRowVisualization().setVisualColumn(
                VISUAL_SELECTION, 
                rowSelection);
    }

    /**
     * Returns the row filter.
     * 
     * @return the row filter
     */
    public FilterColumn getRowFilter() {
        return getRowVisualization().getFilter();
    }

    /**
     * Sets the row filter.
     * 
     * @param rowFilter
     *            The row filter to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setRowFilter(FilterColumn rowFilter) {
        if (this.rowFilter == rowFilter) return false;
        FilterColumn filter = getFilter();
        if (this.rowFilter != null) {
            this.rowFilter.removeChangeListener(this);
            rowDQ.setColumn(null);
        }
        this.rowFilter = rowFilter;
        if (this.rowFilter != null) {
            this.rowFilter.addChangeListener(this);
            if (rowDQ == null) {
                rowDQ = new FilterEdgeDynamicQuery(
                        getGraph(), this.rowFilter, true);
                rowDQ.setFilterColumn(filter);
            }
            else {
                rowDQ.setColumn(this.rowFilter);
            }
        }
        repaint();
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator vertexIterator() {
        return graph.vertexIterator();
    }

    /**
     * Returns the row permutation.
     * 
     * @return the row permutation.
     */
    public Permutation getRowPermutation() {
        return getRowVisualization().getPermutation();
    }

    /**
     * Sets the row permutation.
     * @param perm a row permutation
     */
    public void setRowPermutation(Permutation perm) {
        getRowVisualization().setPermutation(perm);
    }

    /**
     * Returns an iterator over the permuted rows.
     * 
     * @return an iterator over the permuted rows.
     */
    public RowIterator rowIterator() {
        return getRowVisualization().iterator();
    }

    /**
     * Returns the position of a specified row.
     * @param row the row
     * @return the position of the specified row.
     */
    public int getRowPosition(int row) {
        return getRowVisualization().getRowIndex(row);
    }

    /**
     * Returns the number of visible rows.
     * @return the number of visible rows.
     */
    public int getRowPositionCount() {
        return getRowVisualization().getRowCount();
    }
    
    // Column permutation management
    /**
     * Returns the columnSelection.
     * 
     * @return BooleanColumn
     */
    public BooleanColumn getColumnSelection() {
        return getColumnVisualization().getSelection();
    }

    /**
     * Sets the columnSelection.
     * 
     * @param columnSelection
     *            The columnSelection to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public void setColumnSelection(BooleanColumn columnSelection) {
        getColumnVisualization().setVisualColumn(
                VISUAL_SELECTION, 
                columnSelection);
    }

    /**
     * Returns the columnFilter.
     * 
     * @return FilterColumn
     */
    public FilterColumn getColumnFilter() {
        return getColumnVisualization().getFilter();
    }

    /**
     * Sets the columnFilter.
     * 
     * @param columnFilter
     *            The columnFilter to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setColumnFilter(FilterColumn columnFilter) {
        if (this.columnFilter == columnFilter) return false;
        FilterColumn filter = getFilter();
        if (this.columnFilter != null) {
            this.columnFilter.removeChangeListener(this);
            columnDQ.setColumn(null);
        }
        this.columnFilter = columnFilter;
        if (this.columnFilter != null) {
            this.columnFilter.addChangeListener(this);
            if (columnDQ == null) {
                columnDQ = new FilterEdgeDynamicQuery(
                        getGraph(), this.columnFilter, false);
                columnDQ.setFilterColumn(filter);
            }
            else {
                columnDQ.setColumn(this.columnFilter);
            }
            
        }
        repaint();
        return true;
    }

    /**
     * Returns <code>true</code> if the specified column is filtered.
     * 
     * @param column
     *            the column.
     * 
     * @return <code>true</code> if the column is filtered.
     */
    public boolean isColumnFiltered(int column) {
        return getColumnVisualization().isFiltered(column);
    }

    /**
     * Returns the column permutation.
     * 
     * @return the column permutation.
     */
    public Permutation getColumnPermutation() {
        return getColumnVisualization().getPermutation();
    }

    /**
     * Sets the column permutation.
     * 
     * @param perm the permutation to set
     * 
     */
    public void setColumnPermutation(Permutation perm) {
        getColumnVisualization().setPermutation(perm);
    }

    /**
     * Returns an iterator over the permuted columns.
     * 
     * @return an iterator over the permuted columns.
     */
    public RowIterator columnIterator() {
        return getColumnVisualization().iterator();
    }

    /**
     * Returns the position of the specified column or -1 if it is hidden.
     * 
     * @param col
     *            the column.
     * 
     * @return the position of the specified column or -1 if it is hidden.
     */
    public int getColumnPosition(int col) {
        return getColumnVisualization().getRowIndex(col);
    }

    /**
     * Returns the count of visible column positions.
     * 
     * @return the count of visible column positions.
     */
    public int getColumnPositionCount() {
        return getColumnVisualization().getRowCount();
    }

    /**
     * Returns the squared.
     * 
     * @return boolean
     */
    public boolean isSquared() {
        return squared;
    }

    /**
     * Sets the squared.
     * 
     * @param squared
     *            The squared to set
     */
    public void setSquared(boolean squared) {
        if (this.squared != squared) {
            this.squared = squared;
            invalidate();
        }
    }
    
    /**
     * Filters out the rows and inversly filters out the columns.
     * 
     * <p>This method is designed to show bipartite graphs.
     * 
     * @param filter
     */
    public void filterRowColumn(RowFilter filter) {
        Permutation rowP = new Permutation(
                new FilteredRowIterator(
                        rowIterator(),
                        filter));
        Permutation colP = new Permutation(
                new FilteredRowIterator(
                        columnIterator(), 
                        new InverseRowFilter(filter)));
        setRowPermutation(rowP);
        setColumnPermutation(colP);
    }
    
    /**
     * Filters the rows and columns with a specified partition.
     * 
     * <p>The elements set to true in the partition are visible
     * on the rows whereas the elements set to false 
     * are visible on the columns.  The elements that are undefined
     * don't appear at all.
     * 
     * @param partition the partition
     */
    public void filterRowColumn(final BooleanColumn partition) {
        Permutation rowP = new Permutation(
                new FilteredRowIterator(
                        rowIterator(),
                        new RowFilter() {
                            public boolean isFiltered(int row) {
                                return partition.isValueUndefined(row) || !partition.get(row);
                            }
                        }));
        Permutation colP = new Permutation(
                new FilteredRowIterator(
                        columnIterator(), 
                        new RowFilter() {
                            public boolean isFiltered(int row) {
                                return partition.isValueUndefined(row) || partition.get(row);
                            }
                        }));
        setRowPermutation(rowP);
        setColumnPermutation(colP);
    }
    
    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == rowVisualization ||
                evt.getSource() == columnVisualization) {
            String prop = evt.getPropertyName();
            if (prop.equals(PROPERTY_PERMUTATION)) {
                invalidate();
            }
        }
    }
}