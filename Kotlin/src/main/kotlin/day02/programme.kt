package day02

import java.io.File

operator fun MatchResult.get(name: String) = this.groups[name]!!.value

val colour = Regex("""(?<value>\d+)\s+(?<key>\w+)""")

val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)

fun getMax(data: String) = colour.findAll(data).groupBy({ it["key"] }) {
    it["value"].toInt() }.mapValues { (_, v) -> v.max() }.withDefault { 0 }

fun process1(line: String): Int {
    val (game, data) = line.split(": ")
    val id = game.substring(5).toInt()
    getMax(data).forEach { (k, v) -> if (limits[k]!! < v) return 0 }
    return id
}

fun process2(line: String) = getMax(line.split(": ")[1]).values

fun main() {
    // Part 1
    File("src/main/resources/day_2_input.txt").useLines { file ->
        file.map(::process1).sum().apply(::println)
    }
    // Part 2
    File("src/main/resources/day_2_input.txt").useLines { file ->
        file.map(::process2).map { it.fold(1) { acc, i -> acc * i } }.sum().let(::println)
    }
}