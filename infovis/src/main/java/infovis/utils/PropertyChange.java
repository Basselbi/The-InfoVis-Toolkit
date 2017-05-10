/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

import java.beans.PropertyChangeListener;

/**
 * <b>PropertyChange</b> declares the methods to support bound properties
 * for Beans.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface PropertyChange {

    /**
     * Adds a PropertyChangeListener to the listener list.
     *
     * The listener is registered for all properties.
     * See the fields in PNode and subclasses that start
     * with PROPERTY_ to find out which properties exist.
     * @param l  The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property. See the fields in PNode and subclasses that start
     * with PROPERTY_ to find out which properties are supported.
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param l  The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener l);


    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
