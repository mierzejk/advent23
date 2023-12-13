package day13

import manacher
import java.io.File

fun<T> getReflection(input: List<T>, bogus: T) = manacher(input, bogus).run {
        mapIndexed { index, i -> index to i }.
        filter { (index, i) -> 1 == index % 2 && 2 * i in listOf(index + 1, size - index) }.
        maxByOrNull(Pair<Int, Int>::second)?.first ?: -1}.let { (it + 1) / 2 }

internal class Pattern<T>(chars: List<Char>, private val bogus: T, stride: Int, private val convert: (List<Char>) -> T) {
    private val rowChars = (chars.indices step stride).map { (it..<it + stride).map { i -> chars[i] }.toMutableList() }
    private val colChars = (0..<stride).map { (it..chars.lastIndex step stride).map { i -> chars[i] }.toMutableList() }
    private val rows = rowChars.map(convert).toMutableList()
    private val cols = colChars.map(convert).toMutableList()
    val horizontalReflection = getReflection(rows, bogus)
    val verticalReflection = getReflection(cols, bogus)

    private companion object {
        fun Char.flipPoint() = when(this) { '#' -> '.' else -> '#' }
    }

    private inner class Point(val rowIdx: Int, val colIdx: Int) {
        init {
            assert(rowChars[rowIdx][colIdx] == colChars[colIdx][rowIdx])
        }

        fun flip() {
            rowChars[rowIdx][colIdx] = rowChars[rowIdx][colIdx].flipPoint()
            colChars[colIdx][rowIdx] = colChars[colIdx][rowIdx].flipPoint()
            rows[rowIdx] = convert(rowChars[rowIdx])
            cols[colIdx] = convert(colChars[colIdx])
        }
    }

    init {
        assert(listOf(horizontalReflection, verticalReflection).let {
            0 in it && null != it.singleOrNull { r -> 0 < r } })
    }

    fun repairSmudge(horizontalWeight: ULong = 100UL, verticalWeight: ULong = 1UL): ULong {
        val points = rowChars.indices.flatMap { r -> colChars.indices.map { c -> Point(r, c) } }

        for (point in points) {
            point.flip()
            val horizontalRef = getReflection(rows, bogus)
            if (0 != horizontalRef && horizontalReflection != horizontalRef)
                return horizontalWeight * horizontalRef.toULong()

            val verticalRef = getReflection(cols, bogus)
            if (0 != verticalRef && verticalReflection != verticalRef)
                return verticalWeight * verticalRef.toULong()

            point.flip()
        }
        throw IllegalArgumentException()
    }

    override fun toString() = rowChars.joinToString("\n") { it.joinToString("")
    }
}

fun Char.toBit() = when(this) { '#' -> '1' else -> '0' }

fun toBinaryInt(input: List<Char>) = input.map(Char::toBit).joinToString("").toInt(2)

fun main() {
    val sb = StringBuilder()
    var stride = 0
    val patterns = buildList {
        File("src/main/resources/test.txt").useLines { file ->
            for (line in file) {
                if (0 == stride)
                    stride = line.length
                if (line.isEmpty()) {
                    add(Pattern(sb.toList(), -1, stride, ::toBinaryInt))
                    stride = 0
                    sb.clear()
                } else
                    sb.append(line)
            }
            add(Pattern(sb.toList(), -1, stride, ::toBinaryInt))
        }
    }
    // Part I
    patterns.sumOf { 100UL * it.horizontalReflection.toULong() + it.verticalReflection.toULong() }.also(::println)

    // Part II
    patterns.sumOf(Pattern<Int>::repairSmudge).also(::println)
//    Part one answer: 709
//    Part two answer: 1400
}