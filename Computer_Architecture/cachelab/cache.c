//------------------------------------------------------------------------------
// 4190.308                     Computer Architecture                Spring 2019
//
// Cache Simulator Lab
//
// File: cache.c
//
// (C) 2015 Computer Systems and Platforms Laboratory, Seoul National University
//
// Changelog
// 20151119   bernhard    created
// 20190522   ca_ta       modified

#include <assert.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include "cache.h"

extern char RP_STR[3][32];
extern char WP_STR[2][20];

Cache* create_cache(uint32_t capacity, uint32_t blocksize, uint32_t ways,
		REP_POLICY rp, WRITE_POLICY wp, uint32_t verbosity) {
	// 1. check cache parameters
	//    - capacity, blocksize, and ways must be powers of 2
	//    - capacity must be larger than set size
	assert(ISPOW2(capacity));
	assert(ISPOW2(blocksize));
	assert(ISPOW2(ways));
	assert(capacity / blocksize >= ways);
	assert(rp >= 0 && rp <= sizeof RP_STR / sizeof RP_STR[0]);
	assert(wp >= 0 && wp <= sizeof WP_STR / sizeof WP_STR[0]);

	// 2. allocate cache and initialize them
	//    - use the above data structures Cache, Set, and Line
	Cache *c = malloc(sizeof *c);
	if (c == NULL) {
		perror("malloc");
		return NULL;
	}
	uint32_t sets = capacity / ways / blocksize;
	c->set = malloc(sets * sizeof *c->set);
	if (c->set == NULL) {
		free(c);
		perror("malloc");
		return NULL;
	}
	Line *lines = malloc(sets*ways * sizeof *lines);
	if (lines == NULL) {
		free(c);
		free(c->set);
		perror("malloc");
		return NULL;
	}
	for (uint32_t i = 0; i < sets*ways; i++)
		lines[i].valid = 0;

	c->s_access = c->s_hit = c->s_miss = c->s_evict = 0;
	c->sets = sets;
	c->ways = ways;
	c->blocksize = blocksize;
	c->rp = rp;
	c->wp = wp;

	for (uint32_t i = 0; i < sets; i++) {
		c->set[i].way = lines + ways*i;
		c->set[i].rrcnt = c->set[i].lrucnt = 0;
	}

	c->set_shift = 0;
	while (1<<c->set_shift < blocksize) c->set_shift++;
	c->tag_shift = c->set_shift;
	while (1<<c->tag_shift < blocksize*sets) c->tag_shift++;

	// 3. print cache configuration
	printf("Cache configuration:\n"
			"  capacity:        %6u\n"
			"  blocksize:       %6u\n"
			"  ways:            %6u\n"
			"  sets:            %6u\n"
			"  tag shift:       %6u\n"
			"  replacement:     %s\n"
			"  on write miss:   %s\n"
			"\n",
			capacity, blocksize, ways, sets, c->tag_shift, RP_STR[rp], WP_STR[wp]);

	// 4. return cache
	return c;
}

void delete_cache(Cache *c) {
	// clean-up the allocated memory
	free(c->set[0].way);
	free(c->set);
	free(c);
}

void line_access(Cache *c, Set *s, Line *l) {
	// update data structures to reflect access to a cache line
	l->used = s->lrucnt++;
}


void line_alloc(Cache *c, Set *s, Line *l, uint32_t tag) {
	// update data structures to reflect allocation of a new block into a line
	l->valid = 1;
	l->tag = tag;
	l->used = s->lrucnt++;
}

uint32_t set_find_victim(Cache *c, Set *s) {
	// for a given set, return the victim line where to place the new block
	uint32_t minlru = 0;
	for (uint32_t i = 0; i < c->ways; i++) {
		if (!s->way[i].valid) return i;
		if (s->way[i].used < s->way[minlru].used) minlru = i;
	}
	switch (c->rp) {
		case RP_RANDOM:
			return rand() % c->ways;
		case RP_RR:
			return s->rrcnt++ % c->ways;
		case RP_LRU:
			return minlru;
	}
	return 0;
}

void cache_access(Cache *c, uint32_t type, uint32_t address, uint32_t length) {
	// simulate a cache access

	// 1. compute set & tag
	uint32_t setn = (address & ((1<<c->tag_shift) - 1)) >> c->set_shift;
	uint32_t tag = address>>c->tag_shift;
	Set *set = c->set + setn;

	// 2. check if we have a cache hit
	Line *way = NULL;
	for (uint32_t wayn = 0; wayn < c->ways; wayn++) {
		if (set->way[wayn].valid && set->way[wayn].tag == tag) {
			way = set->way + wayn;
			break;
		}
	}

	// 3. on a cache miss, find a victim block and allocate according to the
	//    current policies
	int evicted = 0;
	if (way == NULL && type == WRITE && c->wp == WP_NOWRITEALLOC);
	else if (way == NULL) {
		Line * evict = set->way + set_find_victim(c, set);
		if (evict->valid) evicted = 1;
		line_alloc(c, set, evict, tag);
	} else line_access(c, set, way);

	// 4. update statistics (# accesses, # hits, # misses)
	c->s_access++;
	if (way) c->s_hit++;
	else c->s_miss++;
	if (evicted) c->s_evict++;
}
