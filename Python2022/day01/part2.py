from heapq import heappush, heappushpop

heap = []
current_cal = 0

with open(r'../inputs/day_1_input.txt', encoding=r'utf8') as file:
    for line in map(str.strip, file):
        if line.isdigit():
            current_cal += int(line)
        else:
            (heappushpop if 3 == len(heap) else heappush)(heap, current_cal)
            current_cal = 0

heappushpop(heap, current_cal)

print(sum(heap))
