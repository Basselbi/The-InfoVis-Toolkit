#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
//#include <glib-1.2/glib.h>

#include "global.h"
#include "adtgraph.h"
#include "graphed.h"


char	graphname[GNAMESIZE];


void write_graphed (FILE *f) {

   vertex       u, v;
   edge		e;

   fprintf (f, "GRAPH \"%s\" = UNDIRECTED \n", graphname);
   for_all_vertices (v) {
     fprintf (f, "%d {$NP %ld %ld$} \"\"", vi[v].label, vi[v].pos.x, vi[v].pos.y);
      for_all_incident_edges (v, e) {
	 u = that_vertex (e);
	 if (u > v)
	   fprintf (f, "\n%d \"\"", vi[u].label);
      }
      fprintf (f, ";\n");
   }
   fprintf (f, "END\n");
}


int next_word (FILE *f, char *buf, int buflen)
{
   int   	i = 0;
   char 	ch = getc (f);

   while (isspace(ch))
      ch = getc (f);

   if (ch == '"') {
      ch = getc (f);
      while (!feof (f) && (ch != '"') && (i < buflen)) {
	 buf[i] = ch;
	 ch = getc (f);
	 i++;
   }  }
   else {
      while (!feof (f) && !isspace (ch) && (i < buflen) && (ch != '"')) {
	 buf[i] = ch;
	 ch = getc (f);
	 i++;
      }
      if (ch == '"')
	 ungetc (ch, f);
   }
   buf[i] = '\0';
   if (feof (f))
      return -1;
   else
      if (isspace (ch))
	 return i;
      else
	 return 0;
}


int next_token (FILE *f, char *buf, int buflen)
{
   int	ret;

   ret = next_word (f, buf, buflen);
   while (strchr (buf, '{')) {
      while (!strchr (buf, '}'))
	 ret = next_word (f, buf, buflen);
      ret = next_word (f, buf, buflen);
   }
   return ret;
}


void read_pos (FILE *f, vertex v)
{
   char	buf[PARSEBUFSIZE];

   next_word (f, buf, PARSEBUFSIZE);
   while (!strstr (buf, "NP"))
      next_word (f, buf, PARSEBUFSIZE);
   next_word (f, buf, PARSEBUFSIZE);
   vi[v].pos.x = atoi (buf);
   next_word (f, buf, PARSEBUFSIZE);
   vi[v].pos.y = atoi (buf);
   while (!strstr (buf, "$}"))
      next_word (f, buf, PARSEBUFSIZE);
}


int read_graphed (FILE *f) {

   char		buf[PARSEBUFSIZE];
   int		i, nw, u, v;
   vertex   	vm, um;
#ifndef HASH
   int		map[VMAX+1];
#else
   hash         map;
#endif

   create_graph ();
   nw = next_token (f, buf, PARSEBUFSIZE);
   while ((nw >= 0) && strcmp ("GRAPH", buf) != 0)
      nw = next_token (f, buf, PARSEBUFSIZE);
   if (nw < 0)
     return 1;

   next_token (f, graphname, PARSEBUFSIZE);
   next_token (f, buf, PARSEBUFSIZE);
   next_token (f, buf, PARSEBUFSIZE);
   if ((strcmp (buf, "DIRECTED") == 0) || (strcmp (buf, "UNDIRECTED") == 0))
      next_token (f, buf, PARSEBUFSIZE);
   while (strcmp (buf, "END") != 0) {
      v = atoi (buf);
#ifdef HASH
      vm = hash_get(hash, v);
      if (vm == 0) {
	 insert_vertices (1);
	 hash_add(hash, v, number_vertices);
	 vm = number_vertices;
      }
      */ 
#else
      for (i = 1; (i <= number_vertices) && (map[i] != v); i++) ;
      if (i > number_vertices) {
	 insert_vertices (1);
	 map[number_vertices] = v;
	 vm = number_vertices;
      }
      else
	 vm = i;
#endif
      vi[vm].label = v;
      read_pos (f, vm);
      next_token (f, buf, PARSEBUFSIZE);
      next_token (f, buf, PARSEBUFSIZE);
      while (!strchr (buf, ';')) {
	 u = atoi (buf);
	 for (i = 1; ((i <= number_vertices) && (map[i] != u)); i++) ;
	 if (i > number_vertices) {
	    insert_vertices (1);
	    map[number_vertices] = u ;
	    um = number_vertices;
	 }
	 else
	    um = i;
	 insert_edge (vm, um);
	 next_token (f, buf, PARSEBUFSIZE);
	 if (!strchr (buf, ';'))
	    next_token (f, buf, PARSEBUFSIZE);
      }
      next_token (f, buf, PARSEBUFSIZE);
   }
   return 0;
}
