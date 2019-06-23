#!/usr/bin/env python3

import os
import sys
import csv

outdir = sys.argv[1:]

field_names = [
        'capacity', 'blocksize', 'ways', 'sets', 'replacement', 'on write miss',
        'accesses', 'hit', 'miss', 'evictions', 'miss ratio',
]

rows = []
for odir in outdir:
    for name in os.listdir(odir):
        fullname = os.path.join(odir, name)
        with open(fullname) as f:
            fields = [name.split('.', 6)[-1]]
            for line in f:
                if ':' not in line:
                    continue
                key, value = (s.strip() for s in line.split(':'))
                if key not in field_names:
                    continue
                if key == 'miss ratio':
                    value = value.rstrip('%')
                fields.append(value)
            rows.append(fields)

w = csv.writer(sys.stdout)
w.writerows(rows)
