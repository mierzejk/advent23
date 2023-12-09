package day09

import java.io.File

fun getPrediction(collection: List<Long>, backwards: Boolean = false): Long = with (collection) {
    if (1 == toSet().size)
        get(0)
    else
        getPrediction(collection.zipWithNext().map { (a, b) -> b - a }, backwards).let { when (backwards) {
            true -> get(0) - it
            false -> last() + it
        } }
}

fun main() {
    fun backwards(collection: List<Long>) = getPrediction(collection, true)
    val total: Sequence<String>.((List<Long>) -> Long) -> Long = {
        func -> this.map { it.split(' ').map(String::toLong).let(func) }.sum() }

    // Part I
    File("src/main/resources/day_9_input.txt").useLines { it.total(::getPrediction) }.also(::println)

    // Part II
    File("src/main/resources/day_9_input.txt").useLines { it.total(::backwards) }.also(::println)
}