from collections import deque


with open(r'../inputs/day_6_input.txt', r'rt', encoding=r'utf8') as file:
    text = file.read().rstrip()


def start(size: int) -> int:
    segment = deque(text[:size-1], maxlen=size)
    for i, c in enumerate(text[size-1:], size):
        segment.append(c)
        if size == len({*segment}):
            return i

    raise ValueError


print('Part 1', start(4))
print('Part 2', start(14))
