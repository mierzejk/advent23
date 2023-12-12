package day12

import java.io.File
import kotlin.math.*

internal class DefaultMap<K, V>(private val defaultValue: (key: K) -> V): HashMap<K, V>() {
    override fun get(key: K) = super.get(key) ?: defaultValue(key).also { this[key] = it }
}

@Suppress("RegExpSimplifiable")
internal val regexMap = DefaultMap<Int, Regex> { Regex("""[#?]{$it}(\.|\?|$)""") }

internal fun String.matches(at: Int, erasureCode: Int) =
    regexMap[erasureCode].matches(slice(at..min(at + erasureCode, lastIndex)))

internal fun String.backtrack(at: Int, codesLeft: List<Int>): Int = when {
    // Accept the branch (+1) only if there is no '#' remaining in the line remainder ahead; otherwise return 0.
    codesLeft.isEmpty() -> ('#' !in substring(min(at, length))).compareTo(false)
    else -> codesLeft[0].let { code -> (at..length - code)
        // Follow a branch only if this is an acceptable starting point and there is no '#' left from the last covered span.
        .filter { '#' !in substring(at, max(at, it)) && '.' != this[it] && matches(it, code) }
        .sumOf { backtrack(it + code + 1, codesLeft.drop(1)) }
    }
}

fun main() {
    // Part I
    File("src/main/resources/day_12_input.txt").useLines { file ->
        file.map { it.split(' ').let { (line, codes) ->
            line.backtrack(0, codes.split(',').map(String::toInt)) }}.sum() }
        .also(::println)
}