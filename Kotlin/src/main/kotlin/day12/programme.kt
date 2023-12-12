package day12

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

internal class DefaultMap<K, V>(private val defaultValue: (key: K) -> V): HashMap<K, V>() {
    override fun get(key: K) = super.get(key) ?: defaultValue(key).also { this[key] = it }
}

val cache = ConcurrentHashMap<String, ULong>()

@Suppress("RegExpSimplifiable")
internal val regexInterning = DefaultMap<Int, Regex> { Regex("""[#?]{$it}(\.|\?|$)""") }

internal fun String.matches(at: Int, erasureCode: Int) =
    regexInterning[erasureCode].matches(slice(at..min(at + erasureCode, lastIndex)))

internal fun String.backTrack(at: Int, codesLeft: List<Int>): ULong = when {
    // Accept the branch (+1) only if there is no '#' remaining in the line ahead; otherwise return 0.
    codesLeft.isEmpty() -> if('#' in substring(min(at, length))) 0UL else 1UL
    else -> (if (lastIndex < at) "" else "${this.substring(at)}%${codesLeft.joinToString(",")}").let {
        cacheKey -> cache[cacheKey] ?: codesLeft[0].let { code ->
            (at..length - code)
                // Follow a branch only if this is an acceptable starting point and there is no '#' left from the last covered span.
                .filter { '#' !in substring(at, max(at, it)) && '.' != this[it] && matches(it, code) }
                .sumOf { backTrack(it + code + 1, codesLeft.drop(1)) }
        }.also { cache.putIfAbsent(cacheKey, it) }
    }
}

fun main() {
    File("src/main/resources/day_12_input.txt").useLines { file ->
        file.map { it.split(' ')
            // Part II
            .let { (line, codes) -> Pair(
                List(5){ _ -> line }.joinToString("?"),
                List(5){ _ -> codes }.joinToString(",")) }
            .let { (line, codes) -> line.backTrack(0, codes.split(',').map(String::toInt))
            } }.sum() }.also(::println)
}