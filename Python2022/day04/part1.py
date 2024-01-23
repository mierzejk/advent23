def contains(a: tuple[int, int], b: tuple[int, int]) -> bool:
    a, b = sorted((a, b))
    return b[1] <= a[1] or a[0] == b[0]


def to_tuple(s: str) -> tuple[int, int]:
    a, b = s.split(r'-')
    return int(a), int(b)


if r'__main__' == __name__:
    with open(r'../inputs/day_4_input.txt', r'rt', encoding=r'utf8') as file:
        print(sum(contains(to_tuple(a), to_tuple(b)) for a, b in map(lambda line: line.rstrip().split(r','), file)))
