#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <stdio.h>

#include "global.h"
#include "adtgraph.h"
#include "gsearch.h"
#include "embedder.h"
#include "geometry.h"


unsigned long 	iteration;
scalar		temperature;
vector		center;
scalar		maxtemp;
float		oscillation, rotation;
float		i_maxtemp	= IMAXTEMPDEF;
float		a_maxtemp	= AMAXTEMPDEF;
float		o_maxtemp	= OMAXTEMPDEF;
float		i_starttemp	= ISTARTTEMPDEF;
float		a_starttemp	= ASTARTTEMPDEF;
float		o_starttemp	= OSTARTTEMPDEF;
float		i_finaltemp	= IFINALTEMPDEF;
float		a_finaltemp	= AFINALTEMPDEF;
float		o_finaltemp	= OFINALTEMPDEF;
float		i_maxiter	= IMAXITERDEF;
float		a_maxiter	= AMAXITERDEF;
float		o_maxiter	= OMAXITERDEF;
float		i_gravity	= IGRAVITYDEF;
float		i_oscillation	= IOSCILLATIONDEF;
float		i_rotation	= IROTATIONDEF;
float		i_shake		= ISHAKEDEF;
float		a_gravity	= AGRAVITYDEF;
float		a_oscillation	= AOSCILLATIONDEF;
float		a_rotation	= AROTATIONDEF;
float		a_shake		= ASHAKEDEF;
float		o_gravity	= OGRAVITYDEF;
float		o_oscillation	= OOSCILLATIONDEF;
float		o_rotation	= OROTATIONDEF;
float		o_shake		= OSHAKEDEF;


vertex select (void) {

   register vertex	u, v;
   register int 	n;
   static vertex	map[VMAX+1];

   if (iteration == 0)
      for_all_vertices (v)
	 map[v] = v;
   n = number_vertices - iteration % number_vertices;
   v = 1 + rand () % n;
   u = map[v]; map[v] = map[n]; map[n] = u;
   return u;
}


void vertexdata_init (const float starttemp) {

   vertex	v;

   temperature = 0;
   center.x = center.y = 0;
   for_all_vertices (v) {
      vi[v].heat = starttemp * ELEN;
      temperature += vi[v].heat * vi[v].heat;
      vi[v].imp.x = vi[v].imp.y = 0;
      vi[v].dir = 0;
      vi[v].mass = 1 + vi[v].degree / 3;
      center.x += vi[v].pos.x;
      center.y += vi[v].pos.y;
   }
   srand ((unsigned) time (NULL));
}


void displace (const vertex v, vector i) {

   register scalar		t, n;
   register vector		*imp;

   if (i.x || i.y) {
      n = MAX (ABS (i.x), ABS (i.y)) / 16384L;
      if (n > 1) {
	 i.x /= n;
	 i.y /= n;
      }
      t = vi[v].heat;
      n = NORM2 (i.x, i.y);
      i.x = i.x * t / n;
      i.y = i.y * t / n;
      vi[v].pos.x += i.x;
      vi[v].pos.y += i.y;
      center.x += i.x;
      center.y += i.y;
      imp = &vi[v].imp;
      n = t * NORM2 (imp->x, imp->y);
      if (n) {
	 temperature -= t * t;
	 t += t * oscillation * (i.x * imp->x + i.y * imp->y) / n;
	 t = MIN (t, maxtemp);
	 vi[v].dir += rotation * (i.x * imp->y - i.y * imp->x) / n;
	 t -= t * ABS (vi[v].dir) / number_vertices;
	 t = MAX (t, 2);
	 temperature += t * t;
	 vi[v].heat = t;
      }
      *imp = i;
}  }


vector i_impulse (const vertex v) {

   vertex		u;
   edge			e;
   register vector	i, d;
   register scalar	n;
   vector		p = vi[v].pos;

   n = i_shake * ELEN;
   i.x = rand () % (2 * n + 1) - n;
   i.y = rand () % (2 * n + 1) - n;
   i.x += (center.x / number_vertices - p.x) * vi[v].mass * i_gravity;
   i.y += (center.y / number_vertices - p.y) * vi[v].mass * i_gravity;

   for_all_vertices (u)
      if (vi[u].in > 0) {
	 d.x = p.x - vi[u].pos.x;
	 d.y = p.y - vi[u].pos.y;
	 n = d.x * d.x + d.y * d.y;
	 if (n) {
	    i.x += d.x * ELENSQR / n;
	    i.y += d.y * ELENSQR / n;
      }  }
   for_all_incident_edges (v, e) {
      u = that_vertex (e);
      if (vi[u].in > 0) {
	 d.x = p.x - vi[u].pos.x;
	 d.y = p.y - vi[u].pos.y;
	 n = (d.x * d.x + d.y * d.y) / vi[v].mass;
	 n = MIN (n, MAXATTRACT);
	 i.x -= d.x * n / ELENSQR;
	 i.y -= d.y * n / ELENSQR;
   }  }
   return i;
}


