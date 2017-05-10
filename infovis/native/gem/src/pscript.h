#ifndef PSCRIPT_H
#define PSCRIPT_H

#include <stdio.h>

#define	PSBALLRADIUS	 3
#define	PSBALLRADIUSTEXT "3"
#define MAXHUE		 0.7

/* prototype */

void write_postscript (FILE *, float maxtemp, int, int);

#endif
