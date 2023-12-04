package day04

import java.io.File

fun main() {
    File("src/main/resources/day_4_input.txt").useLines { file ->
        val cards = file.winning().toList()
        val multiple = IntArray(cards.size) { 1 }
        cards.forEachIndexed { i, c -> with(multiple[i]) {
            (i+1..i+c).forEach { multiple[it] += this }
        } }
        print(multiple.sum())
    }
}