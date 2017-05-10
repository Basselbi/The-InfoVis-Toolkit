#include <ctype.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "embedder.h"
#include "config.h"


struct {
   char		*token;
   float	minval, maxval;
   float	*dest;
}		para[] = {

   {"INSERT_MAXTEMP", 	0.01, 10, &i_maxtemp},
   {"INSERT_STARTTEMP", 	0.01, 10, &i_starttemp},
   {"INSERT_FINALTEMP", 	0.01, 0.5, &i_finaltemp},
   {"INSERT_MAXITER", 	1, 100, &i_maxiter},
   {"INSERT_GRAVITY", 	0, 1, &i_gravity},
   {"INSERT_OSCILLATION", 0, 2, &i_oscillation},
   {"INSERT_ROTATION", 	0, 2, &i_rotation},
   {"INSERT_SHAKE", 	0, 5, &i_shake},
   {"ARRANGE_MAXTEMP", 	0.01, 10, &a_maxtemp},
   {"ARRANGE_STARTTEMP", 0.01, 10, &a_starttemp},
   {"ARRANGE_FINALTEMP", 0.01, 10, &a_finaltemp},
   {"ARRANGE_MAXITER", 	1, 100, &a_maxiter},
   {"ARRANGE_GRAVITY", 	0, 1, &a_gravity},
   {"ARRANGE_OSCILLATION", 0, 2, &a_oscillation},
   {"ARRANGE_ROTATION", 	0, 2, &a_rotation},
   {"ARRANGE_SHAKE", 	0, 5, &a_shake},
   {"OPTIMIZE_MAXTEMP", 	0.01, 10, &o_maxtemp},
   {"OPTIMIZE_STARTTEMP", 0.01, 10, &o_starttemp},
   {"OPTIMIZE_FINALTEMP", 0.01, 10, &o_finaltemp},
   {"OPTIMIZE_MAXITER", 	1, 100, &o_maxiter},
   {"OPTIMIZE_GRAVITY", 	0, 1, &o_gravity},
   {"OPTIMIZE_OSCILLATION", 0, 2, &o_oscillation},
   {"OPTIMIZE_ROTATION", 0, 2, &o_rotation},
   {"OPTIMIZE_SHAKE", 	0, 5, &o_shake},
   {"", 0, 0, NULL} };


int check_p (char *buf, float val) {

   int	i = 0;

   while (*para[i].token) {
      if (strcmp (buf, para[i].token) == 0) {
	 if (val < para[i].minval || val > para[i].maxval) {
	    fputs ("Error: ", stderr);
	    fputs (buf, stderr);
	    fputs (" value out of valid range\n", stderr);
	 }
	 else
	    *para[i].dest = val;
	 return 1;
      }
      i ++;
   }
   return 0;
}


int read_config (FILE *f) {

   char		buffer1[80], buffer2[15];
   int		x, i;
   float	val;

   while (1) {
      do
	 x = fgetc (f);
      while (x != EOF && x != '#');
      if (x == EOF)
	 break;

      i = 0;
      do {
	 x = fgetc (f);
	 buffer1[i++] = x;
      } while (x != EOF && !isspace (x));
      buffer1[i-1] = 0;
      do
	 x = fgetc (f);
      while (x != EOF && !isdigit (x) && x != '.' && x != '#');
      if (!isdigit (x) && x != '.') {
	 fputs ("Warning: Invalid value for parameter ", stderr);
	 fputs (buffer1, stderr);
	 fputs ("\n", stderr);
      }
      else {
	 i = 0;
	 do {
	    buffer2[i++] = x;
	    x = fgetc (f);
	 } while ((x != EOF) && ((isdigit (x)) || (x == '.')));
	 buffer2[i] = 0;
	 if (x != EOF) {
	    sscanf (buffer2, "%f", &val);
	    if (!check_p (buffer1, val)) {
	       fputs ("Error: Unknown parameter ", stderr);
	       fputs (buffer1, stderr);
	       fputs ("\n", stderr);
   }  }  }  }
   return 0;
}
