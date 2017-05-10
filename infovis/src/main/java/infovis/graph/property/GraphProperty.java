/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.property;

import infovis.Graph;
import infovis.column.PropertyColumn;
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.metadata.IO;

/**
 * <b>GraphProperty</b> is the base class for Graph Properties.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public abstract class GraphProperty extends PropertyColumn implements GraphChangedListener {
    protected Graph graph;
    
    protected GraphProperty(String name, Graph graph) {
        super(name);
        this.graph = graph;
        graph.addGraphChangedListener(this);
        getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
    }

    /**
     * Releases listeners.
     */
    public void dispose() {
        graph.removeGraphChangedListener(this);
        clear();
        graph = null;
    }

    /**
     * {@inheritDoc}
     */
    public void graphChanged(GraphChangedEvent e) {
        invalidate();
    }
}
