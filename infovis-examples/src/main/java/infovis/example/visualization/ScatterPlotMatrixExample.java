/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.example.visualization;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.column.StringColumn;
import infovis.example.ExampleRunner;
import infovis.io.AbstractReader;
import infovis.ordering.Ordering;
import infovis.ordering.TSPOrdering;
import infovis.panel.DynamicQueryPanel;
import infovis.panel.VisualizationPanel;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotControlPanel;
import infovis.table.visualization.ScatterPlotVisualization;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import cern.colt.function.DoubleFunction;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Class ScatterPlotMatrixExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class ScatterPlotMatrixExample {
    public static void main(String[] args) {
        ExampleRunner example =
            new ExampleRunner(args, "ScatterPlotMatrixExample");
//        if (example.fileCount() != 1) {
//            System.err.println("Syntax: ScatterPlotMatrixExample <name>");
//            System.exit(1);
//        }
        Locale.setDefault(Locale.US);

        Table t = new DefaultTable();
        AbstractReader reader =
            TableReaderFactory.createTableReader(example.getArg(0), t);

        if (reader == null || !reader.load()) {
            DefaultTree tree = new DefaultTree();
            t = tree.getTable();
            reader = TreeReaderFactory.createTreeReader(example.getArg(0), tree);
        
            if (reader == null || !reader.load()) {
                System.err.println("cannot load " + example.getArg(0));
                System.exit(1);
            }
        }
        IntArrayList numericColumnIndices = new IntArrayList();
        for (int i = 0; i < t.getColumnCount(); i++) {
            Column c = t.getColumnAt(i);
            if ((! c.isInternal()) && c instanceof NumberColumn) {
                numericColumnIndices.add(i);
//                if (numericColumnIndices.size() == 6) break;
            }
        }
        numericColumnIndices = orderColumns(t, numericColumnIndices);
        int n = numericColumnIndices.size();
        JPanel pane = new JPanel(new GridLayout(n, n));
        JScrollPane scroll = new JScrollPane(pane);
        JPanel rowHeader = new JPanel(new GridLayout(n, 1));
        JPanel colHeader = new JPanel(new GridLayout(1, n));
        scroll.setRowHeaderView(rowHeader);
        scroll.setColumnHeaderView(colHeader);
        StringColumn label = ExampleRunner.getStringColumn(t, 0);
        ScatterPlotControlPanel cp = null;
        ScatterPlotVisualization mainVisualization = null;
        for (int i = 0; i < n; i++) {
            Column c = t.getColumnAt(numericColumnIndices.get(i));
            JLabel lab = new JLabel(c.getName(), JLabel.CENTER);
            lab.setPreferredSize(
                    new Dimension(
                            lab.getPreferredSize().width,
                            200));
            lab.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            rowHeader.add(lab);
            lab = new JLabel(c.getName(), JLabel.CENTER);
            lab.setPreferredSize(
                    new Dimension(
                            200,
                            lab.getPreferredSize().height));
            lab.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            colHeader.add(lab);
            for (int j = 0; j < n; j++) {
                final ScatterPlotVisualization visualization =
                    new ScatterPlotVisualization(t);
                final VisualizationPanel vp = new VisualizationPanel(visualization);
                vp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                vp.setPreferredSize(new Dimension(200, 200));
                pane.add(vp);
                visualization.setVisualColumn(
                        Visualization.VISUAL_LABEL, 
                        label);
                visualization.setVisualColumn(
                        ScatterPlotVisualization.VISUAL_X_AXIS,
                        t.getColumnAt(numericColumnIndices.get(i)));
                visualization.setVisualColumn(
                        ScatterPlotVisualization.VISUAL_Y_AXIS,
                        t.getColumnAt(numericColumnIndices.get(j)));
                if (cp == null) {
                    mainVisualization = visualization;
                    cp = new ScatterPlotControlPanel(visualization);                    
                }
                else {
                    mainVisualization.addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent evt) {
                                if (visualization.getVisualColumnDescriptor(evt.getPropertyName()) != null
                                    && evt.getNewValue() instanceof Column) {
                                    visualization.setVisualColumn(
                                        evt.getPropertyName(),
                                        (Column)evt.getNewValue());
                                }
//                                else if (evt.getPropertyName().equals(Visualization.PROPERTY_COLOR_VISUALIZATION)) {
//                                    visualization.setColorVisualization((ColorVisualization)evt.getNewValue());
//                                }
                            }
                    });
                    mainVisualization.getParent().addPropertyChangeListener(
                            new PropertyChangeListener() {
                                public void propertyChange(
                                        PropertyChangeEvent evt) {
                                    if (evt.getPropertyName().equals("background")) {
                                        vp.setBackground((Color)evt.getNewValue());
                                    }
                                    else if (evt.getPropertyName()
                                            .equals(VisualizationPanel.PROPERTY_USING_GRADIENT)) {
                                        vp.setUsingGradient(((Boolean)evt.getNewValue()).booleanValue());
                                    }
                                }
                            });
                }
                // Connect dynamic query components to XAxis and YAxis components of
                // scatter plot to also control the scaling.
                DynamicQueryPanel jquery = cp.getDynamicQueryPanel();
                visualization.setXAxisModel(
                        (
                            NumberColumnBoundedRangeModel) jquery
                                .getColumnDynamicQuery(
                            visualization.getXAxisColumn()));
                    visualization.setYAxisModel(
                        (
                            NumberColumnBoundedRangeModel) jquery
                                .getColumnDynamicQuery(
                            visualization.getYAxisColumn()));
            }
        }
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scroll,
                cp);
        split.setResizeWeight(1.0);
        JFrame frame = new JFrame("ScatterPlot Matrix");
        frame.getContentPane().add(split);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
}
    static IntArrayList orderColumns(Table table, IntArrayList colIdx) {
        DoubleMatrix2D cov = covariance(table, colIdx);
        Statistic.correlation(cov);

        DoubleMatrix2D dist = cov.copy();
        dist.assign(new DoubleFunction() {
            public double apply(double v) {
                return 1 - Math.abs(v);
            }
        });
        Ordering ord = new TSPOrdering();
        Permutation perm = ord.computeOrdering(dist);
        int[] res = new int[colIdx.size()];
        for (int k=colIdx.size(); --k >= 0; ) {
            res[k] = colIdx.get(perm.getDirect(k));
        }
        
        return new IntArrayList(res);
    }
    
    static DoubleMatrix2D covariance(Table table, IntArrayList colIdx) {
        int rows = table.getRowCount();
        int columns = colIdx.size();
        DoubleMatrix2D covariance = new DenseDoubleMatrix2D(columns,columns);
        double sums[] = new double[columns];
        NumberColumn[] cols = new NumberColumn[columns];
        for (int i = columns; --i>=0; ) {
            cols[i] = (NumberColumn)table.getColumnAt(colIdx.get(i));
            sums[i] = sum(cols[i]);
        }
        
        for (int i = columns; --i >= 0; ) {
            for (int j = i+1; --j>=0; ) {
                double sumOfProducts = dotProduct(cols[i], cols[j]);
                double cov = (sumOfProducts - sums[i]*sums[j]/rows) / rows;
                covariance.setQuick(i,j,cov);
                covariance.setQuick(j,i,cov); // symmetric
            }
        }
        return covariance;
    }
    
    static double sum(NumberColumn col) {
        double sum = 0;
        for (RowIterator iter = col.iterator(); iter.hasNext(); ) {
            sum += col.getDoubleAt(iter.nextRow());
        }
        return sum;
    }
    
    static double dotProduct(NumberColumn c1, NumberColumn c2) {
        double sum = 0;
        int length = Math.min(c1.size(), c2.size());
        for (int k = length; --k >= 0; ) {
            if (c1.isValueUndefined(k) || c2.isValueUndefined(k)) 
                continue;
            sum += c1.getDoubleAt(k) * c2.getDoubleAt(k);
        }

        return sum;
    }

}
