/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Table;
import infovis.Visualization;
import infovis.column.StringColumn;
import infovis.panel.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.log4j.*;

/**
 * <b>ExampleRunner</b> is a convenient class
 * for running examples.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 */
public class ExampleRunner {
    private static Logger       logger     = Logger
                                                   .getLogger(ExampleRunner.class);
    protected String            name;
    protected String            args[];
    protected int               firstFile;
    protected JFileChooser      createImageFileChooser;
    protected BoundedRangeModel scaleModel;
    protected Visualization     visualization;
    protected ControlPanel      control;
    protected boolean           usingAgile = false;
    protected JFrame            frame;
    protected JMenuBar          menuBar;
    protected JMenu             fileMenu;

    /**
     * Creates an ExampleRunner with an argument list an an example name.
     * @param args the argument list
     * @param name the name
     */
    public ExampleRunner(String args[], String name) {
        this.name = name;
        this.args = args;
        firstFile = skipOptions();

        File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        }
        else {
            BasicConfigurator.configure();
        }
    }

    protected int skipOptions() {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) != '-') {
                return i;
            }
            else if (args[i].charAt(1) == 'a') {
                setUsingAgile(!isUsingAgile());
            }
            else {
                System.err.println("use -a to use Agile2D");
            }
        }
        return 0;
    }

    /**
     * @return the number of files in the arg list
     */
    public int fileCount() {
        return args.length - firstFile;
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
//        if (isUsingAgile()) {
//            Frame aframe = new Frame(name);
//            aframe.addWindowListener(new WindowAdapter() {
//                public void windowClosing(WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//            AgileCanvas agile = new AgileCanvas();
//            aframe.add(agile);
//            VisualizationPanel panel = new VisualizationPanel(visualization);
//            agile.add(panel);
//            panel.setBounds(0, 0, 300, 300);
//            aframe.pack();
//            aframe.setVisible(true);
//            return null;
//        }
        control = create(getFrame(), visualization);
        frame.setVisible(true);
        frame.pack();
        return control;
    }

    /**
     * Create a the visualization control panel in a specified JFrame.
     * 
     * @param frame
     *            the JFrame
     * @param visualization
     *            the visualization.
     * @return a control panel
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
//        if (isUsingAgile()) {
//            Frame aframe = new Frame(frame.getName());
//            AgileCanvas agile = new AgileCanvas();
//            aframe.add(agile);
//            agile.add(new VisualizationPanel(control.getVisualization()));
//            aframe.pack();
//            aframe.setVisible(true);
//            frame.getContentPane().add(control);
//        }
//        else {
            JSplitPane split = ControlPanelFactory
                    .createScrollVisualization(control);
            split.setResizeWeight(1.0);
            frame.getContentPane().add(split);
//        }
        return control;
    }

    protected JMenu createFileMenu() {
        createImageFileChooser = new JFileChooser(".");
        createImageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        createImageFileChooser.setDoubleBuffered(false);
        JSlider scaleSlider = new JSlider(JSlider.VERTICAL, 0, 1600, 100);
        scaleModel = scaleSlider.getModel();
        scaleSlider.setMajorTickSpacing(100);
        scaleSlider.setMinorTickSpacing(10);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        scaleSlider.setBorder(BorderFactory.createTitledBorder("Scale"));
        createImageFileChooser.setAccessory(scaleSlider);
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new AbstractAction("Create Image") {
            public void actionPerformed(ActionEvent e) {
                createImage();
            }
        });

        fileMenu.addSeparator();
        fileMenu.add(new DefaultAction("Quit", 'Q') {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return fileMenu;
    }

    protected void createImage() {
        int ret = createImageFileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = createImageFileChooser.getSelectedFile();
            createImage(file, scaleModel.getValue() / 100.0f);
        }
    }

    /**
     * Saves an image file of the visualization
     * @param file
     * @param scale
     * 
     * @throws IllegalArgumentException if scale &lt; 0
     * @throws NegativeArraySizeException (in BufferedImage constructor) if either
     * height or width of scroll.getPreferredSize dimension is &lt; 0.
     */
    protected void createImage(File file, float scale) {
    	if( scale < 0 )
    		throw new IllegalArgumentException("Negative image scale factor=" + scale);
        Component comp = visualization.getComponent();
        VisualizationPanel vp = (VisualizationPanel) comp;
        Color bg = vp.getBackground();
        boolean ug = vp.isUsingGradient();
        vp.setUsingGradient(false);
//        comp.setBackground(Color.WHITE);
        while (comp != null && !(comp instanceof JScrollPane)) {
            comp = comp.getParent();
        }
        if (comp == null) {
            logger.error("Visualization not in a scroll pane");
            return;
        }
        JScrollPane scroll = (JScrollPane) comp;
        Dimension savedSize = scroll.getSize();
        Rectangle savedBounds = scroll.getBounds();
        Rectangle bounds = new Rectangle(savedBounds);
        Dimension d = scroll.getPreferredSize();
        scroll.setSize(d);
        scroll.validate();

        bounds.x = 0;
        bounds.y = 0;
        bounds.width = (int) (d.width * scale);
        bounds.height = (int) (d.height * scale);
        BufferedImage image = null;
        try {
            image = new BufferedImage(
                    bounds.width,
                    bounds.height,
                    BufferedImage.TYPE_INT_RGB);
        } catch (OutOfMemoryError e) {
            logger.error("Out of memory creating image", e);
            JOptionPane.showMessageDialog(
                    null,
                    "Error",
                    "Out of memory creating image",
                    JOptionPane.ERROR_MESSAGE);
            // parent.add(comp);
            vp.setBackground(bg);
            vp.setUsingGradient(ug);
            scroll.setSize(savedSize);
            scroll.validate();
            return;
        }
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.scale(scale, scale);
        // g2d.setColor(comp.getBackground());
        // g2d.fill(bounds);
        // visualization.paint(g2d, bounds);
        comp.paint(g2d);
        g2d.dispose();

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            logger.error("Cannot write png file " + file, e);
        }
        image.flush();
        image = null;
        vp.setUsingGradient(ug);
        vp.setBackground(bg);
        scroll.setSize(savedSize);
        scroll.validate();
    }

    /**
     * Returns the nth stringcolumn in the specified table.
     * 
     * @param t the table
     * @param index the rank of the string column (0 is the first)
     * 
     * @return the nth string column
     */
    public static StringColumn getStringColumn(Table t, int index) {
        StringColumn ret = null;
        for (int i = 0; i < t.getColumnCount(); i++) {
            ret = StringColumn.getColumn(t, i);
            if (ret != null && !ret.isInternal() && index-- == 0)
                return ret;
        }
        return null;
    }

    /**
     * @return true if using Agile2D
     */
    public boolean isUsingAgile() {
        return usingAgile;
    }

    /**
     * Sets whether it is using Agile2D
     * @param usingAgile true if agile should be used
     */
    public void setUsingAgile(boolean usingAgile) {
        this.usingAgile = usingAgile;
    }

    /**
     * @return the visualization
     */
    public Visualization getVisualization() {
        return visualization;
    }

    /**
     * @return the File Menu
     */
    public JMenu getFileMenu() {
        return fileMenu;
    }

    /**
     * @return the Menu Bar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }
    
}
