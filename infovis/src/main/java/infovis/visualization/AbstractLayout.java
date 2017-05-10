/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;

/**
 * Class AbstractLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class AbstractLayout implements Layout {
    protected transient PropertyChangeSupport     support;

    /**
     * Constructor.
     *
     */
    public AbstractLayout() {
        support = new PropertyChangeSupport(this);
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);

    }

    /**
     * Report a <code>boolean</code> bound indexed property update to any 
     * registered listeners. 
     * <p>
     * No event is fired if old and new values are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * fireIndexedPropertyChange method which takes Object values.
     * 
     * @param propertyName The programmatic name of the property that
     *                     was changed.
     * @param index        index of the property element that was changed.
     * @param oldValue     The old value of the property.
     * @param newValue     The new value of the property.
     * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int, boolean, boolean)
     */
    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    /**
     * Report an <code>int</code> bound indexed property update to any registered 
     * listeners. 
     * <p>
     * No event is fired if old and new values are equal
     * and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * fireIndexedPropertyChange method which takes Object values.
     *
     * @param propertyName The programmatic name of the property that
     *                     was changed.
     * @param index        index of the property element that was changed.
     * @param oldValue     The old value of the property.
     * @param newValue     The new value of the property.
     * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int, int, int)
     */
    public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    /**
     * Report a bound indexed property update to any registered
     * listeners. 
     * <p>
     * No event is fired if old and new values are equal
     * and non-null.
     *
     * @param propertyName The programmatic name of the property that
     *                     was changed.
     * @param index        index of the property element that was changed.
     * @param oldValue     The old value of the property.
     * @param newValue     The new value of the property.
     * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int, java.lang.Object, java.lang.Object)
     */
    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    /**
     * Fire an existing PropertyChangeEvent to any registered listeners.
     * No event is fired if the given event's old and new values are
     * equal and non-null.
     * @param evt  The PropertyChangeEvent object.
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)
     */
    public void firePropertyChange(PropertyChangeEvent evt) {
        support.firePropertyChange(evt);
    }

    /**
     * Report a boolean bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * firePropertyChange method that takes Object values.
     *
     * @param propertyName  The programmatic name of the property
     *      that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, boolean, boolean)
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Report an int bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * firePropertyChange method that takes Object values.
     *
     * @param propertyName  The programmatic name of the property
     *      that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, int, int)
     */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Report a bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     *
     * @param propertyName  The programmatic name of the property
     *      that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Returns an array of all the listeners that were added to the
     * PropertyChangeSupport object with addPropertyChangeListener().
     * <p>
     * If some listeners have been added with a named property, then
     * the returned array will be a mixture of PropertyChangeListeners
     * and <code>PropertyChangeListenerProxy</code>s. If the calling
     * method is interested in distinguishing the listeners then it must
     * test each element to see if it's a
     * <code>PropertyChangeListenerProxy</code>, perform the cast, and examine
     * the parameter.
     * 
     * <pre>
     * PropertyChangeListener[] listeners = bean.getPropertyChangeListeners();
     * for (int i = 0; i < listeners.length; i++) {
     *   if (listeners[i] instanceof PropertyChangeListenerProxy) {
     *     PropertyChangeListenerProxy proxy = 
     *                    (PropertyChangeListenerProxy)listeners[i];
     *     if (proxy.getPropertyName().equals("foo")) {
     *       // proxy is a PropertyChangeListener which was associated
     *       // with the property named "foo"
     *     }
     *   }
     * }
     *</pre>
     *
     * @see PropertyChangeListenerProxy
     * @return all of the <code>PropertyChangeListeners</code> added or an 
     *         empty array if no listeners have been added
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return support.getPropertyChangeListeners();
    }

    /**
     * Returns an array of all the listeners which have been associated 
     * with the named property.
     *
     * @param propertyName the propery
     * @return an array of all the listeners which have been associated 
     * with the named property.
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners(java.lang.String)
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return support.getPropertyChangeListeners(propertyName);
    }

    /**
     *  Check if there are any listeners for a specific property, including
     * those registered on all properties.  If <code>propertyName</code>
     * is null, only check for listeners registered on all properties.
     *
     * @param propertyName  the property name.
     * @return true if there are one or more listeners for the given property
     * @see java.beans.PropertyChangeSupport#hasListeners(java.lang.String)
     */
    public boolean hasListeners(String propertyName) {
        return support.hasListeners(propertyName);
    }

}
