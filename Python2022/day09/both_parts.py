from functools import partial, reduce
from typing import Callable, Iterable, NamedTuple, Self


def fchain[T, R](x: T, f: Callable[[T], R]) -> R:
    return f(x)


def fmap(*args: Callable) -> Iterable[Callable]:
    return map(partial(partial, map), args)


class Position(NamedTuple):
    Y: int
    X: int

    def move(self, dy, dx) -> Self:
        return Position(self.Y + dy, self.X + dx)

    def follow(self, p: Self) -> Self:
        dy, dx = p.Y - self.Y, p.X - self.X
        if 2 == abs(dy):
            dy //= 2
        if 2 == abs(dx):
            dx //= 2

        return self.move(dy, dx)

    def not_adjacent(self, p: Self) -> bool:
        return 1 < abs(self.Y - p.Y) or 1 < abs(self.X - p.X)


directions = {
    r'L': (0, -1),
    r'U': (-1, 0),
    r'R': (0, 1),
    r'D': (1, 0)
}
start: Position = Position(0, 0)
rope: list[Position] = [start] * 10
visited: set[Position] = {start}
visited_10: set[Position] = {start}

with open(r'../inputs/day_9_input.txt', r'rt', encoding=r'utf8') as file:
    for direction, steps in reduce(fchain, fmap(str.rstrip, str.split, lambda x: (directions[x[0]], int(x[1]))), file):
        for i in range(0, steps):
            rope[0] = rope[0].move(*direction)
            for j, knot in enumerate(rope[1:], 1):
                if knot.not_adjacent(pred := rope[j-1]):
                    rope[j] = knot = knot.follow(pred)
                    match j:
                        case 1:
                            visited.add(knot)
                        case 9:
                            visited_10.add(knot)
                else:
                    break

# Part I
print(len(visited))

# Part I
print(len(visited_10))
