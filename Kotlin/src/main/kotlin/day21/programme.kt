package day21

import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.math.min

typealias Point = Pair<Int, Int>

var STEPS = 64

internal class Garden(array: MutableList<Short>, stride: Int, height: Int? = null, private val divisor: Int = STEPS % 2):
    MutableBoard<Short>(array, stride, height) {
    override fun getAdjacent(index: Int) = super.getAdjacent(index).filter { Short.MAX_VALUE == array[it] }

    override fun toString() = array.map { when(it) {
        (-1).toShort() -> "#"
        Short.MAX_VALUE -> "."
        0.toShort() -> "S"
        else -> if (divisor == it % 2) "O" else "_"
    } }.chunked(stride).joinToString("\n") { it.joinToString("") }
}

data class TotalPlots(var odd: Long = 0L, var even: Long = 0L)

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
    val garden = Garden(ArrayList(array), stride, height)

    // Part I
    fun getPlotCount(divisor: Int = STEPS % 2): Long {
        garden[startingPoint] = 0
        val queue: Queue<Pair<Int, Short>> =
            PriorityQueue<Pair<Int, Short>> { a, b -> a.second - b.second }.apply { add(startingPoint to 0) }

        fun straightTo(step: Short, plot: Int, site: (Int) -> Short?, delta: Int) {
            var nextStep = (step + 1).toShort()
            var nextIndex = plot
            while (nextStep <= STEPS && site(nextIndex) == Short.MAX_VALUE) {
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

        return garden.array.filter { -1 < it && it < Short.MAX_VALUE }.count { divisor == it % 2 }.toLong()
    }
    println("Part I: ${getPlotCount()}")

    // Part II
    val totalSteps = 26501365
    val euclidean = (totalSteps / height).toLong()
    val remainder = totalSteps % height
    println("Part II $totalSteps steps = $euclidean * $stride + $remainder")

    fun Point.standardize() = Point(
        (this.first % height).let { when {
            it >= 0 -> it
            else -> height + it
        } },
        (this.second % stride).let { when {
            it >= 0 -> it
            else -> stride + it
        } }
    )

    fun Point.outOfBounds() = first < 0 || height <= first || second < 0 || stride <= second

    fun getPlots(range: IntRange, infinite: Boolean = false) = sequence {
        val queue = ArrayDeque<Point>().apply { add(Point(startingPoint / height, startingPoint % stride)) }
        val visited = mutableSetOf<Point>()
        val totalPlots = TotalPlots()
        range.forEach { step ->
            val counter = when (0 == step % 2) {
                true -> totalPlots::even // even
                false -> totalPlots::odd // odd
            }
            queue.indices.forEach { _ ->
                val (y, x) = queue.removeFirst()
                for (point in listOf(Point(y, x-1), Point(y-1, x), Point(y, x+1), Point(y+1, x))) {
                    if (!infinite && point.outOfBounds())
                        continue
                    val (j, i) = point.standardize()
                    if (point in visited || (-1).toShort() == array[j * stride + i])
                        continue

                    visited.add(point)
                    queue.add(point)
                    counter.get().also { counter.set(it + 1L) }
                }
            }
            yield(step to totalPlots)
            if (queue.isEmpty())
                return@sequence
        }
    }

    val borderCrossings = listOf(remainder, remainder + stride, remainder + 2 * stride)
    val stepPlots = mutableListOf<Long>()
    for ((step, plots) in getPlots(1..borderCrossings.last(), infinite = true)) {
        if (step in borderCrossings)
            stepPlots.add(when (0 == step % 2) {
                true -> plots.even
                false -> plots.odd
            }) }

    // Part II - Lagrange polynomial
    println(listOf(1, 2, 3) zip stepPlots)
    // The parabola of best fit is y = 14663*x^2 - 14808*x + 3719
    println("Lagrange result: ${3719L + 14663L * euclidean * euclidean + 14808L * euclidean}.")

    // Part II - Geometric
    var sixtyFive = TotalPlots()
    var full = TotalPlots()
    getPlots(1..Int.MAX_VALUE).forEach { (step, plots) ->
        if (65 == step) {
            full = plots
            sixtyFive = plots.copy()
        } }
    val (oddFull, evenFull) = full
    val (oddCorners, evenCorners) = listOf(oddFull - sixtyFive.odd, evenFull - sixtyFive.even)
    val result = (euclidean + 1) * (euclidean + 1) * oddFull + euclidean * euclidean * evenFull - (euclidean + 1) * oddCorners + euclidean * evenCorners
    println("Geometric result: $result.")
}

internal fun min(a: Short, b: Short) = min(a.toInt(), b.toInt()).toShort()

@Suppress("unused")
fun zipArrays(left: List<Short>, right: List<Short>): List<Short> {
    return (left zip right).map { (a, b) -> min(a, b).also { if ((-1).toShort() == it) assert(a == b) } }
}