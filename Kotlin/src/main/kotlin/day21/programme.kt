package day21

import java.io.File

internal open class Board<T>(val array: List<T>, val stride: Int, height: Int? = null) {
    private val lastButStride = array.lastIndex - stride
    val height = height ?: (array.size / stride)

    init { assert (this.height * stride == array.size) }

    fun Int.spaceLeft() = 0 != this % stride
    fun Int.distanceLeft() = this % stride
    fun Int.spaceUp() = stride <= this
    fun Int.distanceUp() = this / stride
    fun Int.spaceRight() = 0 != (this + 1) % stride
    fun Int.distanceRight() = stride - 1 - this.distanceLeft()
    fun Int.spaceDown() = this <= lastButStride
    fun Int.distanceDown() = height - 1 - this.distanceUp()

    override fun toString() = array.map(Any?::toString).chunked(stride).joinToString("\n")

}

internal class MutableBoard<T>(private val mutableArray: MutableList<T>, stride: Int, height: Int? = null)
    : Board<T>(mutableArray, stride, height)

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
