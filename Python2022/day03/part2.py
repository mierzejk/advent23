from functools import reduce
from itertools import batched

from day03.part1 import _priority

with open(r'../inputs/day_3_input.txt', encoding=r'utf8') as file:
    print(sum(_priority[reduce(lambda s1, s2: set(s1) & set(s2), b).pop()] for b in batched(map(str.rstrip, file), 3)))
