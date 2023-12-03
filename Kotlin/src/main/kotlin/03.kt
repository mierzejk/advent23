import java.io.File

class Number (val value: Int, range: IntRange) {
    val from by range::first
    val to by range::last
}

val numbers = Regex("""\d+""")
fun loadNumbers(line: String) =
    numbers.findAll(line).map { Number(it.value.toInt(), it.range) }

val window = ArrayDeque<List<Int>>(3)
fun loadSymbols(line: String) {
    if (3 == window.size)
        window.removeFirst()

    val symbols = line.mapIndexedNotNull { index, c -> when(c.code) {
        46, in 48..57 -> null  // . → 46, 0..9 → 48..57
        else -> index } }
    window.addLast(symbols)
}

fun adjacent(number: Number) =
    with(number) { window.flatten().any { from - 2 < it && it < to + 2 } }

fun main() {
    window.addLast(emptyList())
    val numbers = object: Sequence<Int> {
        private var values: Sequence<Number> = emptySequence()

        fun loadFrom(line: String) = run { values = loadNumbers(line) }
        override fun iterator(): Iterator<Int> = values.filter(::adjacent).map(Number::value).iterator()
    }

    fun Sequence<String>.toNumbers() = sequence {
        for (line in this@toNumbers.onEach(::loadSymbols)) {
            if (2 < window.size)
                yieldAll(numbers)

            numbers.loadFrom(line)
        }

        window.removeFirst()
        window.addLast(emptyList())
        yieldAll(numbers)
    }

    File("src/main/resources/day_3_input.txt").useLines { file ->
        file.toNumbers().sum().let(::println)
    }
}