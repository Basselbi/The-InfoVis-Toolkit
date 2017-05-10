#include <stdlib.h>
#include <stdio.h>

#include "global.h"
#include "adtgraph.h"

int		number_vertices, number_edges;
vertex		*NODE = NULL;
edge		*FIRST = NULL;
edge		*NEXT = NULL;
vertexinfo	*vi = NULL;


void create_graph (void) {

   vertex	v;
   edge		e;

   for (v = 0; v <= VMAX; v ++)
      FIRST[v] = 0;
   for (e = 0; e <= EMAX; e ++)
      NODE[e] = NEXT[e] = 0;
   number_edges = 0;
   number_vertices = 0;
}


void insert_vertices (unsigned long n) {

   if (number_vertices + n > VMAX)
      terminate ("Graph overflow: Too many vertices\n");
   while (n--) {
      number_vertices ++;
      vi[number_vertices].pos.x = 0;
      vi[number_vertices].pos.y = 0;
      vi[number_vertices].degree = 0;
}  }


void insert_edge (const vertex v, const vertex w) {

   edge		e;

   number_edges ++;
   if (number_edges > EMAX)
      terminate ("Graph overflow: Too many edges\n");
   vi[v].degree ++;
   vi[w].degree ++;
   NODE[-number_edges] = v;
   e = FIRST[v];
   FIRST[v] = number_edges;
   NEXT[number_edges] = e;
   NODE[number_edges] = w;
   e = FIRST[w];
   FIRST[w] = -number_edges;
   NEXT[-number_edges] = e;
}


void init_graph (void) {

   const char	*errormsg = "Not enough memory for graph structures\n";

   NODE = (vertex *) malloc ((2 * EMAX + 1) * sizeof (vertex));
   if (!NODE) {
      terminate (errormsg);
      exit (1);
   }
   NODE += EMAX;
   FIRST = (edge *) malloc ((1 + VMAX) * sizeof (edge));
   if (!FIRST) {
      terminate (errormsg);
      exit (1);
   }
   NEXT = (edge *) malloc ((2 * EMAX + 1) * sizeof (edge));
   if (!NEXT) {
      terminate (errormsg);
      exit (1);
   }
   NEXT += EMAX;
   vi = (vertexinfo *) malloc ((1 + VMAX) * sizeof (vertexinfo));
   if (!vi) {
      terminate (errormsg);
      exit (1);
   }
}


void exit_graph (void) {

   if (vi)
      free (vi);
   if (NODE)
      free (NODE - EMAX);
   if (FIRST)
      free (FIRST);
   if (NEXT)
      free (NEXT - EMAX);
   number_vertices = number_edges = 0;
}
