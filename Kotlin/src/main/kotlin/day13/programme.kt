package day13

import manacher
import java.io.File

fun getReflection(input: List<UInt>) = manacher(input, UInt.MAX_VALUE).run {
        mapIndexed { index, i -> index to i }.
        filter { (index, i) -> 1 == index % 2 && 2 * i in listOf(index + 1, size - index) }.
        maxByOrNull(Pair<Int, Int>::second)?.first ?: -1
    }.let { (it + 1) / 2 }

data class Pattern(val rows: List<UInt>, val cols: List<UInt>) {
    val horizontalReflection: Int
        get() = getReflection(rows)

    val verticalReflection: Int
        get() = getReflection(cols)
}

fun readPattern(chars: List<Char>, stride: Int) = (chars.size / stride).let { cols -> Pattern(
        (chars.indices step stride).map { (it..<it + stride).fold(0U) { acc, i ->
                acc + if ('#' == chars[i]) 1U shl i % stride else 0U } },
        (0..<stride).map { (it..chars.lastIndex step stride).fold(0U) { acc, i ->
                acc + if ('#' == chars[i]) 1U shl (i - it) % cols else 0U } })
}

fun main() {
    val sb = StringBuilder()
    var stride = 0
    val patterns = buildList {
        File("src/main/resources/day_13_input.txt").useLines { file ->
            for (line in file) {
                if (0 == stride)
                    stride = line.length
                if (line.isEmpty()) {
                    add(readPattern(sb.toList(), stride))
                    stride = 0
                    sb.clear()
                } else
                    sb.append(line)
            }
            add(readPattern(sb.toList(), stride))
        }
    }

    val op = patterns.filter { 0 == it.horizontalReflection + it.verticalReflection }
    patterns.forEach{p -> println("${p.verticalReflection} / ${p.horizontalReflection}")}
    patterns.sumOf { 100UL * it.horizontalReflection.toULong() + it.verticalReflection.toULong() }.also(::println)
}