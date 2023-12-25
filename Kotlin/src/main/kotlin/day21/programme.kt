package day21

import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

var STEPS = 64

internal class Garden(array: MutableList<Short>, private val divisor: Int = STEPS % 2, stride: Int, height: Int? = null): MutableBoard<Short>(array, stride, height) {
    override fun getAdjacent(index: Int) = super.getAdjacent(index).filter { Short.MAX_VALUE == array[it] }

    override fun toString() = array.map { when(it) {
        (-1).toShort() -> "#"
        Short.MAX_VALUE -> "."
        0.toShort() -> "S"
        else -> if (divisor == it % 2) "O" else "_"
    } }.chunked(stride).joinToString("\n") { it.joinToString("") }
}

internal fun min(a: Short, b: Short) = min(a.toInt(), b.toInt()).toShort()

fun zipArrays(left: List<Short>, right: List<Short>): List<Short> {
    return (left zip right).map { (a, b) -> min(a, b).also { if ((-1).toShort() == it) assert(a == b) } }
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
    var garden = Garden(ArrayList(array), stride, height)

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
    val euclidean = totalSteps / height
    println("Part II $totalSteps steps = $euclidean * $stride + ${totalSteps % height}")
    STEPS = 501
    garden = Garden(ArrayList(array), stride, height)
//    val oddPlots = 7336L
    val oddPlots = getPlotCount()
    println("Odd plot count: $oddPlots")
    STEPS = 500
    garden = Garden(ArrayList(array), stride, height)
//    val evenPlots = 7327L
    val evenPlots = getPlotCount()
    println("Even plot count: $evenPlots")
    assert(height == stride)
    var total = oddPlots

    for (i in 1..euclidean) {
        total += i * 4 * when (i % 2) {
            1 -> evenPlots
            else -> oddPlots
        }
    }
    // 600090495422936 → too low     / 1..(totalSteps / height)
    // 600090522932119

    println("Total1: $total")

    // Top gardens, all even
    STEPS = 130 // Starting point + 130
    val sPoint = startingPoint
    // Left
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = sPoint + 65
    val leftPlots = getPlotCount()
    val gardenLeft = garden.array.toList()
    println("Right: $leftPlots")
    // Up
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = array.lastIndex - 65
    val upPlots = getPlotCount()
    val gardenUp = garden.array.toList()
    println("Down: $upPlots")
    // Right
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = sPoint - 65
    val rightPlots = getPlotCount()
    val gardenRight = garden.array.toList()
    println("Left: $rightPlots")
    // Down
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = 65
    val downPlots = getPlotCount()
    val gardenDown = garden.array.toList()
    println("Up: $downPlots")

    total += leftPlots + upPlots + rightPlots + downPlots

    // Diagonal gardens, all even (65+65)
    fun diagonal(a: List<Short>, b: List<Short>) = zipArrays(a, b).filter { -1 < it && it < Short.MAX_VALUE }.count { 0 == it % 2 }.toLong()
    // Left / Up
    val leftUpPlots = diagonal(gardenLeft, gardenUp)
    println("Left-Up: $leftUpPlots")
    // Right / Up
    val rightUpPlots = diagonal(gardenRight, gardenUp)
    println("Right-Up: $rightUpPlots")
    // Right / Down
    val rightDownPlots = diagonal(gardenRight, gardenDown)
    println("Right-Down: $rightDownPlots")
    // Left / Down
    val leftDownPlots = diagonal(gardenLeft, gardenDown)
    println("Left-Down: $leftDownPlots")

    // Sum up
    var multiplier = euclidean / 4
    total += listOf(leftUpPlots, rightUpPlots, rightDownPlots, leftDownPlots).sumOf { it * multiplier }

    // Oblique reminders
    STEPS = 130 - 66
    // Left / Up
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = array.lastIndex
    val leftUpOblique = getPlotCount()
    println("Left-Up oblique: $leftUpOblique")
    // Right / Up
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = array.size - stride
    val rightUpOblique = getPlotCount()
    println("Right-Up oblique: $rightUpOblique")
    // Right / Down
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = 0
    val rightDownOblique = getPlotCount()
    println("Right-Up oblique: $rightDownOblique")
    // Left / Down
    garden = Garden(ArrayList(array), stride, height)
    startingPoint = stride - 1
    val leftDownOblique = getPlotCount()
    println("Right-Up oblique: $leftDownOblique")

    multiplier += 1
    total += listOf(leftUpOblique, rightUpOblique, rightDownOblique, leftDownOblique).sumOf { it * multiplier }

    println(total)
}
