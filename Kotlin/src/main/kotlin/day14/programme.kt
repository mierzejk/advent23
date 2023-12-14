package day14

import java.io.File

internal class Platform(private val tilt: Tilt, size: Int) {
    private val slope = MutableList(size) { 0 }
    private val lines = ArrayList<CharArray>()

    enum class Tilt { North, West, South, East }

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

    fun spinOne() = when (tilt) {
            // Spin West
            Tilt.North -> Tilt.West
//                Platform(Tilt.West, lines.size).apply {
//                slope.indices.forEach { i ->
//                    addLine(this@Platform.lines.reversed().map { it[i] }.toCharArray()) } }
            else -> throw IllegalArgumentException()
        }.let { tilt -> Platform(tilt, lines.size).apply { slope.indices.forEach { i ->
            addLine(this@Platform.lines.reversed().map { it[i] }.toCharArray()) } }
    }
}

fun main() {
    File("src/main/resources/test.txt").useLines{ file ->
        val lines = file.iterator()
        val platform = lines.next().let {
            Platform(Platform.Tilt.North, it.length).apply { addLine(it.toCharArray()) } }
        lines.forEachRemaining { platform.addLine(it.toCharArray()) }
        println(platform.load)

        val p2 = platform.spinOne()
        println(p2)
    }
}