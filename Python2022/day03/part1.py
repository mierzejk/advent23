from itertools import chain

_ord_a, _ord_A = ord('a') - 1, ord('A') - 27
_priority = {k: v for k, v in chain(
    ((chr(i + _ord_a), i) for i in range(1, 27)),
    ((chr(i + _ord_A), i) for i in range(27, 53)))}

if r'__main__' == __name__:
    with open(r'../inputs/day_3_input.txt', encoding=r'utf8') as file:
        print(sum(map(lambda i: _priority[i.pop()],
                      (set(line[:len(line)//2]) & set(line[len(line)//2:]) for line in map(str.rstrip, file)))))
