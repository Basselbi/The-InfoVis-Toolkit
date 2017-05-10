#include "adtgraph.h"
#include "gsearch.h"


vertex bfs (vertex root) {

   static queue	q;
   vertex	v, u;
   edge		e;

   if (root) {
      newqueue (q);
      if (root > 0) {
	 for_all_vertices (v)
	    vi[v].in = 0;
      }
      else
	 root = -root;
      enqueue (q, root);
      vi[root].in = 1;
   }
   if (empty (q))
      return 0;
   dequeue (q, v);
   for_all_incident_edges (v, e) {
      u = that_vertex (e);
      if (!vi[u].in) {
	 enqueue (q, u);
	 vi[u].in = vi[v].in + 1;
      }
   }
   return v;
}


vertex dfs (vertex root) {

   static stack	s;
   vertex	v, u;
   edge		e;

   if (root) {
      newstack (s);
      if (root > 0) {
	 for_all_vertices (v)
	    vi[v].in = 0;
      }
      else
	 root = -root;
      push (s, root);
      vi[root].in = 1;
   }
   if (empty (s))
      return 0;
   pop (s, v);
   for_all_incident_edges (v, e) {
      u = that_vertex (e);
      if (!vi[u].in) {
	 push (s, u);
	 vi[u].in = vi[v].in + 1;
      }
   }
   return v;
}


vertex graph_center (void) {

   vertex	c = 1, u = 1, v, w;
   int		h;

   h = number_vertices + 1;
   for_all_vertices (w) {
      v = bfs (w);
      while (v && vi[v].in < h) {
	 u = v;
	 v = bfs (0);
      }
      if (vi[u].in < h) {
	 h = vi[u].in;
	 c = w;
   }  }
   return c;
}


int graph_diameter (void) {

   vertex	u = 1, v = 1, w;
   int		h;

   h = 0;
   for_all_vertices (w) {
      v = bfs (w);
      while (v) {
	 u = v;
	 v = bfs (0);
      }
      if (vi[u].in - 1 > h)
	 h = vi[u].in - 1;
   }
   return h;
}

