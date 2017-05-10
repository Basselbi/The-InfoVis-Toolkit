ABOUT
-----

GEM is an experimental Graph EMbedding program that I wrote as a
semester project during my recent studies at the University of
Karlsruhe.  It reads a GraphEd [1] file from stdin (up to 400 vertices
/ 3000 edges), calculates a straight-line layout using
spring-embedding heuristics and writes a subset of the input graph
(undirected, with vertex positions) to stdout. GEM can also be used to
evaluate several quality functions for a given graph layout and to run
benchmarks.

GPS is a simple GraphEd-to-PostScript converter. It cannot compete
with that of GraphEd [1], but did fine during my studies.

This package is ***PUBLIC DOMAIN***.

As usual absolutely no warranties are given - any use is at your own risk.


CONTENTS OF THIS PACKAGE
------------------------
I admit that the sources are rather sparsely commented. Mail me some
time and I will write a full documentation ;)

readme.txt      (this)

gem.cfg         default config file for gem
gemi.cfg        config file to show insert step ("first guess")
gemo.cfg        config file to use optimization step
nop.cfg         config file to nothing (use with -r option to randomize)

sources/

makefile        for gem and gps

global.h        auxiliary macros, desired edge length
global.c        termination handling procedures
adtgraph.h      definitions for Abstract Data Type GRAPH and vertex data
adtgraph.c      a fast, limited-size, create-only graph representation
graphed.h/c     easy (and dirty) GraphEd file format parsing and creation

gem.c           main() of gem
embedder.h      default parameters for main algorithm
embedder.c      main algorithm as described above
geometry.h/c    fast line segment intersection / nearest point on segment
quality.h       weights for global quality function
quality.c       quality function(s)
gsearch.h/c     simple BFS and DFS algorithms with applications
config.h/c      parameter (.cfg) file parser

gps.c           main() of gps
pscript.h/c     simple PostScript converter
gscale.h/c      scaling functions

examples/

c60.g           c60 molecule (produced by gem)
entring.g       entringer graph (produced by gem)
torus.g         torus (produced by gem)
bintree.g       complete binary tree (produced by gem)
hcube4d.g       4D-hypercube (produced by gem)
comcon15.g      completely connect graph with 15 vertices (produced by gem)
star30.g        star with 30 vertices (produced by gem)
triangle.g      triangular mesh (randomized)

All sources are written in ANSI-C and have been tested on a
PC (using Borland C++) and a Sun4 Workstation (using GCC).


OPTIONS
-------
Options of GEM:

-b123      benchmark, number of repetitions set to 123
-cfoo.cfg  use custom config file foo.cfg
-d         debug (status) output to stderr
-h         help
-q         quality measurements on
-r         init by randomization, do not use vertex insertion
-t         omit title (to create table when called for different graphs)

Examples:

gem <c60.g >c60!.g
gem -d <c60.g >c60!.g
gem -q <c60.g
gem -d -q -b5 <c60.g
gem -d -q -b5 -cgemo.cfg <c60.g


Options of GPS:

-h         help
1234 567   scale output to a bounding box of 1234x567; default is 480x480

Examples:

gps 500 500 <c60.g >c60.eps
cat star30.g | gem -d | gps >star30.eps
cat star30.g | gem -d -cgemo.cfg | gps >star30.eps


ABOUT THE METHOD
----------------
The following short survey should enable you to play with the 
configuration parameters in the *.cfg files. If you want more
details, get a copy of [2].

The spring-embedding heuristic models vertices as repulsive charges
and edges as contracting springs. The simulation of this pseudo-physical
system often results in a nice view of the graph.

The algorithm used adds a gravitational influence which tows the 
vertices towards the current barycenter thus compacting the graph and
accelerating the convergence. Random shakes add some indeterminism 
to resolve instable states.
Each vertex has got a certain "temperature" which stands for the range 
it will move in the next iteration; it is recalculated after each move 
using the current and last direction the vertex moved to. Movements in 
the same direction as before will be considered as a reason to increase 
temperature, other movements will cool it down (the system oscillates or 
rotates).

The algorithm uses up to three passes:
- As a "first guess" it performs a variant of breadth-first-search
  to insert the vertices one by one, hereby simulating its movement several 
  times. The result is a fairly good starting position.
