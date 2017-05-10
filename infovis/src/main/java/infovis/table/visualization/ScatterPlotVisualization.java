/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.ColumnId;
import infovis.column.IdColumn;
import infovis.column.LiteralColumn;
import infovis.column.NumberColumn;
import infovis.column.filter.NotStringOrNumberFilter;
import infovis.metadata.DependencyMetadata;
import infovis.panel.DoubleBoundedRangeModel;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;
import infovis.utils.IntPair;
import infovis.utils.RowIterator;
import infovis.visualization.DefaultVisualColumn;
import infovis.visualization.DefaultVisualization;
import infovis.visualization.Layout;
import infovis.visualization.render.VisualSize;
import infovis.visualization.ruler.LinearRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;

/**
 * Scatter plot visualization.
 * 
 * <p>
 * Visualize a table with a scatter plot representation.
 * </p>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.72 $
 * @infovis.factory VisualizationFactory "Table Scatter Plot" infovis.Table
 */
public class ScatterPlotVisualization extends DefaultVisualization implements Layout {
    /** Name of the property for x axis model change notification */
    public static final String              PROPERTY_X_AXIS_MODEL = "xAxisModel";
    /** Name of the property for y axis model change notification */
    public static final String              PROPERTY_Y_AXIS_MODEL = "yAxisModel";
    /** The X AXIS visual attribute. */
    public static final String              VISUAL_X_AXIS         = "xAxis";
    /** The Y AXIS visual attribute. */
    public static final String              VISUAL_Y_AXIS         = "yAxis";
    protected NumberColumnBoundedRangeModel xAxisModel;
    protected NumberColumnBoundedRangeModel yAxisModel;
    protected Column                        xAxisColumn;
    protected Column                        yAxisColumn;
    protected int                           margin                = 0;
    protected IdColumn                      idColumn;
    protected Rectangle2D                   dataBounds;
//    private static final Logger             logger                = Logger.getLogger(ScatterPlotVisualization.class);

    /**
     * Creates a new ScatterPlotVisualization object.
     * 
     * @param table
     *            the table.
     * @param xAxis
     *            the <code>Column</code> for the X Axis
     * @param yAxis
     *            the <code>Column</code> for the Y Axis
     */
    public ScatterPlotVisualization(Table table, Column xAxis, Column yAxis) {
        super(table);
        idColumn = new IdColumn();
        putVisualColumn(new DefaultVisualColumn(
                VISUAL_X_AXIS,
                true,
                NotStringOrNumberFilter.sharedInstance()) {
            public void setColumn(Column column) {
                super.setColumn(column);
                xAxisColumn = column;
                setXAxisModel(null);
            }
        });
        putVisualColumn(new DefaultVisualColumn(
                VISUAL_Y_AXIS,
                true,
                NotStringOrNumberFilter.sharedInstance()) {
            public void setColumn(Column column) {
                super.setColumn(column);
                yAxisColumn = column;
                setYAxisModel(null);
            }
        });
        setXAxisColumn(xAxis);
        setYAxisColumn(yAxis);
        this.rulers = new RulerTable();
    }

    /**
     * Creates a new ScatterPlotVisualization object.
     * 
     * @param table
     *            the table.
     * @param xAxis
     *            the name of the <code>Column</code> for the X Axis
     * @param yAxis
     *            the name of the <code>Column</code> for the Y Axis
     */
    public ScatterPlotVisualization(Table table, String xAxis, String yAxis) {
        this(table, table.getColumn(xAxis), table.getColumn(yAxis));
    }

    /**
     * Creates a new ScatterPlotVisualization object. Required columns are
     * searched in the table, taking the first two <code>NumberColumn</code> s
     * as axes.
     * 
     * @param table
     *            the table.
     */
    public ScatterPlotVisualization(Table table) {
        this(table, getNumberColumn(table, 0), getNumberColumn(table, 1));
        //FIXME
    }
    
    /**
     * Returns the data bound, either explicitely set or,
     * when null, the default data bounds.
     * @param bounds rectangle to copy the value to or null
     * @return the rectangle or null
     */
    public Rectangle2D getDataBounds(Rectangle2D bounds) {
        if (bounds == null) {
            bounds = new Rectangle2D.Double();
        }
        if (dataBounds != null) {
            bounds.setFrame(dataBounds);
            return bounds;
        }
        Rectangle2D r = getDefaultDataBounds();
        if (r == null) return null;
        bounds.setFrame(r);
        return bounds;
    }
    
