/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.aggregation;

/**
 * <b>AggregatedValue</b> is the interface that each
 * aggregated value should implement.
 * 
 * <p>This class tries to generalize aggregated values.  The simplest form
 * of aggregated values is a list of values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface AggregatedValue {
    /**
     * @return the number of modalities of the value,
     * i.e. the number of bins for a histogram
     */
    int getModalities();
    
    double getModValue(int mod);
    
    String getModCategory(int mod);
}
