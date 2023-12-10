package day10

import java.io.File
import kotlin.math.abs

class Diagram(private val list: List<Char>, private val stride: Int) {
    private val start: Int = list.indexOf('S')
    private val lastIndex = list.size - 1

    fun loop(): Int {
        val routeA = ArrayDeque<Int>().apply { addLast(start) }
        val routeB = ArrayDeque<Int>().apply { addLast(start) }

        fun ArrayDeque<Int>.next(cell: Int) = next(cell, last()).also { this.addLast(cell) }

        var (a, b) = listOf(
            start - 1 to setOf('-', 'F', 'L'),
            start - stride to setOf('|', 'F', '7'),
            start + 1 to setOf('-', '7', 'J'),
            start + stride to setOf('|', 'L', 'J')).filter { with(it) {
                first in 0..lastIndex && list[first] in second
            } }.map { it.first }.apply { assert(2 == size) }

        while (true) {
            if (routeB.last() == a)
                return routeB.size - 1

            a = routeA.next(a)
            if (routeA.last() == b)
                return routeA.size - 1

            b = routeB.next(b)
        }
    }

    private fun next(cell: Int, previous: Int) =
        with(object { val less = previous < cell; val adjacent = 1 == abs(cell - previous); fun raise(): Nothing =
            throw IllegalArgumentException("(${cell / stride},${cell % stride}): ${list[cell]}") }) {
            when (list[cell]) {
                '|' -> if (less) cell + stride else cell - stride
                '-' -> if (less) cell + 1 else cell - 1
                'L' -> if (less) cell + 1 else cell - stride
                '7' -> if (less) cell + stride else cell - 1
                'J' -> if (adjacent) cell - stride else cell - 1
                'F' -> if (adjacent) cell + stride else cell + 1
                else -> raise()
            }.also { if (it < 0 || lastIndex < it) raise() }
        }
}

fun main() {
    val input = File("src/main/resources/test.txt").readLines()
    val diagram = Diagram(input.flatMap(String::asIterable), input[0].length)

    // Part I
    println(diagram.loop())
}