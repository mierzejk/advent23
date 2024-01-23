from day04.part1 import to_tuple


def overlap(a: tuple[int, int], b: tuple[int, int]) -> int:
    return 1 if min(a[1], b[1]) - max(a[0], b[0]) >= 0 else 0


with open(r'../inputs/day_4_input.txt', r'rt', encoding=r'utf8') as file:
    print(sum(overlap(to_tuple(a), to_tuple(b)) for a, b in map(lambda line: line.rstrip().split(r','), file)))
