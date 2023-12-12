package day12

import java.io.File
import kotlin.math.max
import kotlin.math.min

internal class DefaultMap<K, V>(private val defaultValue: (key: K) -> V): HashMap<K, V>() {
    override fun get(key: K) = super.get(key) ?: defaultValue(key).also { this[key] = it }
}

@Suppress("RegExpSimplifiable")
internal val regexMap = DefaultMap<Int, Regex> { Regex("""[#?]{$it}(\.|\?|$)""") }

internal fun String.matches(at: Int, erasureCode: Int) =
    regexMap.getValue(erasureCode).matches(slice(at..min(at + erasureCode, lastIndex)))

internal fun String.backtrack(at: Int, erasureCodes: List<Int>): Int {
    if (erasureCodes.isEmpty())
        return if (at < length && '#' in substring(at)) 0 else 1

    var total = 0
    erasureCodes[0].let { code ->
        for (i in (at..length - code)
            .filter { '#' !in substring(at, max(at, it)) && '.' != this[it] && matches(it, code)}) {
            total += backtrack(i + code + 1, erasureCodes.drop(1))
        }
    }

    return total
}

fun main() {
    val op = File("src/main/resources/day_12_input.txt").useLines { file ->
        file.map { it.split(' ').let { (line, codes) ->
            line.backtrack(0, codes.split(',').map(String::toInt)) }}.toList() }
    println(op.sum())
    // 10185 your answer is too high
}