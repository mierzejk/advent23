package day18

import DefaultSortedMap
import bisectDistinct
import java.io.File
import kotlin.math.max

internal data class Corner(val y: Long, val x: Long) {
    fun getNext(dir: String, len: Long) = when(dir) {
        "R" -> Corner(y, x+len)
        "D" -> Corner(y+len, x)
        "L" -> Corner(y, x-len)
        "U" -> Corner(y-len, x)
        else -> throw IllegalArgumentException(dir)
    }
}

internal data class Line(var left: Long, val right: Long) {
    val length = 1L + right - left
}

internal fun String.mapColour() = listOf(
    when (this[5]) {
        '0' -> "R"
        '1' -> "D"
        '2' -> "L"
        '3' -> "U"
        else -> throw IllegalArgumentException(this)
    },
    this.take(5)
)

fun main() {
    val corners = DefaultSortedMap<Long, List<Long>>{ emptyList() }
    fun addCorner(element: Corner) {
        corners[element.y] = corners[element.y].bisectDistinct(element.x)
    }

    var corner = Corner(0L, 0L)
    File("src/main/resources/day_18_input.txt").useLines { file -> file.forEach { line ->
        val (dir, len) = lineRe.matchEntire(line)!!.groups["colour"]!!.value.mapColour()
        corner = corner.getNext(dir, len.toLong(radix=16)).also(::addCorner)
    } }
    assert(corner == Corner(0L, 0L)) // Assert cycle

    var lines = ArrayDeque<Line>()
    var lastIndex = 0L
    var area = 0L
    for ((index, values) in corners) {
        // Skipped lines
        var sum = lines.sumOf(Line::length) * max((index - lastIndex - 1L), 0L)
        val pending = ArrayDeque<Line>()
        val newLines = values.zipWithNext().filterIndexed { i, _ -> 0 == i % 2 }.map { Line(it.first, it.second) }

        // Current line
        sum += listOf(lines, newLines).flatten().sortedBy(Line::left).run { when (size) {
            in 0..1 -> asSequence()
            else -> sequence {
                var current = get(0)
                drop(1).forEach {
                    // Merge overlapping lines.
                    if (it.left <= current.right)
                        current = Line(current.left, max(it.right, current.right))
                    else {
                        yield(current)
                        current = it
                    }
                }
                yield(current)
            }
        } }.sumOf(Line::length)

        newLines@for (newLine in newLines) {
            while (lines.isNotEmpty()) {
                val first = lines.removeFirst()
                // Completely to the left.
                if (newLine.right < first.left) {
                    pending.add(Line(newLine.left, newLine.right))
                    lines.addFirst(first)
                    continue@newLines
                }
                // To the left, border overlaps.
                if (newLine.right == first.left) {
                    pending.add(Line(newLine.left, newLine.right - 1L))
                    lines.addFirst(first)
                    continue@newLines
                }

                // Completely to the right.
                if (newLine.left > first.right) {
                    pending.add(first)
                    continue
                }
                // To the right, border overlaps.
                if (newLine.left == first.right) {
                    newLine.left += 1L
                    pending.add(first)
                    continue
                }

                assert(first.left <= newLine.left)
                assert(newLine.right <= first.right)
                assert(!(first.left == newLine.left && first.right == newLine.right))
                // Left difference.
                if (first.left < newLine.left) {
                    pending.add(Line(first.left, newLine.left))
                }
                // Right difference.
                if (newLine.right < first.right) {
                    lines.addFirst(Line(newLine.right, first.right))
                }

                continue@newLines
            }
            // All new lines to the right.
            pending.add(Line(newLine.left, newLine.right))
        }
        // All lines to the right.
        pending.addAll(lines)

        // Merge adjacent lines.
        lines = ArrayDeque<Line>().apply { if (pending.isNotEmpty()) {
            var current = pending[0]
            pending.drop(1).forEach {
                // Adjacent
                if (current.right == it.left - 1L)
                    current = Line(current.left, it.right)
                // Disjoint
                else {
                    add(current)
                    current = it
                }
            }
            add(current)
        } }

        area += sum
        lastIndex = index
    }
    print(area)
}