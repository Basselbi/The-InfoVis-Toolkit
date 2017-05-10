/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.column.ShapeColumn;
import infovis.graph.io.AbstractGraphReader;
import infovis.graph.io.DOTGraphReader;
import infovis.graph.io.DOTGraphWriter;
import infovis.io.AbstractWriter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class GraphVizLayout uses AT&amp;T GraphViz programs to perform the layout.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.22 $
 * @infovis.factory GraphLayoutFactory "GraphViz/twopi" twopi
 * @infovis.factory GraphLayoutFactory "GraphViz/dot" dot
 * @infovis.factory GraphLayoutFactory "GraphViz/neato" neato
 * @infovis.factory GraphLayoutFactory "GraphViz/circo" circo
 * @infovis.factory GraphLayoutFactory "GraphViz/fdp" fdp
 *  
 */
public class GraphVizLayout extends AbstractGraphProgramLayout {
    /**
     * Creates a GraphVizLayout with the default "twopi" program.
     */
    public GraphVizLayout() {
        super("twopi");
    }
    
    /**
     * Creates a GraphVizLayout with a specified program
     * that should read an write the DOT file format.
     * @param layoutProgram the program used to perform the layout
     */
    public GraphVizLayout(String layoutProgram) {
        super(layoutProgram);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "GraphViz/"+layoutProgram;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractWriter createWriter(OutputStream out, ShapeColumn shapes, ShapeColumn lshapes) {
        DOTGraphWriter writer = new DOTGraphWriter(out, visualization);
        writer.setShapes(shapes);
        writer.setLengthColumn(getLinkLengthColumn());
        writer.setOrientation(getOrientation());
        writer.setFixedColumn(getFixedColumn());
        return writer;
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractGraphReader createReader(InputStream in) {
        DOTGraphReader reader = new DOTGraphReader(in, visualization.getName(), visualization);
        return reader;
    }
    
}