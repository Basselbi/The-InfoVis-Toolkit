#include <ctype.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "global.h"
#include "adtgraph.h"
#include "graphed.h"
#include "embedder.h"
#include "config.h"
#include "quality.h"


int status (void) {

   fprintf (stderr, "\rRound %ld", iteration / number_vertices);
   return 0;
}


int main (int argc, char *argv[]) {

   FILE			*f = NULL;
   char			*a, gname[GNAMESIZE], *infile = NULL;
   int			benchmark = 0, debug = 0, qual = 0, title = 1, rnd = 0;
   int			i, runs;
   double		avgR = 0, devR = 0;
   double		avgQd = 0, avgQe = 0, avgQv = 0, avgQx = 0, avgQ = 0, devQ = 0;

   add_termproc (exit_graph);
   init_graph ();
   runs = 1;
   for (i = 1; i < argc; i++) {
      a = argv[i];
      if (*a++ == '-') {
	 switch (*a) {
	 case 'h':
	    terminate (
	       "Usage Version 1.0: gem [-b#] [-c*] [-d] [-h] [-q] [-r] [-t] <in >out\n"
	       "-b#     benchmark, # repetitions\n"
	       "-c~     use config file ~\n"
	       "-d      debug info to stderr\n"
	       "-h      help (this)\n"
	       "-q      quality measurement\n"
	       "-r	random initialization\n"
	       "-t      omit title\n"
	       "Written 1995 by Andreas Ludwig (alu) at the University of Karlsruhe (Germany)\n"
	    );
	    exit (0);
	    break;
	 case 'c':
	    f = fopen (a + 1, "rt+");
	    if (!f)
	       fprintf (stderr, "Warning: Cannot open config file %s - using defaults\n", a + 1);
	    break;
	 case 'b':
	    runs = MAX (1, atoi (a + 1));
	    benchmark = 1;
	    break;
	 case 'd':
	    debug = 1;
	    break;
	 case 'q':
	    qual = 1;
	    break;
	 case 'r':
	    rnd = 1;
            srand (time (NULL));
	    break;
	 case 't':
	    title = 0;
	    break;
	 default:
	   fprintf (stderr, "Warning: Ignoring unknown option %s\n", a);
	   break;
	 }
      }
      else {
	infile = argv[i];
      }
   }
   if (!f)
      f = fopen ("gem.cfg", "rt+");
   if (!f)
      fputs ("Warning: Cannot open gem.cfg - using defaults\n", stderr);
   else {
      read_config (f);
      fclose (f);
   }
   if (infile == NULL) {
     f = stdin;
   }
   else {
     fprintf(stderr, "Reading file %s\n", infile);
     f = fopen(infile, "rt+");
     if (f == NULL) {
       fprintf(stderr, "Error: file %s not found\n", infile);
       return -1;
     }
   }
   while (!feof (f)) {
      if (read_graphed (f)) {
	 terminate (NULL);
	 exit (0);
      }
      if (f != stdin) fclose(f);
      if (number_vertices < 1) {
	 fputs ("Empty graph?!\n", stderr);
	 continue;
      }
      if (benchmark) {
	/* avgR = devR = 0; */
	 if (qual)
	    avgQd = avgQe = avgQv = avgQx = avgQ = devQ = 0;
      }
      for (i = 1; i <= runs; i++) {
	 if (debug && benchmark)
	    fprintf (stderr, "Run %d/%d: ", i, runs);
	 if (!qual || benchmark) {
	    if (debug)
	       fprintf (stderr, "Embedding \"%s\" (%d/%d)...\n", graphname,
		  number_vertices, number_edges);
	    if (rnd)
	       randomize_graph ();
	    else if (i_finaltemp < i_starttemp)
	       insert ();
	    if (a_finaltemp < a_starttemp) {
	       if (debug)
		  arrange (status);
	       else
		  arrange (NULL);
	    }
	    if (o_finaltemp < o_starttemp) {
	       if (debug)
		  optimize (status);
	       else
		  optimize (NULL);
	    }
	    if (benchmark) {
	       avgR += iteration / number_vertices;
	       devR += iteration * iteration / number_vertices / number_vertices;
	    }
	 }
	 if (debug && (benchmark || !qual))
	    fputs ("\n", stderr);
	 if (qual) {
	    if (debug)
	       fprintf (stderr, "Examining \"%s\" (%d/%d)...\n", graphname,
		  number_vertices, number_edges);
	    quality ();
	    if (benchmark) {
	       avgQd += Qd;
	       avgQe += Qe;
	       avgQv += Qv;
	       avgQx += Qx;
	       avgQ += Q;
	       devQ += Q * Q;
	    }
	 }
      }
      if (benchmark || qual) {
	 if (benchmark) {
	    avgR /= runs;
	    devR = sqrt (devR / runs - avgR * avgR);
	    if (qual) {
	       avgQd /= runs; avgQe /= runs; avgQv /= runs;
	       avgQx /= runs; avgQ /= runs;
	       devQ = devQ / runs - avgQ * avgQ;
	       devQ = MAX (devQ, 0.0);
	       devQ = sqrt (devQ);
	 }  }
	 strncpy (gname, graphname, 16);
	 if (strlen (gname) < 16)
	    strcat (gname, "                ");
	 if (!benchmark && qual) {
	    if (title)
	       printf ("Graph name       |V|  |E| Diam  Nx  Qd    Qe    Qv    Qx    Q\n");
	    printf ("%.16s %3d %4d %3d%5ld  %5.3lf %5.3lf %5.3lf %5.3lf %5.3lf\n",
	       gname, (int) number_vertices, (int) number_edges, (int) diam,
	       (long) Nx, (double) Qd, (double) Qe, (double) Qv, (double) Qx,
	       (double) Q);
	 }
	 if (benchmark && !qual) {
	    if (title)
	       printf ("Graph name       |V|  |E| Runs  Rounds\n");
	    printf ("%.16s %3d %4d %3d  %4d%3d\n",
	       gname, (int) number_vertices, (int) number_edges,
	       (int) runs, (int) avgR, (int) devR);
	 }
	 if (benchmark && qual) {
	    if (title)
	       printf ("Graph name       |V|  |E| Runs  Rounds  Qd    Qe    Qv    Qx    Q     Dev\n");
	    printf ("%.16s %3d %4d %3d  %4d%3d  %5.3lf %5.3lf %5.3lf %5.3lf %5.3lf %.3lf\n",
	       gname, (int) number_vertices, (int) number_edges,
	       (int) runs, (int) avgR, (int) devR,
	       (double) avgQd, (double) avgQe, (double) avgQv,
	       (double) avgQx, (double) avgQ, (double) devQ);
      }  }
      else
	 write_graphed (stdout);
   }
   terminate (NULL);
   return 0;
}
