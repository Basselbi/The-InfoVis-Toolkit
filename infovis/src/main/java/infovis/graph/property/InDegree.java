/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.property;

import infovis.Graph;
import infovis.column.AbstractIntColumn;
import infovis.graph.event.GraphChangedEvent;
import infovis.utils.RowIterator;

/**
 * Column containing the number of incident edges of each vertex of the Graph.
 *
 * <p>This column is automatically maintained by the <code>ColumnLink</code>
 * mechanism.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class InDegree extends GraphProperty {
    /** Name of the optional Column containing the number of incident edges. */
    public static final String INDEGREE_COLUMN = "[InDegree]";

    protected InDegree(Graph graph) {
        super(INDEGREE_COLUMN, graph);
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
            case GraphChangedEvent.GRAPH_EDGE_ADDED: {
                int v = graph.getFirstVertex(e.getDetail());
                set(v, get(v) + 1);
            }
                break;
            case GraphChangedEvent.GRAPH_EDGE_REMOVED: {
                int v = graph.getFirstVertex(e.getDetail());
                set(v, get(v) - 1);
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

    /**
     * {@inheritDoc}
     */
    protected void update() {
        try {
            setReadOnly(false);
            disableNotify();
            clear();
    
            for (RowIterator iter = graph.vertexIterator();
                iter.hasNext();
                ) {
                int vertex = iter.nextRow();
                setExtend(vertex, graph.getInDegree(vertex));
            }
        }
        finally {
            setReadOnly(true);
            enableNotify();
        }
    }

    /**
     * Returns the in degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     *
     * @return the in degree column associated with the graph.
     */
    public static AbstractIntColumn getColumn(Graph graph) {
        AbstractIntColumn inDegree =
            AbstractIntColumn.getColumn(graph.getVertexTable(), INDEGREE_COLUMN);
        if (inDegree == null) {
            InDegree degree =
                new InDegree(graph);
            degree.update();
            inDegree = degree;
            graph.getVertexTable().addColumn(inDegree);
        }
        return inDegree;
    }

}
