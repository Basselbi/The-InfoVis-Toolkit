/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.graph.property;

import infovis.Graph;
import infovis.column.AbstractIntColumn;
import infovis.graph.event.GraphChangedEvent;
import infovis.utils.RowIterator;

/**
 * <b>Degree</b> maintains the degree of a graph in an IntColumn.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class Degree extends GraphProperty {
    /** Property name. */
    public static final String DEGREE_COLUMN = "[Degree]";

    protected Degree(Graph graph) {
        super(DEGREE_COLUMN, graph);
    }
    

    /**
     * Returns the degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     *
     * @return the degree column associated with the graph.
     */
    public static AbstractIntColumn getColumn(Graph graph) {
        AbstractIntColumn degree =
            AbstractIntColumn.getColumn(
                    graph.getVertexTable(),
                    DEGREE_COLUMN);
        if (degree == null) {
            Degree d = new Degree(graph);
            d.update();
            degree = d;
            graph.getVertexTable().addColumn(degree);
        }
        return degree;
    }

    /**
     * {@inheritDoc}
     */
    protected void update() {
        try {
            setReadOnly(false);
            disableNotify();
            clear();

            for (RowIterator iter = graph.getVertexTable().iterator();
                iter.hasNext();
                ) {
                int vertex = iter.nextRow();
                setExtend(vertex, graph.getDegree(vertex));
            }
        }
        finally {
            setReadOnly(true);
            enableNotify();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void graphChanged(GraphChangedEvent e) {
        try {
            setReadOnly(false);
            switch (e.getType()) {
            case GraphChangedEvent.GRAPH_VERTEX_ADDED:
                setExtend(e.getDetail(), 0);
                break;
            case GraphChangedEvent.GRAPH_VERTEX_REMOVED:
                setValueUndefined(e.getDetail(), true);
                break;
            case GraphChangedEvent.GRAPH_EDGE_ADDED:
            case GraphChangedEvent.GRAPH_EDGE_REMOVED:
                try {
                    disableNotify();
                    int v = graph.getFirstVertex(e.getDetail());
                    set(v, graph.getDegree(v));
                    v = graph.getSecondVertex(e.getDetail());
                    set(v, graph.getDegree(v));
                } finally {
                    enableNotify();
                }
                break;
            default:
                invalidate();
            }
        }
        finally {
            setReadOnly(true);
        }
    }    
}
