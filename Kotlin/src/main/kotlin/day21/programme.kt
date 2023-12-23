package day21

import java.io.File

fun main() {
    val array = mutableListOf<Short>()
    var stride = 0
    var height = 0
    var startingPoint = -1

    fun addLine(line: String) {
        line.indexOf('S').takeIf { -1 < it }?.also { startingPoint = height * stride + it }
        height++
        line.map { when(it) {
            '#' -> (-1).toShort()
            else -> Short.MAX_VALUE
        } }.also(array::addAll)
    }

    File("src/main/resources/test.txt").useLines { file -> with(file.iterator()) {
        stride = next().also(::addLine).let(String::length)
        forEachRemaining(::addLine)
    } }
    val board = MutableBoard(array, stride, height)
    println(board)
}
