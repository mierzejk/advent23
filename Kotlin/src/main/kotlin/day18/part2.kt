package day18

import DefaultSortedMap
import bisectDistinct
import java.io.File
import kotlin.math.max
import kotlin.math.min

internal data class Corner(val y: ULong, val x: ULong) {
    fun getNext(dir: String, len: ULong) = when(dir) {
        "R" -> Corner(y, x+len)
        "D" -> Corner(y+len, x)
        "L" -> Corner(y, x-len)
        "U" -> Corner(y-len, x)
        else -> throw IllegalArgumentException(dir)
    }
}

internal data class Line(val top: ULong, val left: ULong, val right: ULong) {
    operator fun minus(other: Line): Pair<ULong, List<Line>> {
        val start = max(left, other.left)
        val end = min(right, other.right)
        if (end < start)
            return Pair(0UL, listOf(this))

        return Pair((1UL + end - start) * (other.top - top - 1UL), buildList {
            if (left < start) add(Line(top, left, start - 1UL))
            if (end < right) add(Line(top, end + 1UL, right))
        })
    }
}

fun main() {
    val corners = DefaultSortedMap<ULong, List<ULong>>(::listOf)
    fun addCorner(element: Corner) {
        corners[element.y] = corners[element.y].bisectDistinct(element.x)
    }

    var corner = Corner(0UL, 0UL)
    File("src/main/resources/test.txt").useLines { file -> file.forEach { line ->
        val (dir, len) = lineRe.matchEntire(line)!!.groupValues.slice(1..2)
        corner = corner.getNext(dir, len.toULong()).also(::addCorner)
    } }

    var lines = emptyList<Line>()
    for ((index, values) in corners) {
        val newLines = values.zipWithNext().filterIndexed { i, _ -> 0 == i % 2 }.map { Line(index, it.first, it.second) }

        val exiting = lines.flatMap { line -> newLines.map { it - line } }
        val area = exiting.sumOf { (rectangle, _) -> rectangle  }

        println(newLines)
    }
}