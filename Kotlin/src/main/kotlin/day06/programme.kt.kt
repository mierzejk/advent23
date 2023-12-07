package day06

import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

internal enum class Direction {
    Less,
    Greater
}

internal fun Double.nextULong(direction: Direction) =
    when (direction) {
        Direction.Less -> ceil(this - 1.0)
        Direction.Greater -> floor(this + 1.0)
    }.let(Double::toULong)

internal fun nextLess(value: Double) = value.nextULong(Direction.Less)
internal fun nextGreater(value: Double) = value.nextULong(Direction.Greater)

private data class Race(val time: ULong, val distance: ULong) {
    val range: ULongRange
    val waysCount: ULong

    init {
        val delta = time * time - 4UL * distance
        range = if (delta > 0UL) {
            @Suppress("SpellCheckingInspection")
            val dsqrt = sqrt(delta.toDouble())
            nextGreater((time.toDouble() - dsqrt) / 2.0)..nextLess((time.toDouble() + dsqrt) / 2.0)
        } else
            ULongRange.EMPTY

        waysCount = range.run { endInclusive - start + 1UL }
    }

    constructor(pair: Pair<ULong, ULong>): this(time=pair.first, distance=pair.second)
}

fun main() {
    // Part I
    val races = File("src/main/resources/day_6_input.txt").readLines()
        .map { it.split(Regex("""\s+""")).drop(1).map(String::toULong) }
        .let { (a, b) -> a zip b}.map(::Race)
    races.map(Race::waysCount).reduce { acc, i -> acc * i }.also(::println)

    // Part II
    val (time, distance) = File("src/main/resources/day_6_input.txt").readLines()
        .map { it.split(Regex("""\s+""")).drop(1).joinToString("").let(String::toULong) }
    println(Race(time, distance).waysCount)
}