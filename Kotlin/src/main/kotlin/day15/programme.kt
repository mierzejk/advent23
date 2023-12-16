package day15

import java.io.File

private fun hash(string: CharSequence): Int = string.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

fun main() {
    val input = File("src/main/resources/day_15_input.txt").readText().filterNot { '\n' == it  }
        .split(',')

    // Part I
    input.sumOf(::hash).also(::println)

    // Part II
    val boxes = List(256) { LinkedHashMap<String, Int>(5) }
    input.forEach {
        val label = it.takeWhile(Char::isLetter)
        val box = boxes[hash(label)]
        when (it[label.length]) {
            '-' -> box.remove(label)
            else -> box[label] = it.takeLast(1).toInt()
        }
    }

    boxes.mapIndexed { i, box -> (i + 1) * box.values.foldIndexed(0) { j, acc, fl -> fl * (j + 1) + acc } }
        .sum().also(::println)
}
