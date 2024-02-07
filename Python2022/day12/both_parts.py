import heapq


with open(r'../inputs/day_12_input.txt', r'rt', encoding=r'utf8') as file:
    area = ''.join(map(str.rstrip, file))
    file.seek(0)
    stride = len(next(file)) - 1

del file

start, finish = area.index(r'S'), area.index(r'E')
area = [ord(c) - ord(r'a') for c in area.replace(r'S', r'a', 1).replace(r'E', r'z', 1)]

# Part I
# heap = [(0, start)]
# visited = {start}

# Part II
heap = [(0, finish)]
visited = {finish}

# Part I
# while heap[0][1] != finish:

# Part II
while area[heap[0][1]] != 0:
    cost, cell = heapq.heappop(heap)

    # left
    # noinspection PyUnboundLocalVariable
    if 0 != cell % stride and (left_cell := cell - 1) not in visited and \
            area[left_cell] + 1 >= area[cell]:  # Part II
        # area[left_cell] - 1 <= area[cell]:  # Part I
        heapq.heappush(heap, (cost + 1, left_cell))
        visited.add(left_cell)

    # right
    if 0 != (right_cell := cell + 1) % stride and right_cell not in visited and\
            area[right_cell] + 1 >= area[cell]:  # Part II
        # area[right_cell] - 1 <= area[cell]:  # Part I
        heapq.heappush(heap, (cost + 1, right_cell))
        visited.add(right_cell)

    # top
    if 0 <= (top_cell := cell - stride) and top_cell not in visited and\
            area[top_cell] + 1 >= area[cell]:  # Part II
        # area[top_cell] - 1 <= area[cell]:  # Part I
        heapq.heappush(heap, (cost + 1, top_cell))
        visited.add(top_cell)

    # down
    if (down_cell := cell + stride) < len(area) and down_cell not in visited and\
            area[down_cell] + 1 >= area[cell]:  # Part II
        # area[down_cell] - 1 <= area[cell]:  # Part I
        heapq.heappush(heap, (cost + 1, down_cell))
        visited.add(down_cell)

# Part I
# print(heap[0][0])

# Part II
print(heap[0][0])
