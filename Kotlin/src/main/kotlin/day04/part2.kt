package day04

import java.io.File

fun main() {
    File("src/main/resources/day_4_input.txt").useLines { file ->
        val cards = file.map { it.split(colonSplit)[1] }.map(::intersection).map(Set<Int>::size).toList()
        val multiple = IntArray(cards.size) { 1 }
        cards.forEachIndexed { i, c -> with(multiple[i]) {
            (i+1..i+c).forEach { multiple[it] += this }
        } }
        print(multiple.sum())
    }
}