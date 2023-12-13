package day13

import manacher
import java.io.File

fun<T> getReflection(input: List<T>, bogus: T) = manacher(input, bogus).run {
        mapIndexed { index, i -> index to i }.
        filter { (index, i) -> 1 == index % 2 && 2 * i in listOf(index + 1, size - index) }.
        maxByOrNull(Pair<Int, Int>::second)?.first ?: -1
    }.let { (it + 1) / 2 }

data class Pattern<T>(val bogus: T, val rows: List<T>, val cols: List<T>) {
    val horizontalReflection: Int
        get() = getReflection(rows, bogus)

    val verticalReflection: Int
        get() = getReflection(cols, bogus)
}

fun Char.ToBit() = when(this) { '#' -> '1' else -> '0' }

fun readPattern(chars: List<Char>, stride: Int) = Pattern(bogus = -1,
        (chars.indices step stride).map { (it..<it + stride).map { i ->
            chars[i].ToBit() }.joinToString("").toInt(2) },
        (0..<stride).map { (it..chars.lastIndex step stride).map { i ->
            chars[i].ToBit() }.joinToString("").toInt(2) })

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
    patterns.sumOf { 100UL * it.horizontalReflection.toULong() + it.verticalReflection.toULong() }.also(::println)
}