- The main pass is a complete simulation of the given pseudo-physical model
  where all vertices are moved in one round. The simulation halts when the 
  average temperature has reached a given threshold or the number of 
  iterations is large enough thus forcing a termination (which could not be 
  granted otherwise due to the heuristics used; well, all graphs ever 
  tested cooled down, so this would not be necessary, but who knows).
- A further optimization pass simulates a similar model at lower 
  temperatures. This model adds vertex-edge-distances and is somewhat slower.
  In some cases, better results can be achieved with this method 
  (try star30.g). The default configuration does not use this optimization.


CONFIG PARAMETERS
-----------------
You can either change the parameters in gem.cfg directly or create your
own file and use gem's -c option.

A valid entry in the config file consists of
- a number-sign (#) followed immediately by 
- the parameter name and, seperated by whitespaces,
- the parameter value.
To be accepted, the value of a parameter must be in a certain range.
The parameters are sorted in three paragraphs according to the pass
in which they are used (INSERT_, ARRANGE_ or OPTIMIZE_).

The *-marked values are factors of the desired edge length 
..._MAXTEMP(*) is the maximum temperature a vertex can get
..._STARTTEMP(*) is the initial temperature of each vertex in the 
   current pass
..._FINALTEMP(*) is the (average) temperature all vertices should 
   finally have
..._MAXITER describes the maximum number of iterations; this value is 
   multiplied with the size (number of vertices) of the graph
..._GRAVITY controls the forces that tow each vertex to the barycenter
..._OSCILLATION controls the influence of oscillations (cooling down if
   new movement direction of given vertex is opposite to the last)
..._ROTATION controls the influence of rotations (similar to oscillations)
..._SHAKE controls the influence of random shakes

To turn a pass of, simply set its FINALTEMP = STARTTEMP.
If the insertion pass has been turned off, the graph is taken as is 
(undefined vertex positions are set to (0/0)). You may want to use 
the -r option to randomize the graph then.


QUALITY MEASUREMENT
-------------------
Although it is quite difficult to actually measure the "quality" of a given 
graph layout, I tried to create few, independent functions mapping a layout 
(that is, a two-dimensional position for each vertex) to a simple numerical 
value.
These function should allow comparisons between different layouts and
even different graphs, so several normalizations are done.

Gem produces such statistical output when called with the -q option:

|V|,|E| ...number of vertices and edges
Diam ......(graph theoretical) diameter of the graph
Nx ........number of edge crossings in the given layout

Qd, Qe, Qv and Qx are normalized quality functions, the smaller they, the
better is the layout:

Qd ........Diameter of the drawing (normalized by Diam)
Qe ........Standard deviation of edge lengths
Qv ........Mean reciprocal distance between the vertices
Qx ........Crossing number (relative to a guessed maximum)

Q is a weighted sum of Qd, Qe, Qv and Qx to get one value for a quick first 
comparison. They can be set to any value prefered.

When run in benchmark mode, the deviations of the number of rounds needed 
and "Q" are added.


POSSIBLE ENHANCEMENTS
---------------------
You can easily enlarge the maximum size of the graph by changing
the defines in adtgraph.h to allow for huge graphs.

It should be possible to make GEM pass through vertex or edge
information (like labels) to the output graph.

The parameter defaults in embedder.h are guesses. In case you find
better ones you might want to change the hard-coded defaults.

The routines in pscript.c can create colored output. GPS does not
use this feature because it has no information about the vertices'
temperatures. I used this to illustrate intermediate states of the
simulation. Although an animated version of the algorithm looked
very nice, I have no presentable version of this version. Sorry.

You can also alter the weights used to calculate Q in quality.h
to meet you personal goals.

You might want to add an animation, maybe with possible interaction of
the user. Do so. It looks fine.

REFERENCES
----------
[1] GraphEd is a graph editing and layout tool available for sun4 and linux
    via ftp at ftp.uni-passau.de:/pub/local/graphed.
[2] Frick, Ludwig, Mehldau: A Fast Adaptive Layout Algorithm for Undirected Graphs. 
    Proceedings of Graph Drawing'94, LNCS 894, Springer-Verlag 1994.
    A Postscript version of this file can be found at
    http://i44www.info.uni-karlsruhe.de/~frick/gd/gd94p.ps.gz



