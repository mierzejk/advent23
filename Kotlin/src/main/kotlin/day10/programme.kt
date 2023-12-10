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

    inner class Segment(val index: Int, private val dir: Int) {
        var symbol: Char = list[index]
            set(value) { validate(value).also { field = it } }

        init  { validate() }

        fun next() =
            next(index, index - dir).let { with(index + it) {
                if (this < 0 || lastIndex < this) this.raise() else Segment(this, it) } }

        override fun equals(other: Any?) = when {
            this === other -> true
            else -> (other as? Segment)?.let { index == other.index } ?: false
        }

        override fun hashCode(): Int {
            return index.hashCode()
        }

        override fun toString() = when(dir) {
            direction.Up -> '↑'
            direction.Right -> '→'
            direction.Down -> '↓'
            direction.Left -> '←'
            else -> throw IllegalArgumentException()
        }.let { "$it (${index / stride},${index % stride}): $symbol" }

        private fun validate(smb: Char = symbol) = with(direction) {
            when (smb) {
                '|' -> listOf(Down, Up)
                '-' -> listOf(Left, Right)
                'L' -> listOf(Down, Left)
                '7' -> listOf(Right, Up)
                'J' -> listOf(Down, Right)
                'F' -> listOf(Left, Up)
                'S' -> listOf(Up, Right, Down, Left)
                else -> throw IllegalArgumentException(this@Segment.toString())
            } }.also { assert(dir in it) }.let { smb }
    }

    fun ArrayDeque<Int>.next(cell: Int) = next(cell, last()).let { cell + it }.also {
        if (it < 0 || this@Diagram.lastIndex < it) it.raise() }.also { this.addLast(cell) }

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
        var perimeter = getPerimeter()
        val cornerIndex = perimeter.indexOfMaxBy(Segment::index)
        perimeter = perimeter.drop(cornerIndex) + perimeter.take(cornerIndex)
        print(perimeter[0])
    }

    private fun Int.raise(): Nothing =
        throw IllegalArgumentException("(${this / stride},${this % stride}): ${list[this]}")

    private fun getPerimeter() = buildList {
        val (a, b) = next()
        var segment = a.let { Segment(it, it - start) }.also(::addLast)
        do {
            segment = segment.next().also(::addLast)
        } while (start != segment.index)
        segment.apply {
            assert('S' == symbol)
            symbol = when(index - a) {
                stride -> when(b - index) { -1 -> 'J'; 1 -> 'L'; stride -> '|'; else -> throw IllegalArgumentException() }
                1 -> when(b - index) { 1 -> '-'; stride -> '7'; else -> throw IllegalArgumentException() }
                -1 -> when(b - index) { stride -> 'F'; else -> throw IllegalArgumentException()}
                else -> throw IllegalArgumentException()
            }
        }
    }

    private fun next(cell: Int, previous: Int) =
        with(object { val less = previous < cell; val adjacent = 1 == abs(cell - previous); val symbol = list[cell]
            val Up by direction; val Right by direction; val Down by direction; val Left by direction }) {
            when (symbol) {
                '|' -> if (less) Down else Up
                '-' -> if (less) Right else Left
                'L' -> if (less) Right else Up
                '7' -> if (less) Down else Left
                'J' -> if (adjacent) Up else Left
                'F' -> if (adjacent) Down else Right
                else -> cell.raise()
        } }

    private fun next() = listOf(
        start - stride to setOf('|', 'F', '7'),
        start - 1 to setOf('-', 'F', 'L'),
        start + 1 to setOf('-', '7', 'J'),
        start + stride to setOf('|', 'L', 'J')).filter { with(it) {
        first in 0..lastIndex && list[first] in second
    } }.map { it.first }.apply { assert(2 == size && get(0) < get(1)) }
}

internal fun<T, R: Comparable<R>> List<T>.indexOfMaxBy(selector: (T) -> R) = this.indexOf(this.maxBy(selector))

fun main() {
    val input = File("src/main/resources/test.txt").readLines()
    val diagram = Diagram(input.flatMap(String::asIterable), input[0].length)

    // Part I
    println(diagram.loop())

    // Part II
    diagram.area()
}