package day14

import java.io.File

internal class Platform(private val tilt: Tilt, size: Int) {
    private val slope = MutableList(size) { 0 }
    private val lines = ArrayList<CharArray>()

    enum class Tilt(val value: Int) {
        North(3),
        West(2),
        South(1),
        East(4);

        companion object {
            private val map = entries.associateBy(Tilt::value)
            operator fun get(value: Int) = map[value]!!
        }

        val next: Tilt
            get() = get((value - 1).let { if (0 == it) 4 else it })
    }

    val load: ULong
        get() = lines.reversed().foldIndexed(0UL) { index, acc, chars ->
            (index.toULong() + 1UL) * chars.count { 'O' == it }.toULong() + acc }

    fun addLine(line: CharArray) {
        lines.add(line)
        val nextLine = lines.size
        line.indices.forEach { i ->
            when (line[i]) {
                '.' -> Unit
                '#' -> slope[i] = nextLine
                'O' -> slope[i].run { line[i] = '.'; lines[this][i] = 'O'; slope[i] = this + 1 }
                else -> throw IllegalArgumentException(line.toString())
            }
        }
    }

    private fun spinOne() = Platform(tilt.next, lines.size).apply { slope.indices.forEach { i ->
        addLine(this@Platform.lines.reversed().map { it[i] }.toCharArray()) } }

    fun cycle(): Platform {
        var result = this
        repeat(tilt.value) { result = result.spinOne() }
        assert (Tilt.East == result.tilt)
        return result
    }
}

fun main() {
    File("src/main/resources/test.txt").useLines{ file ->
        val lines = file.iterator()
        val platform = lines.next().let {
            Platform(Platform.Tilt.North, it.length).apply { addLine(it.toCharArray()) } }
        lines.forEachRemaining { platform.addLine(it.toCharArray()) }
        println(platform.load)

        val p1 = platform.cycle()
        println(p1)
        val p2 = p1.cycle()
        println(p2)
        val p3 = p2.cycle()
        println(p3)

    }
}