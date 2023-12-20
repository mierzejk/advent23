package day18

import java.io.File
import pop

internal data class Point(val y: Int, val x: Int) {
    fun getNext(dir: String) = when(dir) {
        "R" -> Point(y, x+1)
        "D" -> Point(y+1, x)
        "L" -> Point(y, x-1)
        "U" -> Point(y-1, x)
        else -> throw IllegalArgumentException(dir)
    }

    val siblings by lazy { listOf(
        Point(y, x+1),
        Point(y+1, x),
        Point(y, x-1),
        Point(y-1, x)) }
}

private val lineRe: Regex = Regex("""(?<dir>[RDLU]) (?<len>\d+) \(#(?<colour>\w+)\)""")

internal fun getAdjacent(side: String): Point.(String) -> Point {
    fun Point.rightAdjacent(dir: String) = when (dir) {
        "R" -> Point(y+1, x)
        "D" -> Point(y, x-1)
        "L" -> Point(y-1, x)
        "U" -> Point(y, x+1)
        else -> throw IllegalArgumentException(dir)
    }
    fun Point.leftAdjacent(dir: String) = when (dir) {
        "R" -> Point(y-1, x)
        "D" -> Point(y, x+1)
        "L" -> Point(y+1, x)
        "U" -> Point(y, x-1)
        else -> throw IllegalArgumentException(dir)
    }
    return when (side) {
        "R" -> Point::rightAdjacent
        "D" -> Point::leftAdjacent
        else -> throw IllegalArgumentException(side)
    }
}

fun main() {
    val interior = mutableSetOf<Point>()
    val floodSeeds = mutableSetOf<Point>()

    File("src/main/resources/day_18_input.txt").useLines { file ->
        file.iterator().run {
            var point = Point(0, 0)
            var line = lineRe.matchEntire(next())!!.groupValues.slice(1..2)
            val adjacent = getAdjacent(line[0])

            do {
                val dir = line[0]
                for (i in 1..line[1].toInt()) {
                    point = point.getNext(dir).also(interior::add).also { floodSeeds.add(adjacent(it,dir)) }
                }
            } while(hasNext().also { if (it) { line = lineRe.matchEntire(next())!!.groupValues.slice(1..2) }})
            assert(point == Point(0, 0))

            floodSeeds.removeAll(interior)
            while (floodSeeds.isNotEmpty()) {
                point = floodSeeds.pop().also(interior::add)
                floodSeeds.addAll(point.siblings subtract interior)
            }
        }
    }

    println(interior.size)
}