#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

#include "global.h"
#include "adtgraph.h"
#include "graphed.h"
#include "gscale.h"
#include "pscript.h"


int main (int argc, char *argv[]) {

   int	i, x, w = 0, h = 0;
   char	*a;

   for (i = 1; i < argc; i ++) {
      a = argv[i];
      if (a[0] == '-') {
	 terminate ("Usage: gps <GraphEd >PostScript [-h] [width [height]]\n");
	 exit (0);
      }
      if (isdigit (a[0])) {
	 x = atoi (a);
	 if (x < 16) {
	    terminate ("Error: Width/Height out of range\n");
	    exit (0);
	 }
	 if (w == 0)
	    w = x;
	 else
	    if (h == 0)
	       h = x;
	    else
	       fputs ("Warning: Too many numbers", stderr);
   }  }
   if (w == 0)
      w = 480;
   if (h == 0)
      h = 480;
   add_termproc (exit_graph);
   init_graph ();
   read_graphed (stdin);
   scale_graph (0, 0, w, h, PSBALLRADIUS);
   write_postscript (stdout, 0, w, h);
   terminate ("");
   return 0;
}
