package day21

import java.io.File
import java.util.*

const val STEPS = 64
@Suppress("KotlinConstantConditions")
val divisor = if (0 == STEPS % 2) 0 else 1

internal class Garden(array: MutableList<Short>, stride: Int, height: Int? = null): MutableBoard<Short>(array, stride, height) {
    override fun getAdjacent(index: Int) = super.getAdjacent(index).filter { Short.MAX_VALUE == array[it] }

    override fun toString() = array.map { when(it) {
        (-1).toShort() -> "#"
        Short.MAX_VALUE -> "."
        0.toShort() -> "S"
        else -> if (divisor == it % 2) "O" else "_"
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
    fun straightTo(step: Short, plot: Int, site: (Int) -> Short?, delta: Int) {
        var nextStep = (step + 1).toShort()
        var nextIndex = plot
        while(nextStep <= STEPS && site(nextIndex) == Short.MAX_VALUE) {
            nextIndex += delta
            garden[nextIndex] = nextStep
            queue.add(nextIndex to nextStep)
            nextStep++
        }
    }

    while (queue.isNotEmpty()) {
        val (plot, step) = queue.poll()
        if (STEPS < step)
            break

        // Left
        straightTo(step, plot, garden::elementLeft, -1)
        // Up
        straightTo(step, plot, garden::elementUp, -stride)
        // Right
        straightTo(step, plot, garden::elementRight, 1)
        // Down
        straightTo(step, plot, garden::elementDown, stride)
    }

    println(garden.array.filter { -1 < it && it < Short.MAX_VALUE }.count { divisor == it % 2 })
    // Part II
    val odd_plots = 7336L
    val even_plots = 7327L
    assert(height == stride)
    val totalSteps = 26501365
    println("$totalSteps = ${totalSteps / height} * $stride + ${totalSteps % height}")
    var total = odd_plots

    for (i in 1..(totalSteps / height)) {
        total += i * 4 * when (i % 2) {
            1 -> even_plots
            else -> odd_plots
        }
    }
    // 600090495422936 → too low     / 1..(totalSteps / height)
    // 600096424460644 → too high    / 1..(totalSteps / height) + 1

    // Starting point + 130

    println(total)
}
