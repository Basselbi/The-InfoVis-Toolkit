/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;


/**
 * Class Distribution
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface Distribution {
    boolean isEmpty();
    int size();
    boolean contains(int cat);
    double getHeight(int cat);
    void add(int cat, double weight);
    void add(Distribution i, double weight);
}
