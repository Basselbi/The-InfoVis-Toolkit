#include <stdio.h>
#include <malloc.h>

void* debug_malloc(unsigned s)
{
  void * ret =  malloc(s);
  fprintf(stderr, "malloc(%u)=%x\n", s, ret);
  return ret;
}

void debug_free(void * ptr)
{
  fprintf(stderr, "free(%x)\n", ptr);
  free(ptr);
}