    /**
     * Returns the data bound, either explicitely set or,
     * when null, the default data bounds.
     * @return the rectangle or null
     */
    public Rectangle2D getDataBounds() {
        return getDataBounds(null);
    }
    
    /**
     * Sets the data bounds used to compute the visible part.
     * @param dataBounds the dataBounds to set
     */
    public void setDataBounds(Rectangle2D dataBounds) {
        if (this.dataBounds == dataBounds) return;
        if (this.dataBounds != null 
                && dataBounds != null
                && this.dataBounds.equals(dataBounds)) {
            return;
        }
        this.dataBounds = dataBounds;
        invalidate();
    }
    
    /**
     * @return the default computed data bounds.
     */
    public Rectangle2D getDefaultDataBounds() {
        NumberColumn xCol = getNumberColumnFor(xAxisColumn);
        NumberColumn yCol = getNumberColumnFor(yAxisColumn);
        IntPair xlimits = computeMinMax(xCol);
        if (xlimits.first == -1) {
            return null;
        }
        IntPair ylimits = computeMinMax(yCol);
        if (ylimits.first == -1) {
            return null;
        }
        idColumn.setSize(getRowCount());
        double xmin;
        double xmax;
        double ymin;
        double ymax;
        if (xAxisModel == null) {
            xmin = xCol.getDoubleAt(xlimits.first);
            xmax = xCol.getDoubleAt(xlimits.second);
        }
        else {
            xmin = xAxisModel.getValueDouble();
            xmax = xmin + xAxisModel.getExtentDouble();
        }

        if (yAxisModel == null) {
            ymin = yCol.getDoubleAt(ylimits.first);
            ymax = yCol.getDoubleAt(ylimits.second);
        }
        else {
            ymin = yAxisModel.getValueDouble();
            ymax = ymin + yAxisModel.getExtentDouble();
        }
        double w = xmax - xmin;
        double h = ymax - ymin;
        return new Rectangle2D.Double(xmin, ymin, w, h);
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
        return "Table Scatter Plot";
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }
    
    /**
     * Returns the nth <code>Column</code> skipping internal columns.
     * 
     * @param t
     *            the Table.
     * @param index
     *            the index of the column, skipping over internal column,
     * 
     * @return the nth <code>NumberColumn</code> skipping internal columns.
     */
    public static NumberColumn getNumberColumn(Table t, int index) {
        NumberColumn ret = null;
        for (int i = 0; i < t.getColumnCount(); i++) {
            ret = LiteralColumn.getNumberColumn(t, i);
            if (ret != null && !ret.isInternal() && index-- == 0)
                return ret;
        }
        return null;
    }

