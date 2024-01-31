import operator
import re

from classes import Directory, File
from typing import Callable, Generator


_cmd_re = re.compile(r'^\$ (?P<op>cd|ls)( (?P<dir>[a-z]+|/|\.\.))?$', re.A | re.I)
folder = root = Directory(r'/')

with open(r'../inputs/day_7_input.txt', r'rt', encoding=r'utf8') as file:
    for line in map(str.rstrip, file):
        if cmd := _cmd_re.match(line):
            match cmd.groupdict():
                case {r'op': r'ls'}:
                    continue
                case {r'op': r'cd', r'dir': str(directory)}:
                    if r'/' == directory:
                        folder = root
                        continue
                    if r'..' == directory:
                        folder = folder.parent
                        continue
                    if directory not in folder.folders:
                        folder[directory] = Directory(directory, folder)

                    folder = folder[directory]
                case _:
                    raise ValueError(cmd.string)
        else:
            match line.split():
                case [r'dir', str(directory)]:
                    if directory not in folder.folders:
                        folder[directory] = Directory(directory, folder)
                case [str(size), str(file_name)]:
                    folder.files[file_name] = File(file_name, folder, int(size))
                case _:
                    raise ValueError(line)


def get_folders(node: Directory, size_limit: int, op: Callable[[int, int], bool]) -> Generator[Directory, None, None]:
    if op(node.size, size_limit):
        yield node
    for child in node.folders.values():
        yield from get_folders(child, size_limit, op)


# Part I
print(sum(f.size for f in get_folders(root, 100000, operator.le)))

# Part II
at_least = 30000000 + root.size - 70000000
print(sorted((f for f in get_folders(root, at_least, operator.ge)), key=lambda f: f.size)[0].size)
