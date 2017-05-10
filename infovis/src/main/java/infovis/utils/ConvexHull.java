package infovis.utils;

import infovis.Visualization;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * Main Screen Panel for showing the result.
 */
public class ConvexHull {

    /** Indicates no intersection between shapes */
    public static final int NO_INTERSECTION = 0;
    /** Indicates intersection between shapes */
    public static final int COINCIDENT      = -1;
    /** Indicates two lines are parallel */
    public static final int PARALLEL        = -2;

    /**
     * Compute the convex hull of several sets of items in a
     * visualization. 
     * @param vis the visualization
     * @param comp the items
     * @return a shape containing all the shapes
     */
    public static Shape computeConvexHulls(Visualization vis, IntArrayList comp) {
        DoubleArrayList ch = new DoubleArrayList();

        for (int i = 0; i < comp.size(); i++) {
            int v = comp.get(i);
            Shape shape = vis.getShapeAt(v);
            if (shape == null) {
                return null;
            }
            addShape(ch, shape);
        }
            
        double[] pts = ConvexHull.convexHull(ch.elements(), ch.size());
        if (pts == null || pts.length < 6) {
            return null;
        }
        GeneralPath p = new GeneralPath();
        p.moveTo((float) pts[0], (float) pts[1]);
        for (int j = 2; j < pts.length; j += 2) {
            p.lineTo((float) pts[j], (float) pts[j + 1]);
        }
        return p;
    }

    private static void addShape(DoubleArrayList list, Shape shape) {
        double[] coords = new double[2];
        for (PathIterator pi = shape.getPathIterator(null, 1); !pi.isDone(); pi
                .next()) {
            switch (pi.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_LINETO:
                list.add(coords[0]);
                list.add(coords[1]);
            }
        }
    }

    /**
     * Compute the intersection of two line segments.
     * 
     * @param a
     *            the first line segment
     * @param b
     *            the second line segment
     * @param intersect
     *            a Point in which to store the intersection point
     * @return the intersection code. One of {@link #NO_INTERSECTION},
     *         {@link #COINCIDENT}, or {@link #PARALLEL}.
     */
    public static int intersectLineLine(Line2D a, Line2D b, Point2D intersect) {
        double a1x = a.getX1(), a1y = a.getY1();
        double a2x = a.getX2(), a2y = a.getY2();
        double b1x = b.getX1(), b1y = b.getY1();
        double b2x = b.getX2(), b2y = b.getY2();
        return intersectLineLine(
                a1x,
                a1y,
                a2x,
                a2y,
                b1x,
                b1y,
                b2x,
                b2y,
                intersect);
    }

