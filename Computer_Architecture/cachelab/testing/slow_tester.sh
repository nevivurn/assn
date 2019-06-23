#!/usr/bin/env bash

set -e

CACHESIM="./cachesim"
REFERENCE="./cachesim-ref"
TRACEDIR="./traces"
OUTDIR="./out_slow"
TRACES=("$TRACEDIR"/*.trace.bz2)

capacity=(16 256 4096)
blocksize=(1 64 256)
ways=(1 16)

mkdir -p "$OUTDIR"

try_cmd() {
	c=$1
	b=$2
	w=$3
	r=$4
	W=$5
	cmdline="-c $c -b $b -w $w -r $r -W $W"
	for trace in ${TRACES[@]}; do
		$CACHESIM $cmdline < "${trace%.bz2}" > "$OUTDIR/sim.$c.$b.$w.$r.$W.$(basename $trace)" &
		$REFERENCE $cmdline < "${trace%.bz2}" > "$OUTDIR/ref.$c.$b.$w.$r.$W.$(basename $trace)" &
	done
	wait
}

for trace in ${TRACES[@]}; do
	bunzip2 -k "$trace" &
done
wait

for c in ${capacity[@]}; do
	for b in ${blocksize[@]}; do
		for w in ${ways[@]}; do
			if [[ $c -lt $(($b*$w)) ]]; then
				continue
			fi
			for r in 0 1 2; do
				for W in 0 1; do
					try_cmd $c $b $w $r $W
				done
			done
		done
	done
done
echo

for trace in ${TRACES[@]}; do
	rm ${trace%.bz2}
done

for ref in $OUTDIR/ref.*; do
	sim=${ref/ref/sim}
	cmp -s $ref $sim || echo "$ref and $sim mismatch"
done
