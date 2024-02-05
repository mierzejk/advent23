_X = _c = 1
_acc = 0
_canvas = [r'.'] * 240

with open(r'../inputs/day_10_input.txt', r'rt', encoding=r'utf8') as file:
    for line in map(str.rstrip, file):
        match line.split():
            case [r'addx', str(value)]:
                for i in range(2):
                    if 20 == _c % 40:
                        _acc += _c * _X
                    if _X - 1 <= (j := _c - 1) % 40 <= _X + 1:
                        _canvas[j] = r'#'

                    _c += 1

                _X += int(value)
            case [r'noop']:
                if 20 == _c % 40:
                    _acc += _c * _X
                if _X - 1 <= (j := _c - 1) % 40 <= _X + 1:
                    _canvas[j] = r'#'

                _c += 1
            case _:
                raise ValueError(line)

# Part I
print(_acc)

# Part II
for i, c in enumerate(_canvas, 1):
    print(c, end='\n' if 0 == i % 40 else r'')
