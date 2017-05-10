/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.utils.RowIterator;

import java.util.ArrayList;

import cern.colt.list.IntArrayList;

/**
 * Column containing list of timed events.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class EventColumn extends IntColumn {
    private static final long serialVersionUID = -8567550256773484562L;
    protected ArrayList eventValue;
    protected boolean valueUpdated;
    protected int minEvent = -1;
    protected int maxEvent = Integer.MAX_VALUE;
    
    /**
     * Creates an EventColumn with a specified name.
     * @param name the name
     */
    public EventColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates an EventColumn with a specified name and
     * an allocated size.
     * @param name the name
     * @param reserve the reserved size
     */
    public EventColumn(String name, int reserve) {
        super(name, reserve);
        eventValue = new ArrayList(reserve);
    }
    
    protected void set(int index) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int i) {
        return i < 0 
            || i > eventValue.size()
            || eventValue.get(i) == null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
        if (isValueUndefined(i) == undef) return;
        readonly();
    }
    
    /** 
     * {@inheritDoc}
     */
    public void set(int index, int element) {
        readonly();
    }
    
    /**
     * {@inheritDoc}
     */
    public int get(int index) {
        updateValues();
        return super.get(index);
    }    
    
    /** 
     * @return the maximum event
     */
    public int getMaxEvent() {
        return maxEvent;
    }
    
    /**
     * Sets the maximum event.
     * @param maxEvent the maximum event
     */
    public void setMaxEvent(int maxEvent) {
        if (this.maxEvent == maxEvent) {
            return;
        }
        this.maxEvent = maxEvent;
        valueUpdated = false;
        modified();
    }
    
    /**
     * 
     * @return the minumum event
     */
    public int getMinEvent() {
        return minEvent;
    }
    
    /**
     * Sets the minimum event.
     * @param minEvent the event
     */
    public void setMinEvent(int minEvent) {
        if (this.minEvent == minEvent) {
            return;
        }
        this.minEvent = minEvent;
        valueUpdated = false;
        modified();
    }
    protected int filter(int index) {
        IntArrayList list = getEvents(index);
        return filter(list);
    }
    
    protected int filter(IntArrayList list) {
        if (list == null) return 0;
        int min, max;
        if (minEvent <= list.getQuick(0)) {
            min = 0;
        }
        else {
            min = list.binarySearch(minEvent);
            if (min < 0) {
                min = - min - 1;
            }
        }
        if (maxEvent >= list.getQuick(list.getQuick(list.size()-1))) {
            max = size()-1;
        }
        else {
            max = list.binarySearch(maxEvent);
            if (max < 0) {
                max = - max;
            }
        }
        return max - min + 1;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        eventValue.ensureCapacity(newSize);
        if (newSize == 0) {
            eventValue.clear();
        }
        else if (newSize < eventValue.size()) {
            while (newSize < eventValue.size()){
                eventValue.remove(eventValue.size()-1);
            }
        }
        else {
            while(eventValue.size() < newSize) {
                eventValue.add(null);
            }
        }
        super.setSize(newSize);
        //FIXME notification won't work
    }
    
    /**
     * Returns the events at the specified index.
     * @param index the index
     * @return the events
     */
    public IntArrayList getEvents(int index) {
        return (IntArrayList)eventValue.get(index);
    }
    
    /**
     * Sets the events at the specified index.
     * @param index the index
     * @param list the events
     */
    public void setEvents(int index, IntArrayList list) {
        eventValue.set(index, list);
        super.set(index, filter(list));
    }

    /**
     * Adds an event at the specified index. 
     * @param index the index
     * @param event the event
     */
    public void addEvent(int index, int event) {
        IntArrayList events = getEvents(index);
        if (events == null) {
            events = new IntArrayList();
        }
        events.add(event);
        setEvents(index, events);
    }
    
    /**
     * Adds an event at the specified index, extending
     * the column if necessary.
     * @param index the index
     * @param event the event
     */
    public void addExtendEvent(int index, int event) {
        if (index >= size()) {
            setSize(index+1);
        }
        addEvent(index, event);
    }
    
    /**
     * Updates the values of the IntColumn.
     *
     */
    public void updateValues() {
        if (valueUpdated) {
            return;
        }
        try {
            disableNotify();
            for (RowIterator iter = iterator(); iter.hasNext(); ) {
                int index = iter.nextRow();
                super.set(index, filter(index));
            }
            valueUpdated = true;
        }
        finally {
            enableNotify();
        }
    }
}
