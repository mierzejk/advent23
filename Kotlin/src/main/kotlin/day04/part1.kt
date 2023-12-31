package day04

import java.io.File
import kotlin.math.pow

val colonSplit = Regex(""":\s+""")
val pipeSplit = Regex("""\s+\|\s+""")
val wsSplit = Regex("""\s+""")

fun intersectionSize(line: String) = line.split(pipeSplit).map {
        it.split(wsSplit).map(String::toInt) }.let {
            (a, b) -> a.toSet() intersect b.toSet() }.run { size }

fun Sequence<String>.winning() = this.map { it.split(colonSplit)[1] }.map(::intersectionSize)

fun main() {
    File("src/main/resources/day_4_input.txt").useLines { file ->
        file.winning().filter { it > 0 }.map { 2.0.pow(it - 1) }.sum().let(::println)
    }
}