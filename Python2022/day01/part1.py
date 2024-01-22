max_cal, current_cal = 0, 0
with open(r'../inputs/day_1_input.txt', encoding=r'utf8') as file:
    for line in map(str.strip, file):
        match line:
            case _ if line.isdigit():
                current_cal += int(line)
            case _:
                if max_cal < current_cal:
                    max_cal = current_cal

                current_cal = 0

print(current_cal if max_cal < current_cal else max_cal)