void insert (void) {

   vertex 	u, v, w, i;
   edge		e;
   int		d;

   vertexdata_init (i_starttemp);
   oscillation = i_oscillation;
   rotation = i_rotation;
   maxtemp = i_maxtemp * ELEN;
   v = graph_center ();
   for_all_vertices (u)
      vi[u].in = 0;
   vi[v].in = -1;
   for_all_vertices (i) {
      d = 0;
      for_all_vertices (u)
	 if (vi[u].in < d) {
	    d = vi[u].in;
	    v = u;
	 }
      vi[v].in = 1;
      for_all_incident_edges (v, e) {
	 u = that_vertex (e);
	 if (vi[u].in <= 0)
	    vi[u].in --;
      }
      vi[v].pos.x = vi[v].pos.y = 0;
      if (i > 1) {
	 d = 0;
	 for_all_incident_edges (v, e) {
	    w = that_vertex (e);
	    if (vi[w].in > 0) {
	       vi[v].pos.x += vi[w].pos.x;
	       vi[v].pos.y += vi[w].pos.y;
	       d ++;
	 }  }
	 if (d > 1) {
	    vi[v].pos.x /= d;
	    vi[v].pos.y /= d;
	 }
	 d = 0;
	 while ((d++ < i_maxiter) && (vi[v].heat > i_finaltemp * ELEN))
	    displace (v, i_impulse (v));
}  }  }


vector a_impulse (const vertex v) {

   vertex		u;
   edge			e;
   register vector	i, d;
   register scalar	n;
   vector		p = vi[v].pos;

   n = a_shake * ELEN;
   i.x = rand () % (2 * n + 1) - n;
   i.y = rand () % (2 * n + 1) - n;
   i.x += (center.x / number_vertices - p.x) * vi[v].mass * a_gravity;
   i.y += (center.y / number_vertices - p.y) * vi[v].mass * a_gravity;
   for_all_vertices (u) {
      d.x = p.x - vi[u].pos.x;
      d.y = p.y - vi[u].pos.y;
      n = d.x * d.x + d.y * d.y;
      if (n) {
	 i.x += d.x * ELENSQR / n;
	 i.y += d.y * ELENSQR / n;
   }  }
   for_all_incident_edges (v, e) {
      u = that_vertex (e);
      d.x = p.x - vi[u].pos.x;
      d.y = p.y - vi[u].pos.y;
      n = (d.x * d.x + d.y * d.y) / vi[v].mass;
      n = MIN (n, MAXATTRACT);
      i.x -= d.x * n / ELENSQR;
      i.y -= d.y * n / ELENSQR;
   }
   return i;
}


void a_round (void) {

   int		i;
   vertex	v;

   for (i = 0; i < number_vertices; i ++) {
      v = select ();
      displace (v, a_impulse (v));
      iteration ++;
}  }


void arrange (int (*intrrpt) (void)) {

   scalar		stop_temperature;
   unsigned long	stop_iteration;

   vertexdata_init (a_starttemp);
   oscillation = a_oscillation;
   rotation = a_rotation;
   maxtemp = a_maxtemp * ELEN;
   stop_temperature = a_finaltemp * a_finaltemp * ELENSQR * number_vertices;
   stop_iteration = a_maxiter * number_vertices * number_vertices;
   iteration = 0;
   while (temperature > stop_temperature && iteration < stop_iteration) {
      a_round ();
      if (intrrpt)
	 if (intrrpt ())
	    stop_iteration = iteration;
   }
}


vector o_impulse (const vertex v) {

   vertex		u, w;
   edge			e;
   register vector	i, d;
   register scalar	n;
   vector		p = vi[v].pos;

   n = o_shake * ELEN;
   i.x = rand () % (2 * n + 1) - n;
   i.y = rand () % (2 * n + 1) - n;
   i.x += (center.x / number_vertices - p.x) * vi[v].mass * o_gravity;
   i.y += (center.y / number_vertices - p.y) * vi[v].mass * o_gravity;
   for_all_edges (e) {
      u = this_vertex (e);
      w = that_vertex (e);
      if (u != v && w != v) {
	 d.x = (vi[u].pos.x + vi[w].pos.x) / 2 - p.x;
	 d.y = (vi[u].pos.y + vi[w].pos.y) / 2 - p.y;
	 n = d.x * d.x + d.y * d.y;
	 if (n < 8 * ELENSQR) {
	    d = EVdistance (e, v);
	    d.x -= p.x;
	    d.y -= p.y;
	    n = d.x * d.x + d.y * d.y;
	 }
	 if (n) {
	    i.x -= d.x * ELENSQR / n;
	    i.y -= d.y * ELENSQR / n;
      }  }
      else {
	 if (u == v)
	    u = w;
	 d.x = p.x - vi[u].pos.x;
	 d.y = p.y - vi[u].pos.y;
	 n = (d.x * d.x + d.y * d.y) / vi[v].mass;
	 n = MIN (n, MAXATTRACT);
	 i.x -= d.x * n / ELENSQR;
	 i.y -= d.y * n / ELENSQR;
   }  }
   return i;
}


void o_round (void) {

   int		i;
   vertex	v;

   for (i = 0; i < number_vertices; i ++) {
      v = select ();
      displace (v, o_impulse (v));
      iteration ++;
}  }


void optimize (int (*intrrpt) (void)) {

   scalar		stop_temperature;
   unsigned long	stop_iteration;

   vertexdata_init (o_starttemp);
   oscillation = o_oscillation;
   rotation = o_rotation;
   maxtemp = o_maxtemp * ELEN;
   stop_temperature = o_finaltemp * o_finaltemp * ELENSQR * number_vertices;
   stop_iteration = o_maxiter * number_vertices * number_vertices;
   while (temperature > stop_temperature && iteration < stop_iteration) {
      o_round ();
      if (intrrpt)
	 if (intrrpt ())
	    stop_iteration = iteration;
   }
}


void randomize_graph (void) {

   vertex v;
   long   scale = ELEN * sqrt (number_vertices) / 2;

   for_all_vertices (v) {
      vi[v].pos.x = (scalar) rand () % (2 * scale + 1) - scale / 2;
      vi[v].pos.y = (scalar) rand () % (2 * scale + 1) - scale / 2;
}  }

