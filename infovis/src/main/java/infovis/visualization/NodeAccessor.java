/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

/**
 * 
 * <b>NodeAccessor</b> is an interface
 * to get the start and end nodes associated
 * with a link.
 * 
 * <p>It is used by LinkVisualization to
 * get the endpoints of the links it
 * manages.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */

public interface NodeAccessor {
    /**
     * Returns the start node of a specified link.
     * @param link the link
     * @return the start node
     */
    public int getStartNode(int link);
    
    /**
     * Returns the end node of a specified link.
     * @param link the link
     * @return the end node
     */
    public int getEndNode(int link); 
}