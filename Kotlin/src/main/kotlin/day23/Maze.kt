package day23

import day21.Board

class Maze(array: List<Char>, stride: Int, height: Int? = null): Board<Char>(array, stride, height) {
    private val allowed = "<^>v".map{ listOf('.', it) }
    constructor(array: CharSequence, stride: Int, height: Int? = null) : this(array.toList(), stride, height)

    override fun getAdjacent(index: Int) =
        adjacentIndices.mapIndexedNotNull { i, fnc -> fnc(index)?.takeIf { array[it] in allowed[i] } }
}