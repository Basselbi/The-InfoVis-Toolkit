/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.graph.io.AbstractGraphReader;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.io.AbstractWriter;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Class AbstractGraphProgramLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public abstract class AbstractGraphProgramLayout extends AbstractGraphLayout {
    protected static final Logger LOG = Logger.getLogger(AbstractGraphProgramLayout.class);
    protected String layoutProgram;
    protected boolean debugging = true;
    protected Rectangle2D.Float bbox;

    /**
     * Creates an abstractGraphProgramLayout with the specified layout program.
     * @param layoutProgram the layout program to use
     */
    public AbstractGraphProgramLayout(String layoutProgram) {
        this.layoutProgram = layoutProgram;
    }
    
    /**
     * {@inheritDoc} 
     */
    public void computeShapes(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
        if (bbox == null) {
            super.computeShapes(bounds, vis);
            bbox = callLayoutProgram();
            if (bbox.x != 0 || bbox.y != 0) {
                shapes.translate(shapes.iterator(), -bbox.x, -bbox.y);
                bbox.x = 0;
                bbox.y = 0;
            }
        }
    }

    /**
     * @return Returns the layout program name
     */
    public String getLayoutProgram() {
        return layoutProgram;
    }

    /**
     * Sets the layout program name.
     * @param program the program name
     */
    public void setLayoutProgram(String program) {
        if (!layoutProgram.equals(program)) {
            layoutProgram = program;
            invalidateVisualization();
        }
    }

    /**
     * Creates a writer for sending the graph to the external program.
     * 
     * @param out the output stream for the writer
     * @param nodeShapes the node shape column to fill
     * @param linkShapes the link shape column to fill
     * @return a writer
     */
    public abstract AbstractWriter createWriter(
            OutputStream out,
            ShapeColumn nodeShapes, 
            ShapeColumn linkShapes);
    
    /**
     * Creates a reader for reading the graph laid out by the external program.
     * @param in the input stream
     * @return a reader
     */
    public abstract AbstractGraphReader createReader(InputStream in);

    /**
     * Transforms the shapes if required as the last stage of the layout.
     * @param nodeShapes the shapes of the nodes
     * @param linkShapes the shapes of the links
     * @return the transformed rectangle
     */
    public Rectangle2D.Float transformShapes(
            ShapeColumn nodeShapes, 
            ShapeColumn linkShapes) {
        Rectangle2D.Float bounds = null;
        if (nodeShapes != null) {
            bounds = nodeShapes.getBounds(visualization.iterator());
        }
        if (linkShapes != null) {
            Rectangle2D.Float lbounds = linkShapes.getBounds(
                    visualization.getLinkVisualization().iterator());
            if (bounds == null) {
                bounds = lbounds;
            }
            else if (lbounds != null) {
                bounds.add(lbounds);
            }
            
        }
        return bounds;
    }

    protected Rectangle2D.Float callLayoutProgram() {
        OutputStream out = null;
        InputStream in = null;
        InputStream err = null;
        
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(layoutProgram);
            out = proc.getOutputStream();
            in = proc.getInputStream();
            err = proc.getErrorStream();
        } catch (Exception ex) {
            if (proc != null) {
                proc.destroy();
            }
            LOG.error(
                    "Exception while setting up Process: "
                    + ex.getMessage()
                    + "\nInstall the external program called: "+layoutProgram,
                    ex);
            out = null;
            in = null;
        }

        if (out == null || in == null)
            return null;
        try {
            OutputStream curOut = out;
            if (debugging) {
                ByteArrayOutputStream wout = new ByteArrayOutputStream();
                curOut = wout;
            }
            AbstractWriter writer = createWriter(
                    curOut, 
                    getShapes(),
                    getLinkShapes());
            if (!writer.write())
                return null;
            writer = null;
            if (debugging) {
                ByteArrayOutputStream wout = (ByteArrayOutputStream)curOut; 
                byte[] b = wout.toByteArray();
                wout.close();
                wout = null;
                OutputStream bout;
            
                bout = new FileOutputStream("debug.out");
                bout.write(b);
                bout.close();
                bout = null;
                out.write(b);
            }
            out.close();
            System.err.println("Graph sent");
            AbstractGraphReader reader = createReader(in);
            reader.setNodeShapes(getShapes());
            reader.setLinkShapes(getLinkShapes());

            if (reader == null || !reader.load()) {
                LOG.error("Cannot read results of the program called: "+layoutProgram);
                if (debugging) {
                    File fin = new File("debug.out");
                    for (int i = 1; i < 100; i++) {
                        File f = new File("debug.out."+i);
                        if (! f.exists()) {
                            fin.renameTo(f);
                            break;
                        }
                    }
                }
                return null;
            }
            reader.reset();
            System.err.println("Graph layout read");
            return transformShapes(
                    getShapes(),
                    getLinkShapes());
        } catch (IOException e) {            
            if (err != null) {
                try {
                    InputStreamReader bin = new InputStreamReader(err);
                    char[] buffer = new char[1024];
                    while(bin.ready()) {
                        int r = bin.read(buffer);
                        System.err.print(new String(buffer, 0, r));
                    }
                }
                catch(IOException e2) {
                    ;
                }
            }
            LOG.error("Problem writing/reading stream from program: "+layoutProgram, e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
        super.invalidate(vis);
        bbox = null;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (bbox == null) {
            ((NodeLinkGraphVisualization)vis).computeShapes(null);
            if (bbox != null) {
                preferredSize = new Dimension((int)bbox.getWidth(), (int)bbox.getHeight());
            }
        }
        return preferredSize; 
    }    
}