    /**
     * Compute the intersection of two line segments.
     * 
     * @param a1x
     *            the x-coordinate of the first endpoint of the first line
     * @param a1y
     *            the y-coordinate of the first endpoint of the first line
     * @param a2x
     *            the x-coordinate of the second endpoint of the first line
     * @param a2y
     *            the y-coordinate of the second endpoint of the first line
     * @param b1x
     *            the x-coordinate of the first endpoint of the second line
     * @param b1y
     *            the y-coordinate of the first endpoint of the second line
     * @param b2x
     *            the x-coordinate of the second endpoint of the second line
     * @param b2y
     *            the y-coordinate of the second endpoint of the second line
     * @param intersect
     *            a Point in which to store the intersection point
     * @return the intersection code. One of {@link #NO_INTERSECTION},
     *         {@link #COINCIDENT}, or {@link #PARALLEL}.
     */
    public static int intersectLineLine(
            double a1x,
            double a1y,
            double a2x,
            double a2y,
            double b1x,
            double b1y,
            double b2x,
            double b2y,
            Point2D intersect) {
        double ua_t = (b2x - b1x) * (a1y - b1y) - (b2y - b1y) * (a1x - b1x);
        double ub_t = (a2x - a1x) * (a1y - b1y) - (a2y - a1y) * (a1x - b1x);
        double u_b = (b2y - b1y) * (a2x - a1x) - (b2x - b1x) * (a2y - a1y);

        if (u_b != 0) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
                intersect.setLocation(a1x + ua * (a2x - a1x), a1y + ua
                        * (a2y - a1y));
                return 1;
            }
            else {
                return NO_INTERSECTION;
            }
        }
        else {
            return (ua_t == 0 || ub_t == 0 ? COINCIDENT : PARALLEL);
        }
    }

    /**
     * Compute the intersection of a line and a rectangle.
     * 
     * @param a1
     *            the first endpoint of the line
     * @param a2
     *            the second endpoint of the line
     * @param r
     *            the rectangle
     * @param pts
     *            a length 2 or greater array of points in which to store the
     *            results
     * @return the intersection code. One of {@link #NO_INTERSECTION},
     *         {@link #COINCIDENT}, or {@link #PARALLEL}.
     */
    public static int intersectLineRectangle(
            Point2D a1,
            Point2D a2,
            Rectangle2D r,
            Point2D[] pts) {
        double a1x = a1.getX(), a1y = a1.getY();
        double a2x = a2.getX(), a2y = a2.getY();
        double mxx = r.getMaxX(), mxy = r.getMaxY();
        double mnx = r.getMinX(), mny = r.getMinY();

        if (pts[0] == null)
            pts[0] = new Point2D.Double();
        if (pts[1] == null)
            pts[1] = new Point2D.Double();

        int i = 0;
        if (intersectLineLine(mnx, mny, mxx, mny, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        if (intersectLineLine(mxx, mny, mxx, mxy, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        if (i == 2)
            return i;
        if (intersectLineLine(mxx, mxy, mnx, mxy, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        if (i == 2)
            return i;
        if (intersectLineLine(mnx, mxy, mnx, mny, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        return i;
    }

    /**
     * Compute the intersection of a line and a rectangle.
     * 
     * @param l
     *            the line
     * @param r
     *            the rectangle
     * @param pts
     *            a length 2 or greater array of points in which to store the
     *            results
     * @return the intersection code. One of {@link #NO_INTERSECTION},
     *         {@link #COINCIDENT}, or {@link #PARALLEL}.
     */
    public static int intersectLineRectangle(
            Line2D l,
            Rectangle2D r,
            Point2D[] pts) {
        double a1x = l.getX1(), a1y = l.getY1();
        double a2x = l.getX2(), a2y = l.getY2();
        double mxx = r.getMaxX(), mxy = r.getMaxY();
        double mnx = r.getMinX(), mny = r.getMinY();

        if (pts[0] == null)
            pts[0] = new Point2D.Double();
        if (pts[1] == null)
            pts[1] = new Point2D.Double();

        int i = 0;
        if (intersectLineLine(mnx, mny, mxx, mny, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        if (intersectLineLine(mxx, mny, mxx, mxy, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        if (i == 2)
            return i;
        if (intersectLineLine(mxx, mxy, mnx, mxy, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        if (i == 2)
            return i;
        if (intersectLineLine(mnx, mxy, mnx, mny, a1x, a1y, a2x, a2y, pts[i]) > 0)
            i++;
        return i;
    }

    /**
     * Computes the 2D convex hull of a set of points using Graham's scanning
     * algorithm. The algorithm has been implemented as described in Cormen,
     * Leiserson, and Rivest's Introduction to Algorithms.
     * 
     * The running time of this algorithm is O(n log n), where n is the number
     * of input points.
     * 
     * @param pts
     *            the input points in [x0,y0,x1,y1,...] order
     * @param len
     *            the length of the pts array to consider (2 * #points)
     * @return the convex hull of the input points
     */
    public static double[] convexHull(double[] pts, int len) {
        if (len < 6) {
            return null;
            // throw new IllegalArgumentException(
            // "Input must have at least 3 points");
        }
        int plen = len / 2 - 1;
        float[] angles = new float[plen];
        int[] idx = new int[plen];
        int[] stack = new int[len / 2];
        return convexHull(pts, len, angles, idx, stack);
    }

    /**
     * Computes the 2D convex hull of a set of points using Graham's scanning
     * algorithm. The algorithm has been implemented as described in Cormen,
     * Leiserson, and Rivest's Introduction to Algorithms.
     * 
     * The running time of this algorithm is O(n log n), where n is the number
     * of input points.
     * 
     * @param pts the points
     * @param len number of points
     * @param angles angles
     * @param idx the stack index
     * @param stack the stack
     * @return the convex hull of the input points
     */
    public static double[] convexHull(
            double[] pts,
            int len,
            float[] angles,
            int[] idx,
            int[] stack) {
        // check arguments
        int plen = len / 2 - 1;
        if (len < 6) {
            throw new IllegalArgumentException(
                    "Input must have at least 3 points");
        }
        if (angles.length < plen || idx.length < plen || stack.length < len / 2) {
            throw new IllegalArgumentException(
                    "Pre-allocated data structure too small");
        }

        int i0 = 0;
        // find the starting ref point: leftmost point with the minimum y coord
        for (int i = 2; i < len; i += 2) {
            if (pts[i + 1] < pts[i0 + 1]) {
                i0 = i;
            }
            else if (pts[i + 1] == pts[i0 + 1]) {
                i0 = (pts[i] < pts[i0] ? i : i0);
            }
        }

        // calculate polar angles from ref point and sort
        for (int i = 0, j = 0; i < len; i += 2) {
            if (i == i0)
                continue;
            angles[j] = (float) Math.atan2(pts[i + 1] - pts[i0 + 1], pts[i]
                    - pts[i0]);
            idx[j++] = i;
        }
        sort(angles, idx, plen);

        // toss out duplicated angles
        float angle = angles[0];
        int ti = 0, tj = idx[0];
        for (int i = 1; i < plen; i++) {
            int j = idx[i];
            if (angle == angles[i]) {
                // keep whichever angle corresponds to the most distant
                // point from the reference point
                double x1 = pts[tj] - pts[i0];
                double y1 = pts[tj + 1] - pts[i0 + 1];
                double x2 = pts[j] - pts[i0];
                double y2 = pts[j + 1] - pts[i0 + 1];
                double d1 = x1 * x1 + y1 * y1;
                double d2 = x2 * x2 + y2 * y2;
                if (d1 >= d2) {
                    idx[i] = -1;
                }
                else {
                    idx[ti] = -1;
                    angle = angles[i];
                    ti = i;
                    tj = j;
                }
            }
            else {
                angle = angles[i];
                ti = i;
                tj = j;
            }
        }

        // initialize our stack
        int sp = 0;
        stack[sp++] = i0;
        int j = 0;
        for (int k = 0; k < 2; j++) {
            if (idx[j] != -1) {
                stack[sp++] = idx[j];
                k++;
            }
        }

        // do graham's scan
        for (; j < plen; j++) {
            if (idx[j] == -1)
                continue; // skip tossed out points
            while (isNonLeft(i0, stack[sp - 2], stack[sp - 1], idx[j], pts)) {
                sp--;
            }
            stack[sp++] = idx[j];
        }

        // construct the hull
        double[] hull = new double[2 * sp];
        for (int i = 0; i < sp; i++) {
            hull[2 * i] = pts[stack[i]];
            hull[2 * i + 1] = pts[stack[i] + 1];
        }

        return hull;
    }

    /**
     * Convex hull helper method for detecting a non left turn about 3 points
     */
    private static boolean isNonLeft(
            int i0,
            int i1,
            int i2,
            int i3,
            double[] pts) {
        double l1, l2, l4, l5, l6, angle1, angle2, angle;

        l1 = Math.sqrt(Math.pow(pts[i2 + 1] - pts[i1 + 1], 2)
                + Math.pow(pts[i2] - pts[i1], 2));
        l2 = Math.sqrt(Math.pow(pts[i3 + 1] - pts[i2 + 1], 2)
                + Math.pow(pts[i3] - pts[i2], 2));
        l4 = Math.sqrt(Math.pow(pts[i3 + 1] - pts[i0 + 1], 2)
                + Math.pow(pts[i3] - pts[i0], 2));
        l5 = Math.sqrt(Math.pow(pts[i1 + 1] - pts[i0 + 1], 2)
                + Math.pow(pts[i1] - pts[i0], 2));
        l6 = Math.sqrt(Math.pow(pts[i2 + 1] - pts[i0 + 1], 2)
                + Math.pow(pts[i2] - pts[i0], 2));

        angle1 = Math.acos(((l2 * l2) + (l6 * l6) - (l4 * l4)) / (2 * l2 * l6));
        angle2 = Math.acos(((l6 * l6) + (l1 * l1) - (l5 * l5)) / (2 * l6 * l1));

        angle = (Math.PI - angle1) - angle2;

        if (angle <= 0.0) {
            return (true);
        }
        else {
            return (false);
        }
    }

    /**
     * Sort two arrays simultaneously, using the sort order of the values in the
     * first array to determine the sort order for both arrays.
     * 
     * @param a
     *            the array to sort by
     * @param b
     *            the array to re-arrange based on the sort order of the first
     *            array.
     * @param length
     *            the length of the range to be sorted
     */
    private static final void sort(float[] a, int[] b, int length) {
        mergesort(a, b, 0, length - 1);
    }

    private static final void insertionsort(float[] a, int[] b, int p, int r) {
        for (int j = p + 1; j <= r; ++j) {
            float key = a[j];
            int val = b[j];
            int i = j - 1;
            while (i >= p && a[i] > key) {
                a[i + 1] = a[i];
                b[i + 1] = b[i];
                --i;
            }
            a[i + 1] = key;
            b[i + 1] = val;
        }
    }

    private static final int SORT_THRESHOLD = 30;

    protected static final void mergesort(float[] a, int[] b, int p, int r) {
        if (p >= r) {
            return;
        }
        if (r - p + 1 < SORT_THRESHOLD) {
            insertionsort(a, b, p, r);
        }
        else {
            int q = (p + r) / 2;
            mergesort(a, b, p, q);
            mergesort(a, b, q + 1, r);
            merge(a, b, p, q, r);
        }
    }

    protected static final void merge(float[] a, int[] b, int p, int q, int r) {
        float[] t = new float[r - p + 1];
        int[] v = new int[r - p + 1];
        int i, p1 = p, p2 = q + 1;
        for (i = 0; p1 <= q && p2 <= r; ++i) {
            if (a[p1] < a[p2]) {
                v[i] = b[p1];
                t[i] = a[p1++];
            }
            else {
                v[i] = b[p2];
                t[i] = a[p2++];
            }
        }
        for (; p1 <= q; ++p1, ++i) {
            v[i] = b[p1];
            t[i] = a[p1];
        }
        for (; p2 <= r; ++p2, ++i) {
            v[i] = b[p2];
            t[i] = a[p2];
        }
        for (i = 0, p1 = p; i < t.length; i++, p1++) {
            b[p1] = v[i];
            a[p1] = t[i];
        }
    }
}
