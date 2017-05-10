/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.ruler;

import infovis.Table;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.Format;



/**
 * Class DiscreteRuler
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public class LinearRulersBuilder {

    public static final double LOG10 = StrictMath.log(10);
    public static final int DEFAULT_APPROX_STEP = 100;
    public static Format doubleFormat = new DecimalFormat();

    public static void createHorizontalRulers(
            double min,
            double max,
            Rectangle2D bounds,
            double step,
            double scale,
            Table rulers,
            boolean up) {
        double last = (int)(max/step)*step;
        
        for (double tick = last; tick >= min; tick -= step) {
            double a;
            if (up) {
                a = bounds.getMinY()+StrictMath.round((max-tick)*scale);                
            }
            else {
                a = bounds.getMaxY()-StrictMath.round((max-tick)*scale);
            }

            double v = Math.round(tick * 1000 / step) * step / 1000; 
            String label = doubleFormat.format(v);
            Line2D line = new Line2D.Double(bounds.getMinX(), a, bounds.getMaxX(), a);
            RulerTable.addRuler(rulers, line, label);
            
        }
    }
    
    public static void createVerticalRulers(
            double min,
            double max,
            Rectangle2D bounds,
            double step,
            double scale,
            Table rulers) {
        double last = (int)(max/step)*step;

        assert(max >= min);
        for (double tick = last; tick >= min; tick -= step) {
            double a = bounds.getMinY()+StrictMath.round((tick-min)*scale);
            double v = Math.round(tick * 1000 / step) * step / 1000; 
            String label = doubleFormat.format(v);
            Line2D line = new Line2D.Double(a, bounds.getMinY(), a, bounds.getMaxY());
            RulerTable.addRuler(rulers, line, label);
            
        } 
    }
    
    public static double computeStep(double min, double max, double scale) {
        double size = max - min;
        
        double log10 = StrictMath.log(size)/LOG10;
        double step = StrictMath.pow(10, StrictMath.floor(log10));
        double deltaPixels = step * scale;
        if (deltaPixels > 500) {
            step /= 10;
        }
        else if (deltaPixels > 250) {
            step /= 5;
        }
        else if (deltaPixels > 200) {
            step /= 4;
        }
        else if (deltaPixels > 100) {
            step /= 2;
        }
        return step;
    }
}
