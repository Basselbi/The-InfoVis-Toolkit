#include "global.h"
#include "adtgraph.h"
#include "gscale.h"

void scale_graph (int left, int top, int width, int height, int radius) {

   vertex 	v;
   scalar	minx, maxx, miny, maxy, p, q;

   maxx = minx = vi[1].pos.x;
   maxy = miny = vi[1].pos.y;
   for_all_vertices (v) {
      minx = MIN (minx, vi[v].pos.x);
      maxx = MAX (maxx, vi[v].pos.x);
      miny = MIN (miny, vi[v].pos.y);
      maxy = MAX (maxy, vi[v].pos.y);
   }
   if (maxx <= minx) maxx = minx + 1;
   if (maxy <= miny) maxy = miny + 1;
   p = width - 2 * radius;
   q = maxx - minx;
   if ((height - 2 * radius) * q < (maxy - miny) * p) {
      p = height - 2 * radius;
      q = maxy - miny;
   }
   for_all_vertices (v) {
      vi[v].x = (vi[v].pos.x - minx) * p / q + radius + left;
      vi[v].y = (vi[v].pos.y - miny) * p / q + radius + top;
}  }
