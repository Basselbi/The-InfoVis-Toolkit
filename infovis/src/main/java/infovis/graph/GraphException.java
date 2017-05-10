/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;

/**
 * Class GraphException
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class GraphException extends RuntimeException {
    protected int vertex;
    protected int edge;
    /**
     * Creates a GraphException with a message, a vertex and an edge.
     * @param msg the message
     * @param vertex the vertex
     * @param edge the edge
     */
    public GraphException(String msg, int vertex, int edge) {
        super(msg);
        this.vertex = vertex;
        this.edge = edge;
    }

    /**
     * @return the edge
     */
    public int getEdge() {
        return edge;
    }

    /**
     * @return the vertex
     */
    public int getVertex() {
        return vertex;
    }
    
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	b.append(" graph exception=").append('"').append(super.getMessage()).append('"');
    	if( vertex != Graph.NIL ) {
    		b.append(" v: ").append(vertex);
    	}
    	if( edge != Graph.NIL ) {
    		b.append(" e: ").append(edge);
    	}
    	
    	return b.toString();
    }

}
