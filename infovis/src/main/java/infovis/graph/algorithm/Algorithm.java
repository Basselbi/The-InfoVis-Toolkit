/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.algorithm;

import javax.swing.ProgressMonitor;

import infovis.Graph;

/**
 * Class Algorithm
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public abstract class Algorithm {
    protected Graph graph;
    protected ProgressMonitor progressMonitor;
    /** White color (seen but not processed). */
    public static final int WHITE = 0;
    /** Grey color (being processed). */
    public static final int GREY  = 1;
    /** Black color (finished processed). */
    public static final int BLACK = 2;
    
    protected Algorithm(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * @return the progressMonitor
     */
    public ProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }
    
    /**
     * @param progressMonitor the progressMonitor to set
     */
    public void setProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }
    
    /**
     * Specifies the bounds of the progress monitor.
     * @param min minimum value
     * @param max maximum value
     * @param note the note
     */
    public void setProgressValues(int min, int max, String note) {
        if (progressMonitor != null) {
            progressMonitor.setMinimum(0);
            progressMonitor.setMaximum(max);
            progressMonitor.setNote(note);
        }
    }
    
    /**
     * Specifies the new value of the progress
     * @param v
     */
    public void setProgress(int v) {
        if (progressMonitor != null) {
            progressMonitor.setProgress(v);
        }
    }
    
    /**
     * 
     * @return <code>true</code> if the algorithm has been canceled
     */
    public boolean isCanceled() {
        if (progressMonitor != null) {
            return progressMonitor.isCanceled();
        }
        return false;
    }
    
    /**
     * Specifies the algorithm is finished.
     *
     */
    public void terminate() {
        if (progressMonitor != null) {
            progressMonitor.close();
        }
        
    }
}
