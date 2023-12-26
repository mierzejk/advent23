package day23

import day21.Board

class Maze(array: List<Char>, stride: Int, height: Int? = null): Board<Char>(array, stride, height) {
    constructor(array: CharSequence, stride: Int, height: Int? = null): this(array.toList(), stride, height)

    override fun getAdjacent(index: Int): List<Int> = super.getAdjacent(index).filter { '#' != array[it] }
}