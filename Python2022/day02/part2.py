scores = {
    r'A X': 3 + 0,  # lose
    r'A Y': 1 + 3,  # draw
    r'A Z': 2 + 6,  # win
    r'B X': 1 + 0,  # lose
    r'B Y': 2 + 3,  # draw
    r'B Z': 3 + 6,  # win
    r'C X': 2 + 0,  # lose
    r'C Y': 3 + 3,  # draw
    r'C Z': 1 + 6   # win
}

with open(r'../inputs/day_2_input.txt', r'rt', encoding=r'utf8') as file:
    print(sum(scores[line.rstrip()] for line in file))
