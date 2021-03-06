#include	<stdio.h>
#include	<stdlib.h>
#include	<string.h>
#include	<time.h>
#undef min
#undef max
#include "defs.h"
#include "structs.h"
#include "read_params.h"
#include "utils.h"
#include "amg_eigen_computation.h"
#include "graph_generator.h"



unsigned long coarsening_time=0, power_iteration_time=0, rqi_time=0;
int total_negative_edges=0, total_coarse_edges=0;
double max_neg_percentage=0;

int IsWeightedGraph = TRUE;


int input_graph(
	FILE     *fin,			// input file 
	char     *inname,		// name of input file 
	int     **start,		// start of edge list for each vertex 
	int     **adjacency,	// edge list data 
	int      *nvtxs,		// number of vertices in graph 
	int     **vweights,		// vertex weight list data 
	float   **eweights		// edge weight list data 
);

void export_layout
		(int  *start, int *adjacency, int n, double ** coords, 
		FILE *fp,  char * filename, double coarsen_time, double pi_time,
		double running_time, int * dims2show, int ndims);


int main(int argc, char *argv[]) {

	struct vtx_data **graph;	/* graph data structure */
     int       nedges;		/* number of edges in graph */
	

	char * parametrs_fname;
	FILE * fin, * fout;

	int     *start;		/* start of edge list for each vertex */
	int     *adjacency;		/* edge list data */
	int     nvtxs;		/* number of vertices in graph */
	int     *vweights;		/* vertex weight list data */
	float   *eweights;		/* edge weight list data */
	double * vmasses=NULL;
	
	double ** evecs; // eigen vectors
	double * evals;  // eigen vals
	int ndims; // number of eigenvectors to compute
	int * dims2show; // which dimensions to print

	int i,j;

	int vmax; // smallest graph that should be coarsened
	int seed=time(NULL);
	srand(seed);
	unsigned int old_time,overall_time;
	//	extern int negativeMasses,	positiveMasses;

	
	GraphType graphType;
	int graphSize1,graphSize2;
	char graphFilename[100];
	char coordinatesFilename[100];
	int useGdrawLayoutFormat;

	int min_degree,max_degree,sum_degs,degree_i;
	
	if (argc==1) {
	  strcpy(graphFilename, "-");
	  read_parameters(NULL, &graphType, &graphSize1, &graphSize2,
			  graphFilename, coordinatesFilename, &useGdrawLayoutFormat,
			  &ndims, &dims2show, &vmax);
	  strcpy(coordinatesFilename,"-"); // ignore default
	  useGdrawLayoutFormat = 0;
	}
	else {
	  parametrs_fname=argv[1];
	  int len = strlen(parametrs_fname);
	  if (parametrs_fname[len-2]=='.' && parametrs_fname[len-1]=='g') {
	    strcpy(graphFilename, parametrs_fname);
	    read_parameters(NULL,
			    &graphType,
			    &graphSize1, 
			    &graphSize2,
			    graphFilename,
			    coordinatesFilename,
			    &useGdrawLayoutFormat,
			    &ndims, &dims2show, &vmax);
	    strcpy(coordinatesFilename,"-"); // ignore default
	    useGdrawLayoutFormat = 0;
	  }
	  else {
	    read_parameters(parametrs_fname,
			    &graphType, 
			    &graphSize1, 
			    &graphSize2,
			    graphFilename,
			    coordinatesFilename, 
			    &useGdrawLayoutFormat,
			    &ndims, &dims2show, &vmax);
	  }
	}

	// build the input file, if needed
	switch (graphType) {
	case grid: case folded_grid: case partial_grid: case partial_folded_grid:
		if (graphSize1<=0 || graphSize2<=0) {
			printf("One or more grid dimensions are not available. Exiting.\n");
			return -1;
		}
		makeSquareGrid(&nvtxs,graphSize1,graphSize2,graphFilename,
					   graphType==folded_grid || graphType==partial_folded_grid,
					   graphType==partial_grid || graphType==partial_folded_grid);
		break;
	case circle:
		if (graphSize1<=0) {
			printf("Circle perimeter is not available. Exiting.\n");
			return -1;
		}
		nvtxs = graphSize1;
		makeCircle(nvtxs,graphFilename);
		break;
	case tree:
		if (graphSize1<=0) {
			printf("Tree depth is not available. Exiting.\n");
			return -1;
		}
		makeBinaryTree(&nvtxs,graphSize1,graphFilename);
		break;
	case torus:
		if (graphSize1<=0 || graphSize2<=0) {
			printf("One or more torus dimensions are not available. Exiting.\n");
			return -1;
		}
		makeTorus(&nvtxs,graphSize1,graphSize2,graphFilename);
		break;
	case cylinder:
		if (graphSize1<=0 || graphSize2<=0) {
			printf("One or more cylinder dimensions are not available. Exiting.\n");
			return -1;
		}
		makeCylinder(&nvtxs,graphSize1,graphSize2,graphFilename);
		break;
	case sierpinski:
		if (graphSize1<=0) {
			printf("Sierpinski depth is not available. Exiting.\n");
			return -1;
		}
		makeSierpinski(&nvtxs,graphSize1,graphFilename);
		break;
	case hypercube:
		if (graphSize1<=1) {
			printf("Hyper-cube dimension is not correct. Exiting.\n");
			return -1;
		}
		make_hypercube(&nvtxs,graphSize1,graphFilename);
		break;
	case star:
		if (graphSize1<=2) {
			printf("Star size must be greatwer than 2. Exiting.\n");
			return -1;
		}
		nvtxs = graphSize1;
		makeStar(nvtxs,graphFilename);
		break;
	case unknown:
	  break;
	}
	
	if (graphFilename[0]=='-' && graphFilename[1]==0) {
	  fin = stdin;
	}
	else {
	  fin=fopen(graphFilename,"r");
	}
	if (fin==NULL) {
		printf("Error. Cannot open graph file: %s\n",graphFilename);
		exit(1);
	}

	input_graph(fin, graphFilename, &start, &adjacency, &nvtxs, &vweights, &eweights);
	if (fin != stdin) {
	  fclose(fin);
	}

	// Print stats:

	max_degree=sum_degs=0;
	min_degree=nvtxs;
	for (i=1; i<=nvtxs; i++) {
		degree_i=start[i]-start[i-1];
		sum_degs+=degree_i;
		if (degree_i>max_degree)
			max_degree=degree_i;
		if (degree_i<min_degree)
			min_degree=degree_i;
	}

	if (showStats) {
		fprintf(fpStatsFile,"Average degree: %.2lf, Max degree: %d, Min Degree: %d\n",
				(float)(sum_degs)/nvtxs, max_degree, min_degree);
	}
	//	printf("Average degree: %.2lf, Max degree: %d, Min Degree: %d\n",
	//			(float)sum_degs/nvtxs, max_degree, min_degree);
	
	if (eweights == NULL)
		IsWeightedGraph = FALSE;
	if (eweights == NULL/* && coarseningMethod==weighted*/) {
		eweights = (float *) malloc ((unsigned)(start[nvtxs]) * sizeof(float));
		for (i=0; i<start[nvtxs]; i++)
			eweights[i]=1.0; 
	}

	
	evecs = (double **) malloc( sizeof(double *) * (ndims+1));
	for (i=1; i<=ndims; i++) 
		evecs[i]=(double*) malloc((nvtxs+1)*sizeof(double));
	evals = (double*) malloc((ndims+1)*sizeof(double));


	// compute masses
	vmasses = (double *) malloc ((unsigned) (nvtxs+1) * sizeof(double));		
	if (vweights!=NULL) {
		for (i=1; i<=nvtxs; i++)
			vmasses[i] = vweights[i];
	}
	else {
		if (MAKE_VWGTS) {
			/* Generate vertex weights equal to weighted degree of node. */
			if (eweights != NULL) { // compute weighted degree
				for (i = 0; i < nvtxs; i++) {
					vmasses[i+1]=0;
					for (j=start[i]; j<start[i + 1]; j++)
						vmasses[i+1]+=eweights[j];	
				}
			}
			else { // no edge weights
				for (i = 0; i < nvtxs; i++) 
					vmasses[i+1] = start[i + 1] - start[i];	
			}
		}
		else { // uniform weights
			for (i = 1; i <= nvtxs; i++)
				vmasses[i] = 1;
		}
	}


	/////////////////////////////////
	///							  ///
	/// Eigenvectors computation: ///
	///							  ///
	/////////////////////////////////
	
	old_time=clock();
	reformat(start, adjacency, nvtxs, &nedges, NULL, eweights, &graph);
	if (!useGdrawLayoutFormat) {
		delete [] start;
		delete [] adjacency;
		if (eweights != NULL)
			delete [] eweights;
	}
	amg_eigen_computation(graph, nvtxs,  nedges, evecs, ndims, vmax, 0, vmasses, levels_between_refinement);
	overall_time = clock()-old_time;
	

	
	if (showStats) {
		fprintf(fpStatsFile,"net computation time %fsec\n",(float)(overall_time)/CLOCKS_PER_SEC);
		fprintf(fpStatsFile," Coarsening: %.3fs (%2.2f%%), Power it.:  %.3fs (%2.2f%%), RQI:  %.3fs (%2.2f%%)\n", 
			(float)coarsening_time/CLOCKS_PER_SEC, 100.0*coarsening_time/overall_time,
			(float)power_iteration_time/CLOCKS_PER_SEC, 100.0*power_iteration_time/overall_time,
			(float)rqi_time/CLOCKS_PER_SEC, 100.0*rqi_time/overall_time);
		fprintf(fpStatsFile, "Percentage of negative coarse edges %2.2f%%, Max Percentage of neative edges: %2.2f%%\n",
			total_negative_edges>0 ? (float)(100*total_negative_edges)/total_coarse_edges : 0 ,
			 max_neg_percentage);
	}

	if (fpStatsFile!=NULL && fpStatsFile!=stdout)
		fclose(fpStatsFile);
	
	//	printf("\n Net computation time %fsec\n",(float)(overall_time)/CLOCKS_PER_SEC);
	//	printf(" Coarsening: %.3fs (%2.2f%%), Power it.:  %.3fs (%2.2f%%), RQI:  %.3fs (%2.2f%%)\n", 
	//			(float)coarsening_time/CLOCKS_PER_SEC, 100.0*coarsening_time/overall_time,
	//			(float)power_iteration_time/CLOCKS_PER_SEC, 100.0*power_iteration_time/overall_time,
	//			(float)rqi_time/CLOCKS_PER_SEC, 100.0*rqi_time/overall_time);
	
	if (coordinatesFilename[0]=='-' && coordinatesFilename[1]==0) {
	  fout = stdout;
	}
	else {
	  fout = fopen(coordinatesFilename,"w");
	}
	if (fout==NULL) {
	    printf("Error. Cannot open graph file: %s\n",coordinatesFilename);
		exit(1);
	}


	// write eigenvectors to fout:
	if (!useGdrawLayoutFormat) {
		if (dims2show==NULL) {
			for (i=1; i<=nvtxs; i++) {
				for (j=1; j<=ndims; j++)
					fprintf(fout, "%lf  ", evecs[j][i]);
				fprintf(fout, "\n");
			}
		}
		else {
			for (i=1; i<=nvtxs; i++) {
				for (j=1; j<=ndims; j++)
					if (dims2show[j])
						fprintf(fout, "%lf  ", evecs[j][i]);
				fprintf(fout, "\n");
			}
		}
	} 
	else {
		export_layout(start, adjacency, nvtxs, evecs, fout,graphFilename,
			(float)coarsening_time/CLOCKS_PER_SEC,
			(float)power_iteration_time/CLOCKS_PER_SEC,(float)overall_time/CLOCKS_PER_SEC,dims2show,ndims);
		// Graph havn't been freed yet:
		delete [] start;
		delete [] adjacency;
		if (eweights != NULL)
			delete [] eweights;
	}

	
	for (i=1; i<=ndims; i++) 
		delete [] evecs[i];
	delete [] evecs;
	delete []evals;
	if (vmasses != NULL) 
		delete [] vmasses;
	if (graph != NULL)
		free_graph(graph);
	
	if (fout != stdout) {
	  fclose(fout);
	}
}

