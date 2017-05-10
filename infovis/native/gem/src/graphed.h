#ifndef GRAPHED_H
#define GRAPHED_H

#include <stdio.h>

/* BUFFER SIZES */

#define GNAMESIZE		80
#define PARSEBUFSIZE		200

/* global vars */

extern char	graphname[GNAMESIZE]; /* name of graph as seen in file */

/* prototypes */

extern void write_graphed (FILE *); /* write current graph as graphed file */
extern int read_graphed (FILE *); /* read graph from graphed file */

#endif
