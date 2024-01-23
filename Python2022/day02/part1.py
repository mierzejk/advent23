scores = {
    r'A X': 1 + 3,
    r'A Y': 2 + 6,
    r'A Z': 3 + 0,
    r'B X': 1 + 0,
    r'B Y': 2 + 3,
    r'B Z': 3 + 6,
    r'C X': 1 + 6,
    r'C Y': 2 + 0,
    r'C Z': 3 + 3
}

with open(r'../inputs/day_2_input.txt', r'rt', encoding=r'utf8') as file:
    print(sum(scores[line.rstrip()] for line in file))
