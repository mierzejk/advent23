package day21

import java.io.File

const val STEPS = 64

internal class Garden(array: MutableList<Short>, stride: Int, height: Int? = null): MutableBoard<Short>(array, stride, height) {
    override fun getAdjacent(index: Int) = super.getAdjacent(index).filter { Short.MAX_VALUE == array[it] }
}

fun main() {
    val array = mutableListOf<Short>()
    var stride = 0
    var height = 0
    var startingPoint = -1

    fun addLine(line: String) {
        line.indexOf('S').takeIf { -1 < it }?.also { startingPoint = height * stride + it }
        height++
        line.map { when(it) {
            '#' -> -1
            else -> Short.MAX_VALUE
        } }.also(array::addAll)
    }

    File("src/main/resources/day_21_input.txt").useLines { file -> with(file.iterator()) {
        stride = next().also(::addLine).let(String::length)
        forEachRemaining(::addLine)
    } }
    val garden = Garden(array, stride, height)

    // Part I
    val deque = ArrayDeque(garden.getAdjacent(startingPoint).map { it to 1.toShort()})
    while (deque.isNotEmpty()) {
        val (plot, step) = deque.removeFirst()
        println(step)
        garden[plot] = step
        if (step < STEPS) {
            garden.getAdjacent(plot).map { it to (step + 1).toShort() }.also(deque::addAll)
        }
    }

    println(garden.array.count { 0 == it % 2 })
}
