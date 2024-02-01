grid = []
with open(r'../inputs/day_8_input.txt', r'rt', encoding=r'utf8') as file:
    for height, line in enumerate(map(str.rstrip, file), 1):
        grid.extend(map(int, line))

grid_len = len(grid)
stride = grid_len // height

# Part I
visible = [0] * grid_len

# left to right
for i in range(0, height):
    visible[stride * i] = 1
    tallest = grid[stride * i]
    for j in range(1, stride):
        cell = stride * i + j
        if grid[cell] > tallest:
            visible[cell] = 1
            tallest = grid[cell]

# top to bottom
for j in range(0, stride):
    visible[j] = 1
    tallest = grid[j]
    for i in range(1, height):
        cell = stride * i + j
        if grid[cell] > tallest:
            visible[cell] = 1
            tallest = grid[cell]

# right to left
for i in range(0, height):
    visible[stride * (i + 1) - 1] = 1
    tallest = grid[stride * (i + 1) - 1]
    for j in range(stride - 2, -1, -1):
        cell = stride * i + j
        if grid[cell] > tallest:
            visible[cell] = 1
            tallest = grid[cell]

# bottom to top
for j in range(0, stride):
    visible[stride * (height - 1) + j] = 1
    tallest = grid[stride * (height - 1) + j]
    for i in range(height - 2, -1, -1):
        cell = stride * i + j
        if grid[cell] > tallest:
            visible[cell] = 1
            tallest = grid[cell]

print(sum(visible))


# Part II
def score_up(index: int) -> int:
    score = 0
    tree = grid[index]
    next_index = index - stride
    while 0 <= next_index:
        score += 1
        if tree <= grid[next_index]:
            return score

        next_index -= stride

    return score


def score_down(index: int) -> int:
    score = 0
    tree = grid[index]
    next_index = index + stride
    while next_index < grid_len:
        score += 1
        if tree <= grid[next_index]:
            return score

        next_index += stride

    return score


def score_right(index: int) -> int:
    score = 0
    tree = grid[index]
    next_index = index + 1
    while 0 != next_index % stride:
        score += 1
        if tree <= grid[next_index]:
            return score

        next_index += 1

    return score


def score_left(index: int) -> int:
    score = 0
    tree = grid[index]
    next_index = index - 1
    while stride - 1 != next_index % stride:
        score += 1
        if tree <= grid[next_index]:
            return score

        next_index -= 1

    return score


def get_score(index: int) -> int:
    return score_up(index) * score_down(index) * score_right(index) * score_left(index)


print(max(map(get_score, range(0, grid_len))))
