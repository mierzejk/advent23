package day15

import java.io.File

private fun hash(acc: Long, c: Char) = (((acc + c.code.toLong()) * 17L) % 256L)

private fun hash(string: CharSequence): Long = string.fold(0L, ::hash)

fun main() {
    File("src/main/resources/day_15_input.txt").readText().filterNot { '\n' == it  }
        .split(',').sumOf(::hash).also(::println)
}
