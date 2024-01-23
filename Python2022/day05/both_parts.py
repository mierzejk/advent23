import re

from collections import deque


_digits_or_whitespace = re.compile(r'[\s\d]*', re.A | re.I)
_move_pattern = re.compile(r'^move (?P<move>\d+) from (?P<from>\d+) to (?P<to>\d+)', re.A | re.I)

with open(r'../inputs/day_5_input.txt', r'rt', encoding=r'utf8') as file:
    assert file.seekable()
    for i, line in enumerate(map(str.rstrip, file)):
        if _digits_or_whitespace.fullmatch(line):
            index_line = i
            stacks_no = int(re.split(r'\s+', line)[-1])
            break

    file.seek(0)
    stacks1 = {i: deque() for i in range(1, stacks_no + 1)}
    stacks2 = {i: deque() for i in range(1, stacks_no + 1)}
    for i, line in enumerate(map(lambda ln: ln.rstrip('\n'), file)):
        if i < index_line:
            for s, j in enumerate(range(1, stacks_no * 4 - 2, 4), 1):
                if (letter := line[j]).isalpha():
                    stacks1[s].appendleft(letter)
                    stacks2[s].appendleft(letter)
        elif match := _move_pattern.match(line):
            no, fr, to = map(int, match.groups())
            ordered = []
            for _ in range(no):
                stacks1[to].append(stacks1[fr].pop())
                ordered.append(stacks2[fr].pop())

            stacks2[to].extend(reversed(ordered))




print('Part I', ''.join(s[-1] for s in stacks1.values() if s))
print('Part II', ''.join(s[-1] for s in stacks2.values() if s))
