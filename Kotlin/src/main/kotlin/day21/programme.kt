package day21

import java.io.File
import java.util.*

const val STEPS = 64

internal class Garden(array: MutableList<Short>, stride: Int, height: Int? = null): MutableBoard<Short>(array, stride, height) {
    override fun getAdjacent(index: Int) = super.getAdjacent(index).filter { Short.MAX_VALUE == array[it] }

    override fun toString() = array.map { when(it) {
        (-1).toShort() -> "#"
        Short.MAX_VALUE -> "."
        else -> "O"
    } }.chunked(stride).joinToString("\n") { it.joinToString("") }
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
    garden[startingPoint] = 0
    val queue: Queue<Pair<Int, Short>> = PriorityQueue<Pair<Int, Short>> { a, b -> a.second - b.second}.apply { add(startingPoint to 0) }
    while (queue.isNotEmpty()) {
        val (plot, step) = queue.poll()
        if (STEPS < step)
            break

        // Left
        var nextStep = (step + 1).toShort()
        var nextIndex = plot
        while(nextStep <= STEPS && garden.elementLeft(nextIndex) == Short.MAX_VALUE) {
            nextIndex -= 1
            garden[nextIndex] = nextStep
            queue.add(nextIndex to nextStep)
            nextStep++
        }

        // Up
        nextStep = (step + 1).toShort()
        nextIndex = plot
        while(nextStep <= STEPS && garden.elementUp(nextIndex) == Short.MAX_VALUE) {
            nextIndex -= stride
            garden[nextIndex] = nextStep
            queue.add(nextIndex to nextStep)
            nextStep++
        }

        // Right
        nextStep = (step + 1).toShort()
        nextIndex = plot
        while(nextStep <= STEPS && garden.elementRight(nextIndex) == Short.MAX_VALUE) {
            nextIndex += 1
            garden[nextIndex] = nextStep
            queue.add(nextIndex to nextStep)
            nextStep++
        }

        // Down
        nextStep = (step + 1).toShort()
        nextIndex = plot
        while(nextStep <= STEPS && garden.elementDown(nextIndex) == Short.MAX_VALUE) {
            nextIndex += stride
            garden[nextIndex] = nextStep
            queue.add(nextIndex to nextStep)
            nextStep++
        }
    }

    println(garden.array.count { 0 == it % 2 })
}
