/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;
import infovis.Table;
import infovis.Visualization;
import infovis.io.AbstractReader;
import infovis.panel.ControlPanel;
import infovis.panel.ControlPanelFactory;
import infovis.panel.DefaultAction;
import infovis.panel.DynamicQueryPanel;
import infovis.panel.VisualizationPanel;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotVisualization;
import infovis.visualization.DefaultAxisVisualization;
import infovis.visualization.Orientable;
import infovis.visualization.inter.InteractorFactory;
import infovis.visualization.render.DefaultVisualLabel;
import infovis.visualization.render.VisualLabel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/**
 * Example of scatter plot visualization.
 * 
 * Uses a specific Layout for the axis
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class ScatterPlotExample {
    /**
     * Main program.
     * @param args argument list.
     */
    public static void main(String[] args) {
        File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        }
        else {
            BasicConfigurator.configure();
        }
        
        ScatterPlotExample example =
            new ScatterPlotExample(args, "ScatterPlotExample");

        final Table t = new DefaultTable();
        AbstractReader reader =
            TableReaderFactory.createTableReader(example.getArg(0), t);

        if (reader != null && reader.load()) {
            ScatterPlotVisualization visualization =
                new ScatterPlotVisualization(t);
            ControlPanel control = example.createFrame(visualization);
            DynamicQueryPanel jquery = control.getDynamicQueryPanel();

            // CHECK++
            visualization.setXAxisModel(
                (NumberColumnBoundedRangeModel) jquery.getColumnDynamicQuery(
                        visualization.getXAxisColumn()));
            visualization.setYAxisModel(
                (NumberColumnBoundedRangeModel) jquery.getColumnDynamicQuery(
                    visualization.getYAxisColumn()));

        }
        else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }
    
    /**
     * Creates a ScatterPlotExample from args and a name.
     * @param args the main program args
     * @param name the name
     */
    public ScatterPlotExample(String[] args, String name) {
        this.name = name;
        this.args = args;
        firstFile = 0;        
    }
    


    /**
     * Return the specified argument.
     * @param index the index
     * @return the argument specified
     */
    public String getArg(int index) {
        if (args.length <= (firstFile + index)) {
            JFileChooser fchooser = new JFileChooser(".");
            fchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (fchooser.showDialog(null, "Select a data file to visualize") != JFileChooser.APPROVE_OPTION) {
                System.exit(1);
            }
            String file = fchooser.getSelectedFile().getAbsolutePath();
            String[] nargs = new String[firstFile + index + 1];
            System.arraycopy(args, 0, nargs, 0, firstFile + index);
            args = nargs;
            nargs[firstFile + index] = file;
        }
        return args[firstFile + index];
    }
    
    /**
     * @return the JFrame, creating it if necessary
     */
    public JFrame getFrame() {
        if (frame == null) {
            frame = new JFrame(name);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        return frame;
    }

    /**
     * Creates a ControlPanel frame for the specified visualization.
     * @param visualization the visualization
     * @return a ControlPanel
     */
    public ControlPanel createFrame(Visualization visualization) {
        control = create(getFrame(), visualization);
        frame.setVisible(true);
        frame.pack();
        return control;
    }
    

    protected JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new DefaultAction("Quit", 'Q') {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return fileMenu;
    }
    
    /**
     * Creates a non-standard control panel for the scatterplot.
     * 
     * @param frame the JFrame of this example
     * @param visualization the visualization 
     * @return a ControlPanel
     */
    public ControlPanel create(JFrame frame, Visualization visualization) {
        this.visualization = visualization;
        menuBar = new JMenuBar();

        frame.setJMenuBar(menuBar);
        fileMenu = createFileMenu();
        menuBar.add(fileMenu);
        ControlPanel control = ControlPanelFactory
                .createControlPanel(visualization);
        if (control == null) {
            return control;
        }
        InteractorFactory.installInteractor(control.getVisualization());
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        DefaultAxisVisualization column = new DefaultAxisVisualization(
                visualization,
                Orientable.ORIENTATION_SOUTH);
        InteractorFactory.installInteractor(column);
        VisualLabel vl = VisualLabel.get(column);
        if (vl instanceof DefaultVisualLabel) {
            DefaultVisualLabel dvl = (DefaultVisualLabel)vl;
            dvl.setOrientation(Orientable.ORIENTATION_SOUTH);
        }
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        pane.add(new VisualizationPanel(column), c);

        DefaultAxisVisualization row = new DefaultAxisVisualization(
                visualization,
                Orientable.ORIENTATION_EAST);
        InteractorFactory.installInteractor(row);
        vl = VisualLabel.get(row);
        if (vl instanceof DefaultVisualLabel) {
            DefaultVisualLabel dvl = (DefaultVisualLabel)vl;
            dvl.setOrientation(Orientable.ORIENTATION_EAST);
        }
        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(new VisualizationPanel(row), c);
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        pane.add(new VisualizationPanel(control.getVisualization()), c);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                pane, control);
        split.setResizeWeight(1.0);
        frame.getContentPane().add(split);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return control;
    }

    protected String            name;
    protected String            args[];
    protected int               firstFile;
    protected Visualization     visualization;
    protected ControlPanel      control;
    protected JFrame            frame;
    protected JMenuBar          menuBar;
    protected JMenu             fileMenu;
}
