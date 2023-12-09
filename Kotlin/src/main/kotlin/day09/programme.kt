package day09

import java.io.File

fun getPrediction(collection: List<Long>): Long = with (collection) {
    if (1 == toSet().size)
        get(0)
    else
        last() + getPrediction(collection.zipWithNext().map { (a, b) -> b - a })
    }

fun main() {
    fun Sequence<String>.total(backwards: Boolean = false) = map { with ( when(backwards)
        { false -> it.split(' '); true -> it.split(' ').asReversed() })
        { map(String::toLong).let(::getPrediction) } }.sum()

    // Part I
    File("src/main/resources/day_9_input.txt").useLines { it.total() }.also(::println)

    // Part II
    File("src/main/resources/day_9_input.txt").useLines { it.total(backwards = true) }.also(::println)
}