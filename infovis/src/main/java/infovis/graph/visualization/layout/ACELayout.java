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
import infovis.graph.io.ChacoGraphWriter;
import infovis.graph.io.ChacoGraphWriter.CoordsReader;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;
import infovis.utils.ShapeFunction;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <b>ACELayout</b> uses the ACE program to layout graphs.
 * 
 * <p>See <a href="http://www.research.att.com/~yehuda/pubs/ace_journal.pdf">
 * Y. Koren, L. Carmel and D. Harel,
 * ACE: A Fast Multiscale Eigenvector Computation for Drawing
 * Huge Graphs</a>.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 * @infovis.factory GraphLayoutFactory "ACE"
 */
public class ACELayout extends AbstractGraphProgramLayout {
    protected ChacoGraphWriter writer;
    /**
     * Creates an ACELayout.
     */
    public ACELayout() {
        super("ACE");
    }
    
    /**
     * Creates an ACELayout.
     * @param the program name
     */
    public ACELayout(String name) {
        super(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractGraphReader createReader(InputStream in) {
        CoordsReader reader = writer.createReader(in);
//        DoubleColumn x = 
//                DoubleColumn.findColumn(
//                        visualization.getVertexTable(), 
//                        "ACE_x");
//        DoubleColumn y =
//                DoubleColumn.findColumn(
//                        visualization.getVertexTable(), 
//                        "ACE_y");
        return reader;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractWriter createWriter(
            OutputStream out,
            ShapeColumn nodeShapes,
            ShapeColumn linkShapes) {
        writer = new ChacoGraphWriter(
                out, 
                visualization.getName(),
                visualization);
        return writer;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "ACE";
    }
    
    /**
     * {@inheritDoc}
     */
    public Float transformShapes(
            ShapeColumn nodeShapes, 
            ShapeColumn linkShapes) {
        final Float bbox = shapes.getBounds(visualization.iterator());
        for (RowIterator iter = visualization.iterator(); iter.hasNext();) {
            int v = iter.nextRow();

            Rectangle2D.Float rect = getRectAt(v);
            setRectSizeAt(v, rect);
        }
        
        return nodeShapes.transform(visualization.iterator(), new ShapeFunction() {
            /**
             * {@inheritDoc}
             */
            public Shape apply(Shape shape) {
                if (shape != null) {
                    Float s = (Float)shape;
                    s.x = (s.x-bbox.x)*2000/bbox.width;
                    s.y = (s.y-bbox.y)*2000/bbox.width;
                }
                return shape;
            }
        });
        
    }
    
    protected void recomputeSizes() {
        for (RowIterator iter = visualization.iterator(); iter.hasNext();) {
            int v = iter.nextRow();

            Rectangle2D.Float rect = getRectAt(v);
            if (rect == null) {
                rect = createRect();
            }
            rect.x = 0;
            rect.y = 0;
            rect.width = 0;
            rect.height = 0;
            //setRectSizeAt(v, rect);
            setShapeAt(v, rect);
        }
    }   
}
