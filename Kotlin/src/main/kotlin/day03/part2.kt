package day03

import java.io.File

val numberWindow = ArrayDeque<List<Number>>(3)
fun loadWindowLine(line: String) = numberWindow.apply { if (3 == size) removeFirst() }
    .addLast(loadNumbers(line).toList())

fun adjacent(index: Int) =
    numberWindow.flatten().filter { it.from - 2 < index && index < it.to + 2 }

fun main() {
    numberWindow.addLast(emptyList())
    val numbers = object: Sequence<Int> {
        private var values: List<Int> = emptyList()

        fun loadFrom(line: String) { values = line.mapIndexedNotNull { index, c -> if ('*' == c) index else null } }
        override fun iterator(): Iterator<Int> =
            values.map(::adjacent).filter { 2 == it.size }.map { (a, b) -> a * b }.iterator()
    }

    @Suppress("DuplicatedCode")
    fun Sequence<String>.toNumbers() = sequence {
        for (line in this@toNumbers.onEach(::loadWindowLine)) {
            if (2 < numberWindow.size)
                yieldAll(numbers)

            numbers.loadFrom(line)
        }

        numberWindow.removeFirst()
        numberWindow.addLast(emptyList())
        yieldAll(numbers)
    }

    File("src/main/resources/day_3_input.txt").useLines { file ->
        file.toNumbers().sum().let(::println)
    }
}