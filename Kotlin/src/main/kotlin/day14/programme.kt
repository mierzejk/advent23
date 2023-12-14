package day14

import java.io.File
import java.util.concurrent.ConcurrentHashMap

internal class Platform(private val tilt: Tilt, size: Int) {
    private val slope = MutableList(size) { 0 }
    private val lines = ArrayList<CharArray>()

    private companion object {
        private val cache = ConcurrentHashMap<String, Platform>()
        private fun nextFromCache(self: Platform, spun: () -> Platform) =
            cache.computeIfAbsent(self.key) { _ -> spun() }
    }

    enum class Tilt(val value: Int) {
        North(3),
        West(2),
        South(1),
        East(4);

        private companion object {
            private val map = entries.associateBy(Tilt::value)
            operator fun get(value: Int) = map[value]!!
        }

        val next: Tilt
            get() = get((value - 1).let { if (0 == it) 4 else it })
    }

    val load: ULong
        get() = when(tilt) {
            Tilt.North -> lines
            Tilt.East -> slope.indices.map(::rotatedRight)
            else -> TODO("West, South")
        }.let {facingNorth -> facingNorth.reversed().foldIndexed(0UL) { index, acc, chars ->
            (index.toULong() + 1UL) * chars.count { 'O' == it }.toULong() + acc }
        }

    private val key: String
        get() = StringBuilder("$tilt|").apply { lines.forEach(::append) }.toString()

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

    fun cycle(): Platform {
        var result = this
        repeat(tilt.value) { result = result.getSpun() }
        assert (Tilt.East == result.tilt)
        return result
    }

    private fun getSpun() = nextFromCache(this, ::spinOne)

    private fun spinOne() = Platform(tilt.next, lines.size).apply { slope.indices.forEach { i ->
        addLine(this@Platform.rotatedRight(i)) } }

    private fun rotatedRight(col: Int) = lines.reversed().map { it[col] }.toCharArray()
}

fun main() {
    File("src/main/resources/day_14_input.txt").useLines{ file ->
        val lines = file.iterator()
        var platform = lines.next().let {
            Platform(Platform.Tilt.North, it.length).apply { addLine(it.toCharArray()) } }
        lines.forEachRemaining { platform.addLine(it.toCharArray()) }

        // Part I
        println(platform.load)

        // Part II
        // Cycle detection
        val progression = ArrayList<Platform>()
        var startIndex: Int
        do {
            platform = platform.cycle().also { startIndex = progression.indexOf(it) }.also(progression::add)
        } while (-1 == startIndex)

        // Skip cycles, go only through the reminder.
        repeat((999999999 - startIndex) % (progression.lastIndex - startIndex)) {
            platform = platform.cycle()
        }
        println(platform.load)
    }
}