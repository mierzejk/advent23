package day18

import DefaultMap
import bisectDistinct
import java.io.File

internal data class Corner(val y: ULong, val x: ULong) {
    fun getNext(dir: String, len: ULong) = when(dir) {
        "R" -> Corner(y, x+len)
        "D" -> Corner(y+len, x)
        "L" -> Corner(y, x-len)
        "U" -> Corner(y-len, x)
        else -> throw IllegalArgumentException(dir)
    }
}

fun main() {
    val corners = DefaultMap<ULong, List<ULong>> { listOf() }
    fun addCorner(element: Corner) {
        corners[element.y] = corners[element.y].bisectDistinct(element.x)
    }

    var corner = Corner(0UL, 0UL)
    File("src/main/resources/test.txt").useLines { file -> file.forEach { line ->
        val (dir, len) = lineRe.matchEntire(line)!!.groupValues.slice(1..2)
        corner = corner.getNext(dir, len.toULong()).also(::addCorner)
    } }

    print(corners)
}