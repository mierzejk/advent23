import java.io.File

val digits = arrayOf(
    "1", "one",
    "2", "two",
    "3", "three",
    "4", "four",
    "5", "five",
    "6", "six",
    "7", "seven",
    "8", "eight",
    "9", "nine")
val mapper = (digits.filterIndexed{ i, _ -> 1 == i%2 } zip digits.filterIndexed{ i, _ -> 0 == i%2 }).toMap()
val pattern = Regex(digits.joinToString("|"), RegexOption.IGNORE_CASE)

fun String.getMatches() = buildList {
    var i = 0
    while (i < length)
        pattern.matchAt(this@getMatches, i)?.apply { add(value); i = range.first + 1 } ?: i++
}

fun main() {
    File("src/main/resources/day_1_input.txt").useLines { file ->
        file.map(String::getMatches).map { matches ->
            val first = matches.first().let { mapper.getOrDefault(it, it) }
            val last = matches.last().let { mapper.getOrDefault(it, it) }
            "$first$last".toInt() }.sum().let(::println)
    }
}