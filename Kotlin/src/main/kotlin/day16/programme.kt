package day16

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val input = File("src/main/resources/day_16_input.txt").readLines()
    val contraption = input.joinToString("").toCharArray()
    val stride = input[0].length
    val direction = object { val Up = -stride; val Right = 1; val Down = stride; val Left = -1 }

    data class Beam(val pos: Int, val dir: Int) {
        private fun Int.move() = Beam(pos + this, this)

        @Suppress("SpellCheckingInspection")
        fun geradeaus() = listOf(dir.move())
        fun up() = listOf(direction.Up.move())
        fun right() = listOf(direction.Right.move())
        fun down() = listOf(direction.Down.move())
        fun left() = listOf(direction.Left.move())
        fun vertical() = listOf(direction.Up.move(), direction.Down.move())
        fun horizontal() = listOf(direction.Left.move(), direction.Right.move())
    }

    fun trace(beam: Beam): Int {
        val queue = ArrayDeque<Beam>(12).apply { add(beam) }
        val visited = List(contraption.size) { mutableSetOf<Int>() }.apply { this[0] += beam.dir }

        while (queue.isNotEmpty()) {
            queue.removeFirst().run {
                when (contraption[pos]) {
                    '.' -> geradeaus()
                    '|' -> when (dir) {
                        direction.Up, direction.Down -> geradeaus()
                        direction.Left, direction.Right -> vertical()
                        else -> throw IllegalArgumentException("$dir")
                    }

                    '-' -> when (dir) {
                        direction.Left, direction.Right -> geradeaus()
                        direction.Up, direction.Down -> horizontal()
                        else -> throw IllegalArgumentException("$dir")
                    }

                    '/' -> when (dir) {
                        direction.Up -> right()
                        direction.Right -> up()
                        direction.Down -> left()
                        direction.Left -> down()
                        else -> throw IllegalArgumentException("$dir")
                    }

                    '\\' -> when (dir) {
                        direction.Up -> left()
                        direction.Right -> down()
                        direction.Down -> right()
                        direction.Left -> up()
                        else -> throw IllegalArgumentException("$dir")
                    }

                    else -> throw IllegalArgumentException("(${pos / stride},${pos % stride}): ${contraption[pos]}")
                }
            }.filter {
                when {
                    it.pos !in contraption.indices -> false
                    direction.Left == it.dir && 0 == (it.pos + 1) % stride -> false
                    direction.Right == it.dir && 0 == (it.pos) % stride -> false
                    it.dir in visited[it.pos] -> false
                    else -> true
                }
            }.forEach {
                visited[it.pos] += it.dir
                queue.add(it)
            }
        }

        return visited.count { it.isNotEmpty() }
    }

    measureTimeMillis {
        trace(Beam(0, direction.Right)).also(::println)
    }.also { println("timeit: $it") }

    // Part II
    measureTimeMillis {
        listOf(
            input.indices.map { Beam(it * stride, direction.Right) },
            (0..<stride).map { Beam(it, direction.Down) },
            (1..stride).map { Beam(contraption.size - it, direction.Up) },
            (1..input.size).map { Beam(it * stride - 1, direction.Left) }
        ).flatMap { it.map(::trace) }.max().also { println("Max: $it") }
    }.also { println("timeit: $it") }
}
