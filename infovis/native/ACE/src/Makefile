
#Uncomment the next lines for Windows/Cygwin
NO_CYGWIN = -mno-cygwin
#EXE=.exe
DEBUG=
OPTIMIZE=-O2
CFLAGS =  $(NO_CYGWIN) -DSILENT -DALL_INTERIOR_POINTERS -Igc6.7/include

CXXFLAGS = $(DEBUG) $(OPTIMIZE) $(NO_CYGWIN) -Dmalloc=debug_malloc -Dfree=debug_free


OBJ = \
	ace_main.o \
	amg_eigen_computation.o \
	coarsen_amg.o \
	coarsen_match.o \
	export_layout.o \
	graph_generator.o \
	input_graph.o \
	power_iteration.o \
	read_parameters.o \
	sort.o \
	utils.o \
	weighted_coarsening.o \
	RQI/RQI_main.o \
	RQI/aprod.o \
	RQI/msolve.o \
	RQI/orthog.o \
	RQI/rqi.o \
	RQI/symmlq.o \
	RQI/symmlqblas.o \
	RQI/utils_rqi.o \
	RQI/y2x.o \
	debug_malloc.o

# 	gc6.7/alloc.o \
# 	gc6.7/reclaim.o \
# 	gc6.7/allchblk.o \
# 	gc6.7/misc.o \
# 	gc6.7/mach_dep.o \
# 	gc6.7/os_dep.o \
# 	gc6.7/mark_rts.o \
# 	gc6.7/headers.o \
# 	gc6.7/mark.o \
# 	gc6.7/obj_map.o \
# 	gc6.7/blacklst.o \
# 	gc6.7/finalize.o \
# 	gc6.7/new_hblk.o \
# 	gc6.7/dbg_mlc.o \
# 	gc6.7/malloc.o \
# 	gc6.7/stubborn.o \
# 	gc6.7/dyn_load.o \
# 	gc6.7/typd_mlc.o \
# 	gc6.7/ptr_chck.o \
# 	gc6.7/mallocx.o


all:	ACE$(EXE)

ACE$(EXE):	$(OBJ)
	$(CXX) $(DEBUG) $(OPTIMIZE) $(NO_CYGWIN) -o ACE$(EXE) $(OBJ)

clean:
	rm -f $(OBJ) ACE$(EXE)

install: all
	cp ACE$(EXE) ../../..
