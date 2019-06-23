#!/usr/bin/env python3

import os

tracedir = 'traces/'
#traces = [f for f in os.listdir(tracedir) if not f.endswith('.bz')]
traces = ['ls.1.trace', 'ls.2.trace']

capacity = [2**i for i in range(8, 17)]
ways = [2**i for i in range(0, 17)]
blocksize = [2**i for i in range(0, 11)]

rpol = [i for i in range(0, 3)]
wpol = [i for i in range(0, 2)]

for cap in capacity:
    for way in ways:
        if cap < way:
            continue

        for blk in blocksize:
            if cap < way*blk:
                continue

            for wp in wpol:
                reps = rpol
                if way == 1:
                    reps = [0]

                for rp in reps:
                    for trace in traces:
                        cmd = './cachesim -c {} -w {} -b {} -r {} -W {}'.format(cap, way, blk, rp, wp)
                        out = 'exp/sim.{}.{}.{}.{}.{}.{}'.format(cap, way, blk, rp, wp, trace)

                        if (os.path.isfile(out)):
                            continue
                        print(cmd, tracedir+trace, out, sep=':')
