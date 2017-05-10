/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.ShapeColumn;
import infovis.table.io.AbstractTableReader;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for Graph readers, except for formats based on XML.
 * 
 * Graph readers hold a graph and can also maintain
 * the shapes of the nodes and the shapes of the links.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public abstract class AbstractGraphReader extends AbstractTableReader {
    protected Graph graph;
    protected ShapeColumn nodeShapes;
    protected ShapeColumn linkShapes;
 
    
    /**
     * Creates an AbstractGraphReader from a name and a Graph.
     * 
     * @param name the file/input name
     * @param graph the Graph
     */   
   public AbstractGraphReader(
       String name,
       Graph graph)
   throws IOException, FileNotFoundException {
       this(open(name), name, graph);
   }
     /**
      * Creates an AbstractGraphReader from a BufferedReader, a name and a Graph.
      * 
      * @param in the BufferedReader
      * @param name the file/input name
      * @param graph the Graph
      */   
    public AbstractGraphReader(
        InputStream in,
        String name,
        Graph graph) {
        super(in, name, graph.getEdgeTable());
        this.graph = graph;
    }

    /**
     * Returns the Graph
     * @return the Graph
     */    
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * Removes all reference to outside ressources.
     *
     */
    public void reset() {
        nodeShapes = null; // don't clear
        linkShapes = null; // idem
    }
    /**
     * Returns the rectangle containing the layed-out graph
     * @return the rectangle containing the layed-out graph
     *  or null if it is not computed.
     */
    public abstract Rectangle2D.Float getBbox();
    
    protected ShapeColumn findNodeShapes() {
        if (nodeShapes == null) {
            nodeShapes = new ShapeColumn("#nodeShapes");
        }
        return nodeShapes;
    }
    
    protected Shape getNodeShape(int node) {
        return findNodeShapes().get(node); 
    }
    
    protected void setNodeShape(int node, Shape s) {
        findNodeShapes().setExtend(node, s);
    }
    
    protected Shape findNodeShape(int node) {
         return findNodeShapes().findRect(node);
    }
    
    protected ShapeColumn findLinkShapes() {
        if (linkShapes == null) {
            linkShapes = new ShapeColumn("#linkShapes");
        }
        return linkShapes;
    }

    protected Shape getLinkShape(int edge) {
        return findLinkShapes().get(edge);
    }
    
    protected void setLinkShape(int edge, Shape s) {
        findLinkShapes().setExtend(edge, s);
    }
    
    protected Shape findLinkShape(int edge) {
        if (findLinkShapes().isValueUndefined(edge)) {
            GeneralPath p = new GeneralPath();
            setLinkShape(edge, p);
            return p;
        }
        return getLinkShape(edge);
    }

    /**
     * @return the linkShapes
     */
    public ShapeColumn getLinkShapes() {
        return linkShapes;
    }

    /**
     * @param linkShapes the linkShapes to set
     */
    public void setLinkShapes(ShapeColumn linkShapes) {
        this.linkShapes = linkShapes;
    }

    /**
     * @return the nodeShapes
     */
    public ShapeColumn getNodeShapes() {
        return nodeShapes;
    }

    /**
     * @param nodeShapes the nodeShapes to set
     */
    public void setNodeShapes(ShapeColumn nodeShapes) {
        this.nodeShapes = nodeShapes;
    }
}
