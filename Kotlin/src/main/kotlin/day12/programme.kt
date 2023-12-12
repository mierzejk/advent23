package day12

import java.io.File
import kotlin.math.min

internal class DefaultMap<K, V>(private val defaultValue: (key: K) -> V): HashMap<K, V>() {
    override fun get(key: K) = super.get(key) ?: defaultValue(key).also { this[key] = it }
}

@Suppress("RegExpSimplifiable")
internal val regexMap = DefaultMap<Int, Regex> { Regex("""[#?]{$it}(\.|\?|$)""") }

internal fun String.matches(at: Int, erasureCode: Int) =
    regexMap.getValue(erasureCode).matches(slice(at..min(at + erasureCode, lastIndex)))

internal fun String.backtrack(at: Int, codeIndex: Int, erasureCodes: List<Int>): Int {
    if (codeIndex == erasureCodes.size)
//        return 1
        return if (at < length && '#' in substring(at)) 0 else 1

    var total = 0
    erasureCodes[codeIndex].let { code ->
        for (i in at..length - code) {
            val groups = Regex("""#+""").findAll(substring(0, i)).count()
            if (codeIndex < groups)
                break

            if ('.' == this[i] || 0 < i && '#' == this[i-1] || !matches(i, code)) // Is correct starting point?
                continue

            total += backtrack(i + code + 1, codeIndex + 1, erasureCodes)
        }
    }

    return total
}

fun main() {
    val op = File("src/main/resources/test.txt").useLines { file ->
        file.take(10).map { it.split(' ').let { (line, codes) ->
            line.backtrack(0, 0, codes.split(',').map(String::toInt)) }}.toList() }
    println(op)
    // 10185 your answer is too high
}