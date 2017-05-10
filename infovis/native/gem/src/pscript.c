#include <stdio.h>
#include <stdlib.h>

#include "global.h"
#include "adtgraph.h"
#include "pscript.h"


const char *PSheader =
   "%%!PS-Adobe-3.0 EPSF-3.0\n%%%%BoundingBox: 0 0 %d %d\n"
   "/M{moveto}def/N{newpath}def/L{lineto stroke N}def\n"
   "/C{1 1 sethsbcolor}def/B{"PSBALLRADIUSTEXT" 0 360 arc fill N}def N\n";


void write_postscript (FILE *f, float maxtemp, int width, int height) {

   edge         e;
   vertex       v, w;

   fprintf (f, PSheader, width, height);
   for_all_edges (e) {
      v = this_vertex (e);
      w = that_vertex (e);
      fprintf (f, "%ld %ld M %ld %ld L\n",
	 (long) vi[v].x, (long) vi[v].y,
	 (long) vi[w].x, (long) vi[w].y);
   }
   if (maxtemp) {
      maxtemp *= ELEN;
      for_all_vertices (v)
	 fprintf (f, "%0.2f C %ld %ld B\n",
	    (float) MAX (0, MAXHUE - vi[v].heat / maxtemp),
	    (long) vi[v].x, (long) vi[v].y);
   } else {
      for_all_vertices (v)
	 fprintf (f, "%ld %ld B\n", (long) vi[v].x, (long) vi[v].y);
   }
}
