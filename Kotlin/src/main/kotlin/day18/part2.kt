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

internal data class Line(var left: Long, val right: Long)

fun main() {
    val corners = DefaultSortedMap<Long, List<Long>>{ emptyList() }
    fun addCorner(element: Corner) {
        corners[element.y] = corners[element.y].bisectDistinct(element.x)
    }

    var corner = Corner(0L, 0L)
    File("src/main/resources/day_18_input.txt").useLines { file -> file.forEach { line ->
        val (dir, len) = lineRe.matchEntire(line)!!.groupValues.slice(1..2)
        corner = corner.getNext(dir, len.toLong()).also(::addCorner)
    } }
    assert(corner == Corner(0L, 0L))

    var lines = ArrayDeque<Line>()
    var lastIndex = 1L
    var area = 0L
    for ((index, values) in corners) {
        var sum = lines.sumOf { 1L + it.right - it.left } * max((index - lastIndex - 1L), 0L)

        val pending = ArrayDeque<Line>()
        val newLines = values.zipWithNext().filterIndexed { i, _ -> 0 == i % 2 }.map { Line(it.first, it.second) }

        val linesUnion = listOf(lines, newLines).flatten().sortedBy { it.left }
        if (1 < linesUnion.size) {
            var current = linesUnion[0]
            sum += buildList {
                linesUnion.drop(1).forEach {
                    if (it.left <= current.right)
                        current = Line(current.left, max(it.right, current.right))
                    else {
                        add(current)
                        current = it
                    }
                }
                add(current)
            }.sumOf { 1L + it.right - it.left }
        }
        else
            sum += newLines.sumOf { 1L + it.right - it.left }

        newLines@ for (newLine in newLines) {
            while (lines.isNotEmpty()) {
                val first = lines.removeFirst()
                if (newLine.right < first.left) {
                    pending.add(Line(newLine.left, newLine.right))
                    lines.addFirst(first)
                    continue@newLines
                }
                if (newLine.right == first.left) {
                    pending.add(Line(newLine.left, newLine.right - 1L))
                    lines.addFirst(first)
                    continue@newLines
                }

                if (newLine.left > first.right) {
                    pending.add(first)
                    continue
                }
                if (newLine.left == first.right) {
                    newLine.left += 1L
                    pending.add(first)
                    continue
                }

                assert(first.left <= newLine.left)
                assert(newLine.right <= first.right)
                assert(!(first.left == newLine.left && first.right == newLine.right))
                // pending left
                if (first.left < newLine.left) {
                    pending.add(Line(first.left, newLine.left))
                }
                // pending right
                if (newLine.right < first.right) {
                    lines.addFirst(Line(newLine.right, first.right))
                }

                continue@newLines
            }

            pending.add(Line(newLine.left, newLine.right))
        }

        pending.addAll(lines)
        lines = ArrayDeque()
        // Merge adjacent lines.
        if (pending.isNotEmpty()) {
            var current = pending.first()
            for (line in pending.drop(1)) {
                if (current.right == line.left - 1L)
                    current = Line(current.left, line.right)
                else {
                    lines.add(current)
                    current = line
                }
            }

            lines.add(current)
        }

        print("$index: $area + $sum = ")
        area += sum
        println(area)

        println(lines)
        lastIndex = index
    }
    print(area)
}