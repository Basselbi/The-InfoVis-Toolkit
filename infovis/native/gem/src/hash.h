/* Copyright 2006 David Crawshaw, released under the new BSD license.
 * Version 2, from http://www.zentus.com/c/hash.html */

#ifndef __HASH__
#define __HASH__

/* Opaque structure used to represent hashtable. */
typedef struct hash hash;

typedef unsigned int (* hash_fn)(const void * key);

unsigned int strhash(const void * key);

unsigned int idhash(const void * key);

/* Create new hashtable. */
hash * hash_new(unsigned int size, hash_fn h);

/* Free hashtable. */
void hash_destroy(hash *h);

/* Add key/value pair. Returns non-zero value on error (eg out-of-memory). */
int hash_add(hash *h, const void *key, void *value);

/* Return value matching given key. */
void * hash_get(hash *h, const void *key);

/* Remove key from table, returning value. */
void * hash_remove(hash *h, const void *key);

/* Returns total number of keys in the hashtable. */
unsigned int hash_size(hash *h);

#endif