    /**
     * Returns a valid NumberColumn for a specified column.
     * @param column the column
     * @return a valid NumberColumn
     */
    public NumberColumn getNumberColumnFor(Column column) {
        if (column == null)
            return idColumn;
        if (column instanceof NumberColumn) {
            return (NumberColumn) column;
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            Column col = table.getColumnAt(i);
            if (col instanceof NumberColumn
                    && DependencyMetadata.isDependentColumn(column, col)) {
                return (NumberColumn) col;
            }
        }
        return ColumnId.findIdColumn(column);
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        VisualSize vs = VisualSize.get(this);
        vs.install(null);
        double maxSize = vs.getMaxSize();
        double height = bounds.getHeight();
        double off = maxSize / 2 + margin;
        double insideMargin = maxSize - 2 * margin;
        Rectangle2D.Double insideBounds = new Rectangle2D.Double();
        insideBounds.setRect(bounds);
        insideBounds.x += off;
        insideBounds.y += off;
        insideBounds.width -= insideMargin;
        insideBounds.height -= insideMargin;

        if (insideBounds.isEmpty()) {
            return;
        }

        Rectangle2D dataBounds = getDataBounds();
        if (dataBounds == null) return;

        double xscale = insideBounds.width / dataBounds.getWidth(); //Math.max(dataBounds.getWidth(), 1);
        double yscale = insideBounds.height / dataBounds.getHeight(); // Math.max(dataBounds.getHeight(), 1);
        

        NumberColumn xCol = getNumberColumnFor(xAxisColumn);
        NumberColumn yCol = getNumberColumnFor(yAxisColumn);
        shapes.clear();
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            if (xCol.isValueUndefined(row) || yCol.isValueUndefined(row)) {
                freeRectAt(row);
                continue;
            }

            double xpos = 
                (xCol.getDoubleAt(row) - dataBounds.getX()) * xscale 
                + off
                + bounds.getX();
            double ypos = 
                height - ((yCol.getDoubleAt(row) - dataBounds.getY()) * yscale
                + off
                + bounds.getY());
            Rectangle2D.Float rect = findRectAt(row);
            vs.setRectSizeAt(row, rect);
            rect.x = (float) xpos - rect.width / 2;
            rect.y = (float) ypos - rect.height / 2;
            setShapeAt(row, rect);
        }
        clearRulers();
        double step = LinearRulersBuilder.computeStep(
                dataBounds.getMinX(),
                dataBounds.getMaxX(),
                xscale);
        LinearRulersBuilder.doubleFormat = xCol.getFormat();
        LinearRulersBuilder.createVerticalRulers(
                dataBounds.getMinX(),
                dataBounds.getMaxX(),
                insideBounds,
                step,
                xscale,
                getRulerTable());
        RulerTable.setAxisLabel(getRulerTable(), xAxisColumn.getName(), false);
        step = LinearRulersBuilder.computeStep(
                dataBounds.getMinY(),
                dataBounds.getMaxY(),
                yscale);
        LinearRulersBuilder.doubleFormat = yCol.getFormat();
        LinearRulersBuilder.createHorizontalRulers(
                dataBounds.getMinY(),
                dataBounds.getMaxY(),
                insideBounds,
                step,
                yscale,
                getRulerTable(), true);
        RulerTable.setAxisLabel(getRulerTable(), yAxisColumn.getName(), true);
        vs.uninstall(null);
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        return null;
    }

    /**
     * Returns the xAxisColumn.
     * 
     * @return Column
     */
    public Column getXAxisColumn() {
        return xAxisColumn;
    }

    /**
     * Returns the yAxisColumn.
     * 
     * @return Column
     */
    public Column getYAxisColumn() {
        return yAxisColumn;
    }

    /**
     * Sets the xAxisColumn. 
     * 
     * @param xAxis The xAxisColumn to set.
     */
    public void setXAxisColumn(Column xAxis) {
        setVisualColumn(VISUAL_X_AXIS, xAxis);
    }

    /**
     * Sets the yAxisColumn.
     * 
     * @param yAxis
     *            The yAxisColumn to set
     * 
     */
    public void setYAxisColumn(Column yAxis) {
        setVisualColumn(VISUAL_Y_AXIS, yAxis);
    }

    /**
     * Returns the margin.
     * 
     * @return int
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Sets the margin.
     * 
     * @param margin
     *            The margin to set
     */
    public void setMargin(int margin) {
        this.margin = margin;
        invalidate();
    }

    /**
     * Returns the xAxisModel.
     * 
     * @return DoubleBoundedRangeModel
     */
    public DoubleBoundedRangeModel getXAxisModel() {
        return xAxisModel;
    }

    /**
     * Returns the yAxisModel.
     * 
     * @return DoubleBoundedRangeModel
     */
    public DoubleBoundedRangeModel getYAxisModel() {
        return yAxisModel;
    }

    /**
     * Sets the xAxisModel.
     * 
     * @param xAxisModel
     *            The xAxisModel to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setXAxisModel(NumberColumnBoundedRangeModel xAxisModel) {
        if (xAxisModel != null && xAxisModel.getColumn() != xAxisColumn)
            return false;
        firePropertyChange(PROPERTY_X_AXIS_MODEL, this.xAxisModel, xAxisModel);
        if (this.xAxisModel != xAxisModel) {
            if (this.xAxisModel != null) {
                this.xAxisModel.removeChangeListener(this);
            }
            this.xAxisModel = xAxisModel;
            if (this.xAxisModel != null) {
                this.xAxisModel.addChangeListener(this);
            }
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * Sets the yAxisModel.
     * 
     * @param yAxisModel
     *            The yAxisModel to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setYAxisModel(NumberColumnBoundedRangeModel yAxisModel) {
        if (yAxisModel != null && yAxisModel.getColumn() != yAxisColumn)
            return false;
        if (this.yAxisModel != yAxisModel) {
            firePropertyChange(
                    PROPERTY_Y_AXIS_MODEL,
                    this.yAxisModel,
                    yAxisModel);
            if (this.yAxisModel != null) {
                this.yAxisModel.removeChangeListener(this);
            }
            this.yAxisModel = yAxisModel;
            if (this.yAxisModel != null) {
                this.yAxisModel.addChangeListener(this);
            }
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * @see infovis.visualization.DefaultVisualization#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == xAxisModel || e.getSource() == yAxisModel) {
            invalidate();
        }
        else {
            super.stateChanged(e);
        }
    }
}