/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.column.ShapeColumn;
import infovis.graph.io.AbstractGraphReader;
import infovis.graph.io.GraphEdReader;
import infovis.graph.io.GraphEdWriter;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;
import infovis.utils.ShapeFunction;

import java.awt.Shape;
import java.awt.geom.Rectangle2D.Float;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class GemLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * @infovis.factory GraphLayoutFactory "Gem" gem
 */
public class GemLayout extends AbstractGraphProgramLayout {

    /**
     * Creates a GemLayout with the specified layout program.
     * 
     * @param layoutProgram
     *            the program to call for actual layout
     */
    public GemLayout(String layoutProgram) {
        super(layoutProgram);
    }

    /**
     * Creates a GemLayout.
     */
    public GemLayout() {
        this("gem");
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Gem";
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractWriter createWriter(OutputStream out, ShapeColumn shapes, ShapeColumn lshapes) {
        GraphEdWriter writer = new GraphEdWriter(out, visualization);
        writer.setShapes(getShapes());
        return writer;
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractGraphReader createReader(InputStream in) {
        AbstractGraphReader reader = new GraphEdReader(
                in,
                getGraphVisualization().getName(),
                getGraphVisualization());
        return reader;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public Float transformShapes(
            ShapeColumn nodeShapes, 
            ShapeColumn linkShapes) {
        final Float bbox = createRect();
        bbox.setRect(0, 0, 0, 0);
        for (RowIterator iter = visualization.iterator(); iter.hasNext(); ) {
            int i = iter.nextRow();
            Shape shape = nodeShapes.get(i);
            Float s = (Float)shape;
            s.x = s.x - s.width/2;
            s.y = s.y - s.height/2;
            if (s.isEmpty()) continue;
//            System.out.println("rect["+i+"]="+s);
            if (bbox.isEmpty()) {
                bbox.setRect(s);
            }
            else {
                bbox.add(s);
            }
        }
        return nodeShapes.transform(visualization.iterator(), new ShapeFunction() {
            /**
             * {@inheritDoc}
             */
            public Shape apply(Shape shape) {
                if (shape != null) {
                    Float s = (Float)shape;
                    s.x = (s.x-bbox.x)/3;
                    s.y = (s.y-bbox.y)/3;
                }
                return shape;
            }
        });
    }
}
