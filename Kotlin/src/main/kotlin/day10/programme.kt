package day10

import java.io.File
import kotlin.math.abs
import kotlin.reflect.KProperty

class Diagram(private val list: List<Char>, private val stride: Int) {
    private val start: Int = list.indexOf('S')
    private val lastIndex = list.size - 1
    private val span = 0..lastIndex
    private val compassRose = listOf(-stride, -1, 1, stride)

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

    inner class Segment(val index: Int, val dir: Int) {
        var symbol: Char = list[index]
            set(value) { validate(value).also { field = it } }

        init  { validate() }

        fun next() =
            next(index, index - dir).let { with(index + it) {
                if (this !in span) this.raise() else Segment(this, it) } }

        fun getTiles(reversed: Boolean) = when(reversed) { true -> tilesReversed(); false -> tiles() }
            .map { index + it }.filter { it in span }

        private fun tiles() = with(direction) {
            when (symbol) {
                '|' -> when (dir) { Down -> listOf(Left); else -> listOf(Right) }
                '-' -> when (dir) { Left -> listOf(Up); else -> listOf(Down) }
                'L' -> when (dir) { Down -> listOf(Down, Left); else -> emptyList() }
                '7' -> when (dir) { Right -> emptyList(); else -> listOf(Up, Right) }
                'J' -> when (dir) { Down -> emptyList(); else -> listOf(Right, Down) }
                'F' -> when (dir) { Left -> listOf(Left, Up); else -> emptyList() }
                else -> throw IllegalArgumentException(this@Segment.toString())
            }
        }

        private fun tilesReversed() = with(direction) {
            when (symbol) {
                '|' -> when (dir) { Down -> listOf(Right); else -> listOf(Left) }
                '-' -> when (dir) { Left -> listOf(Down); else -> listOf(Up) }
                'L' -> when (dir) { Down -> emptyList(); else -> listOf(Down, Left) }
                '7' -> when (dir) { Right -> listOf(Up, Right); else -> emptyList() }
                'J' -> when (dir) { Down -> listOf(Right, Down); else -> emptyList() }
                'F' -> when (dir) { Left -> emptyList(); else -> listOf(Left, Up) }
                else -> throw IllegalArgumentException(this@Segment.toString())
            }
        }

        override fun equals(other: Any?) = when {
            this === other -> true
            else -> (other as? Segment)?.let { index == other.index } ?: false
        }

        override fun hashCode(): Int {
            return index.hashCode()
        }

        override fun toString() = when (dir) {
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
        if (it !in span) it.raise() }.also { this.addLast(cell) }

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

    fun area(): Int {
        var perimeter = getPerimeter()
        val cornerIndex = perimeter.indexOfMaxBy(Segment::index)
        perimeter = perimeter.drop(cornerIndex) + perimeter.take(cornerIndex)
        val corner = perimeter[0]
        assert('J' == corner.symbol)
        val reversed = corner.dir == direction.Right
        val perimeterSet = perimeter.map(Segment::index).toSet()
        val pending = (perimeter.map { it.getTiles(reversed) }.flatten().subtract(perimeterSet)).toMutableSet()
        val added = HashSet<Int>()

        while(pending.isNotEmpty()) {
            val tile = pending.pop().also(added::add)
            pending += compassRose.map { tile + it }.filter { it in span }.subtract(added union perimeterSet)
        }

        return added.size
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
            symbol = when (index - a) {
                stride -> when (b - index) { -1 -> 'J'; 1 -> 'L'; stride -> '|'; else -> throw IllegalArgumentException() }
                1 -> when (b - index) { 1 -> '-'; stride -> '7'; else -> throw IllegalArgumentException() }
                -1 -> when (b - index) { stride -> 'F'; else -> throw IllegalArgumentException()}
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
        first in span && list[first] in second
    } }.map { it.first }.apply { assert(2 == size && get(0) < get(1)) }
}

internal fun<T, R: Comparable<R>> List<T>.indexOfMaxBy(selector: (T) -> R) = this.indexOf(this.maxBy(selector))

internal fun<T> MutableCollection<T>.pop() = with(iterator()) { next().also { remove() } }

fun main() {
    val input = File("src/main/resources/day_10_input.txt").readLines()
    val diagram = Diagram(input.flatMap(String::asIterable), input[0].length)

    // Part I
    println(diagram.loop())

    // Part II
    println(diagram.area())
}