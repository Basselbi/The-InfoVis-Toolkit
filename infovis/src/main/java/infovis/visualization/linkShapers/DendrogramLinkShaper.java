/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.linkShapers;

import infovis.visualization.Orientable;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Class DendogramLinkShaper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * @infovis.factory LinkShaperFactory "Dendrogram"
 */
public class DendrogramLinkShaper extends DefaultLinkShaper {
    private float   startLen = 10;
    private boolean smooth;

    /**
     * Default constructor.
     */
    public DendrogramLinkShaper() {
        this(false);
    }

    /**
     * Default constructor.
     * 
     * @param smooth
     *            true of curved links are desired
     */
    public DendrogramLinkShaper(boolean smooth) {
        this.smooth = smooth;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Dendrogram";
    }

    /**
     * {@inheritDoc}
     */
    public Shape createLink(
            Point2D startPos,
            int startOrientation,
            Point2D endPos,
            int endOrientation,
            Shape prevShape) {
        GeneralPath path;
        if (prevShape instanceof GeneralPath) {
            path = (GeneralPath) prevShape;
            path.reset();
        }
        else {
            path = new GeneralPath();
        }
        path.moveTo((float) startPos.getX(), (float) startPos.getY());
        switch (startOrientation) {
        case Orientable.ORIENTATION_NORTH:
        case Orientable.ORIENTATION_SOUTH: {
            double ymid = (endPos.getY() - startPos.getY());
            if (ymid < 0) {
                ymid = Math.max(-startLen, ymid / 2);
            }
            else {
                ymid = Math.min(startLen, ymid / 2);
            }
            ymid += startPos.getY();
            if (smooth) {
                path.curveTo(
                        (float) startPos.getX(),
                        (float) (ymid),
                        (float) endPos.getX(),
                        (float) (ymid),
                        (float) endPos.getX(),
                        (float) endPos.getY());
            }
            else {
                path.lineTo((float) startPos.getX(), (float) (ymid));
                path.lineTo((float) endPos.getX(),
                // (float)startPos.getY());
                        (float) (ymid));
            }
            break;
        }
        default: {
            double xmid = (endPos.getX() - startPos.getX());
            if (xmid < 0) {
                xmid = Math.max(-startLen, xmid / 2);
            }
            else {
                xmid = Math.min(startLen, xmid / 2);
            }
            xmid += startPos.getX();
            if (smooth) {
                path.curveTo(
                        (float) (xmid),
                        (float) startPos.getY(),
                        (float) (xmid),
                        (float) endPos.getY(),
                        (float) endPos.getX(),
                        (float) endPos.getY());
            }
            else {
                path.lineTo((float) (xmid), (float) startPos.getY());
                path.lineTo((float) (xmid), (float) endPos.getY());
            }
            break;
        }
        }
        if (!smooth) {
            path.lineTo((float) endPos.getX(), (float) endPos.getY());
        }
        return path;
    }

    /**
     * Return the starting length.
     * @return the starting length
     */
    public float getStartLen() {
        return startLen;
    }

    /**
     * sets the starting length.
     * @param startLen the starting length
     */
    public void setStartLen(float startLen) {
        if (this.startLen == startLen)
            return;
        this.startLen = startLen;
        if (visualization != null) {
            visualization.invalidate();
        }
    }

    /**
     * @return true if the curve is smooth
     */
    public boolean isSmooth() {
        return smooth;
    }

    /**
     * Sets the smooth value.
     * @param smooth the new value
     */
    public void setSmooth(boolean smooth) {
        if (this.smooth == smooth)
            return;
        this.smooth = smooth;
        if (visualization != null) {
            visualization.invalidate();
        }
    }

}
