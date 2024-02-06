import operator

from collections import deque
from functools import reduce
from typing import Iterable


class Monkey:
    __slots__ = r'__divisor', r'__id', r'__inspections', r'__items', r'__operation', r'__throw'

    def __init__(self,
                 monkey_id: int,
                 starting_items: Iterable[int],
                 operation: str,
                 divisor: int,
                 throw_true: int,
                 throw_false: int):
        self.__id = monkey_id
        self.__inspections = 0
        self.__items = deque(starting_items)
        self.__divisor = divisor
        self.__throw = {
            True: throw_true,
            False: throw_false
        }
        exec(compile(fr'self._Monkey__operation = lambda old: {operation}', r'<string>', 'single'),
             None,
             {'self': self})

    @property
    def moneky_id(self) -> int:
        return self.__id

    @property
    def inspections(self) -> int:
        return self.__inspections

    @property
    def divisor(self) -> int:
        return self.__divisor

    def process_next_item(self, euclidean_divisor: int = 3) -> tuple[int, int] | None:
        if not self.__items:
            return None

        self.__inspections += 1
        item = self.__operation(self.__items.popleft()) // euclidean_divisor
        return self.__throw[0 == item % self.__divisor], item

    def catch_item(self, item: int):
        self.__items.append(item)


_monkeys: dict[int, Monkey] = dict()
with open(r'../inputs/day_11_input.txt', r'rt', encoding=r'utf8') as file:
    for i, line in enumerate(map(str.strip, file)):
        match i % 7:
            case 0:
                m_id = int(line[7:-1])
            case 1:
                m_starting_items = [int(j) for j in line[16:].split(r', ')]
            case 2:
                m_operation = line[17:]
            case 3:
                m_divisor = int(line[19:])
            case 4:
                m_throw_true = int(line[25:])
            case 5:
                m_throw_false = int(line[26:])
            case 6:
                _monkeys[m_id] = Monkey(m_id, m_starting_items, m_operation, m_divisor, m_throw_true, m_throw_false)

    if m_id not in _monkeys:
        _monkeys[m_id] = Monkey(m_id, m_starting_items, m_operation, m_divisor, m_throw_true, m_throw_false)


lcm = reduce(operator.mul, (monkey.divisor for monkey in _monkeys.values()))


def play_round(euclidean_divisor: int):
    for monkey_id in range(len(_monkeys)):
        monkey = _monkeys[monkey_id]
        result = monkey.process_next_item(euclidean_divisor)
        while result is not None:
            _monkeys[result[0]].catch_item(result[1] % lcm)
            result = monkey.process_next_item(euclidean_divisor)


# Part I
# for i in range(20):
#     play_round(3)

# Part II
for i in range(10000):
    play_round(1)

inspections = sorted((monkey.inspections for monkey in _monkeys.values()), reverse=True)
print(inspections[0] * inspections[1])
