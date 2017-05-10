/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.ordering;

import infovis.utils.Permutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

/**
 * <b>TSPOrdering</b> computes an ordering using a Traveling Salesman
 * heuristics (TSP) optimizer such as LKH or Concord.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class TSPOrdering implements Ordering {
    /**
     * Name of the LKH program.
     */
    public static final String LKH       = "LKH";

    /**
     * Name of the Concorde program.
     */
    public static final String Concorde  = "Concorde";

    protected String           heuristic = LKH;

    protected boolean          debugging = true;

    /**
     * @return the debugging
     */
    public boolean isDebugging() {
        return debugging;
    }

    /**
     * @param debugging
     *            the debugging to set
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }
    
    /**
     * @return the heuristic
     */
    public String getHeuristic() {
        return heuristic;
    }
    
    /**
     * @param heuristic the heuristic to set
     */
    public void setHeuristic(String heuristic) {
        if (heuristic == null) return;
        if (heuristic.equals(LKH) || heuristic.equals(Concorde))
            this.heuristic = heuristic;
        else
            throw new IllegalArgumentException("Only LKH and Concorde are suppored");
    }

    /**
     * {@inheritDoc}
     */
    public Permutation computeOrdering(DoubleMatrix2D distanceMatrix) {
        // write matrix file
        File dist = writeTSPLIBMatrix(distanceMatrix);

        IntArrayList tmpOrder = null;
        if (heuristic.equalsIgnoreCase(LKH)) {
            File tour = writeLKHInitialTour(distanceMatrix.rows());
            File par = writeLKHParameters(dist, tour);
            tmpOrder = runLKH(par);
            if (!isDebugging()) {
                tour.delete();
                par.delete();
            }
        }
        else if (heuristic.equalsIgnoreCase(Concorde)) {
            tmpOrder = runConcorde(dist, distanceMatrix.rows());
        }
        else {
            throw new UnsupportedOperationException("Cannot call heuristic "
                    + heuristic);
        }
        if (!isDebugging()) {
            dist.delete();
        }
        if (tmpOrder == null) {
            return null;
        }
        IntArrayList order = new IntArrayList();
        int pivot = tmpOrder.indexOf(distanceMatrix.rows());
        order.addAllOfFromTo(tmpOrder, pivot + 1, tmpOrder.size() - 1);
        if (pivot != 0) {
            order.addAllOfFromTo(tmpOrder, 0, pivot - 1);
        }

        return new Permutation(order);
    }

    protected IntArrayList runConcorde(File dist, int size) {
        IntArrayList ret = null;
        if (isDebugging()) {
            System.out.println("    Running Concorde...");
        }
        try {
            String path = dist.getCanonicalPath();
            path = path.replace('\\', '/');
            File outfile = File.createTempFile("out", ".tour");
            String out = outfile.getCanonicalPath();
            out = out.replace('\\', '/');
            String args[] = { "concorde", "-x", "-o", out, path };
            String sizeString = Integer.toString(size + 1);

            Process proc = Runtime.getRuntime().exec(args);
            // in = proc.getInputStream();
            // InputStream err = proc.getErrorStream();
            String line = null;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            String currentLine = null;
            while ((currentLine = in.readLine()) != null) {
//                System.out.println(currentLine);
            }
            BufferedReader err = new BufferedReader(
                    new InputStreamReader(proc.getErrorStream()));
            while ((currentLine = err.readLine()) != null) {
                System.err.println(currentLine);
            }
            // int term =
            // proc.waitFor();
            // proc.destroy();
            FileReader fr = new FileReader(outfile);
            in = new BufferedReader(fr);
            while ((line = in.readLine()) != null) {
                if (line.equals(sizeString)) {
                    ret = new IntArrayList();
                }
                else if (ret != null) {
                    for (StringTokenizer tok = new StringTokenizer(line); tok
                            .hasMoreTokens();) {
                        int v = Integer.parseInt(tok.nextToken());
                        if (v == -1)
                            break;
                        ret.add(v);
                    }
                }
            }
            fr.close();
            outfile.delete();
            // if (term != 0) {
            // while ((c = err.read()) != -1) {
            // err((char)c);
            // }
            // throw new RuntimeException("Process Concorde finished with exit
            // code
            // "+term+": "+buf);
            // }

        } catch (Exception e) {
            System.out.println("runConcorde:: " + e.toString());
        }
        return ret;
    }

    /**
     * Run the LKH program (written in C) to solve the TSP
     */
    protected IntArrayList runLKH(File par) {
        IntArrayList ret = null;
        if (isDebugging()) {
            System.out.println("    Running LKH...");
        }
        try {
            String[] args = { "lkh", par.getAbsolutePath() }; 
            InputStream in = null;
            BufferedReader b = null;

            Process proc = Runtime.getRuntime().exec(args);
            in = proc.getInputStream();
            b = new BufferedReader(new InputStreamReader(in));
            String line = b.readLine();

            while (line != null) {
                line = b.readLine();
                if (line == null)
                    break;
                if (line.startsWith("TOUR_SECTION")) {
                    ret = new IntArrayList();
                }
                else if (ret != null) {
                    int v = Integer.parseInt(line);
                    if (v == -1)
                        break;
                    ret.add(v-1);
                }
            }
        } catch (Exception e) {
            System.out.println("runLKH :: " + e.toString());
        }
        return ret;
    }

    /**
     * Write problem file for LKH program
     * 
     * @param distanceMatrix
     *            the distance matrix between elements
     */
    protected File writeTSPLIBMatrix(DoubleMatrix2D distanceMatrix) {
        double minDist = distanceMatrix.aggregate(Functions.min, Functions.identity); 
        if (minDist < 0) {
            throw new java.lang.IllegalArgumentException("Distance matrix contains a negative value "+minDist);
        }
        int size = distanceMatrix.rows();
        File tmp = null;
        double max = distanceMatrix
                .aggregate(Functions.max, Functions.identity);
        double scale = 1;
        if (max == 0) {
            throw new java.lang.IllegalArgumentException("Distance matrix is null");
        }
        if (max < size) {
            scale = size;
        }
        try {
            tmp = File.createTempFile("dist", ".tsp");
            FileWriter f = new FileWriter(tmp);
            f.write("NAME: DistanceMatrix" + '\n');
            f.write("TYPE: TSP " + '\n');
            f.write("COMMENT: Symetric matrix TSP " + '\n');
            f.write("DIMENSION: " + (size + 1) + '\n');
            f.write("EDGE_WEIGHT_TYPE: EXPLICIT" + '\n');
            f.write("EDGE_WEIGHT_FORMAT: UPPER_ROW" + '\n');
            f.write("EDGE_WEIGHT_SECTION" + '\n');

            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    f.write("\t" + (int) (scale * distanceMatrix.get(i, j)));
                }
                f.write("\t0\n");
            }
            f.write("EOF \n");
            f.close();
        } catch (Exception e) {
            System.out.println("writeLKHMatrix :: " + e.toString());
        }
        return tmp;
    }

    /**
     * Write parameter file for LKH program
     * 
     */
    protected File writeLKHParameters(File dist, File tour) {
        int LKH_TRACELEVEL = 0;
        int LKH_RUNSNUMBER = 2;
        File tmp = null;
        try {
            tmp = File.createTempFile("Par", ".lkh");
            FileWriter f = new FileWriter(tmp); // "Par.Win");
            f.write("PROBLEM_FILE = " + dist.getAbsolutePath() + '\n');
            f.write("TRACE_LEVEL = " + LKH_TRACELEVEL + '\n');
            // f.write("MAX_TRIALS = 100 " +'\n');
            f.write("RUNS = " + LKH_RUNSNUMBER + '\n');
            f.write("INITIAL_TOUR_FILE = " + tour.getAbsolutePath() + '\n');
            // f.write("TOUR_FILE = "+ tmp.getAbsolutePath() +'\n');
            f.close();
        } catch (Exception e) {
            System.out.println("writeLKHParameters :: " + e.toString());
        }
        return tmp;
    }

    /**
     * Write initial tour file to improve for LKH program
     * 
     * @param initialTour
     */
    protected File writeLKHInitialTour(int n) {
        File tmp = null;
        try {
            tmp = File.createTempFile("initialTour", ".tour");
            FileWriter f = new FileWriter(tmp);
            f.write("NAME: initialTour" + '\n');
            f.write("TYPE: TOUR " + '\n');
            f.write("COMMENT: initial tour for tsp problem" + '\n');
            f.write("DIMENSION: " + (n + 1) + '\n'); // take into account the
            // ins. element
            f.write("TOUR_SECTION" + '\n');

            for (int i = 0; i < n + 1; i++) {
                // LKH numbers first vertex as 1 not 0
                f.write(Integer.toString(i + 1) + '\n');
            }
            f.write("-1 \n");
            f.close();
        } catch (Exception e) {
            System.out.println("writeLKHInitialTour :: " + e.toString());
        }
        return tmp;
    }
}
