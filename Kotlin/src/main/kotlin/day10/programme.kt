package day10

import java.io.File
import kotlin.math.abs
import kotlin.reflect.KProperty

class Diagram(private val list: List<Char>, private val stride: Int) {
    private val start: Int = list.indexOf('S')
    private val lastIndex = list.size - 1

    private val direction = object {
        val Up = -stride
        val Right = 1
        val Down = stride
        val Left = -1

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = property.name.let {
            when (it) {
                "Up" -> Up
                "Right" -> Right
                "Down" -> Down
                "Left" -> Left
                else -> throw IllegalArgumentException(it)
            }
        }
    }

    inner class Segment(val index: Int, private val offset: Int) {
        operator fun invoke() = list[index + offset]

        override fun equals(other: Any?) = when {
            this === other -> true
            else -> (other as? Segment)?.let { index == other.index } ?: false
        }

        override fun hashCode(): Int {
            return index.hashCode()
        }
    }

    fun ArrayDeque<Int>.next(cell: Int) =
        next(cell, last()).let { cell + it }.also { if (it < 0 || this@Diagram.lastIndex < it) it.raise() }.also { this.addLast(cell) }

    fun loop(): Int {
        val routeA = ArrayDeque<Int>().apply { addLast(start) }
        val routeB = ArrayDeque<Int>().apply { addLast(start) }

        var (a, b) = next()

        while (true) {
            if (routeB.last() == a)
                return routeB.size - 1

            a = routeA.next(a)
            if (routeA.last() == b)
                return routeA.size - 1

            b = routeB.next(b)
        }
    }

    fun area() {
        val perimeter = getPerimeter()
        var cell = perimeter.max()
    }

    private fun Int.raise(): Nothing =
        throw IllegalArgumentException("(${this / stride},${this % stride}): ${list[this]}")

    private fun getPerimeter(): List<Int> {
        val perimeter = ArrayDeque<Int>()
        var cell = next()[0]
        do
            cell = perimeter.next(cell)
        while (start != cell)

        return perimeter
    }

    private fun next(cell: Int, previous: Int) =
        with(object { val less = previous < cell; val adjacent = 1 == abs(cell - previous)
            val Up by direction; val Right by direction; val Down by direction; val Left by direction }) {
            when (list[cell]) {
                '|' -> if (less) Down else Up
                '-' -> if (less) Right else Left
                'L' -> if (less) Right else Up
                '7' -> if (less) Down else Left
                'J' -> if (adjacent) Up else Left
                'F' -> if (adjacent) Down else Right
                else -> cell.raise()
        } }

    private fun next() = listOf(
        start - 1 to setOf('-', 'F', 'L'),
        start - stride to setOf('|', 'F', '7'),
        start + 1 to setOf('-', '7', 'J'),
        start + stride to setOf('|', 'L', 'J')).filter { with(it) {
        first in 0..lastIndex && list[first] in second
    } }.map { it.first }.apply { assert(2 == size) }
}

fun main() {
    val input = File("src/main/resources/day_10_input.txt").readLines()
    val diagram = Diagram(input.flatMap(String::asIterable), input[0].length)

    // Part I
    println(diagram.loop())

    // Part II
    print(diagram)
}