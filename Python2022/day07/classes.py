from abc import ABC, abstractmethod
from itertools import chain
from typing import Iterable, Optional, Self, TypeVar


TNode = TypeVar(r'TNode', bound=r'Node')


class Node(ABC):
    __slots__ = r'__name', r'__parent'

    def __init__(self: Self, /, name: str, parent: Optional[r'Directory']):
        self.__name = name
        self.__parent = parent

    @property
    def name(self: Self, /) -> str:
        return self.__name

    @property
    def parent(self: Self, /) -> Optional[r'Directory']:
        return self.__parent

    @property
    @abstractmethod
    def size(self: Self, /) -> int:
        raise NotImplementedError


class Directory(Node):
    __slots__ = r'__files', r'__folders',

    def __init__(self: Self,
                 /,
                 name: str,
                 parent: Self | None = None,
                 *,
                 folders: dict[str, Self] | None = None,
                 files: dict[str, r'File'] | None = None):
        super().__init__(name, parent)
        self.__files = dict() if files is None else files
        self.__folders = dict() if folders is None else folders

    @property
    def folders(self: Self, /) -> dict[str, Self]:
        return self.__folders

    def __getitem__(self: Self, /, path: str) -> Self:
        folder = self
        for name in path.split(r'/'):
            folder = folder.__folders[name]

        return folder

    def __setitem__(self, /, folder: str, directory: Self):
        self.__folders[folder] = directory

    @property
    def files(self: Self, /) -> dict[str, r'File']:
        return self.__files

    @property
    def children(self: Self, /) -> Iterable[TNode]:
        return chain(self.__folders.values(), self.__files.values())

    @property
    def size(self: Self, /) -> int:
        return sum(child.size for child in self.children)


class File(Node):
    __slots__ = r'__size',

    def __init__(self: Self, /, name: str, parent: Directory, size: int):
        super().__init__(name, parent)
        self.__size = size

    @property
    def parent(self: Self, /) -> Directory:
        return super().parent

    @property
    def size(self: Self, /) -> int:
        return self.__size
