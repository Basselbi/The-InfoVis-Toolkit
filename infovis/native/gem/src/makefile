#                                      o
# Makefile for GEM-Filter             o  
#                           __________ o
#                           ===-------++

# Sources


#Uncomment the next lines for Windows/Cygwin
NO_CYGWIN = -mno-cygwin
EXE=.exe
OPTIMIZE=-O3 -Wall

CFLAGS = $(DEBUG) $(OPTIMIZE) $(NO_CYGWIN)

GEMSRCS = gem.c global.c adtgraph.c graphed.c gsearch.c config.c embedder.c \
          geometry.c quality.c  hash.c
GPSSRCS = gps.c global.c adtgraph.c graphed.c gscale.c pscript.c

GEMOBJS = gem.o global.o adtgraph.o graphed.o gsearch.o config.o embedder.o \
          geometry.o quality.o hash.o
GPSOBJS = gps.o global.o adtgraph.o graphed.o gscale.o pscript.o

# Targets

all: gem 

gem: $(GEMOBJS)
	$(CC) $(DEBUG) $(OPTIMIZE) $(NO_CYGWIN) -o $@ $(GEMOBJS) -lm 

gps: $(GPSOBJS)
	$(CC)  $(NO_CYGWIN) -o $@ $(GPSOBJS) -lm

clean:
	rm -f $(GEMOBJS) $(GPSOBJS) gem$(EXE)

install: all
	cp gem ../../..
