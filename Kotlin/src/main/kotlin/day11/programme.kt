package day11

import java.io.File
import kotlin.math.abs

internal data class Galaxy(var y: Long, var x: Long) {
    constructor(y: Int, x: Int): this(y.toLong(), x.toLong())
}
private const val expansion = 999999L // Part II; Part I: 1L

fun main() {
    val space = ArrayList<Galaxy>()
    val emptyRows = ArrayList<Long>()
    File("src/main/resources/day_11_input.txt").useLines { file -> file.flatMapIndexedTo(space) {
        row, line -> line.withIndex().filter { (_, char) -> '#' == char }.map {
            (col, _) -> Galaxy(row, col) }.also {if (it.isEmpty()) emptyRows.addLast(row.toLong()) } } }

    val emptyCols = 0L..space.maxOf(Galaxy::x) subtract space.map(Galaxy::x).toSet()
    emptyRows.reversed().forEach { i -> space.filter { it.y > i }.forEach { it.y += expansion } }
    emptyCols.reversed().forEach { i -> space.filter { it.x > i }.forEach { it.x += expansion } }

//    val result = space.combinations().pivot({ x }, { y } ).flatMap { it.map { (a, b) -> a - b } }.map(::abs).sum()
    val result = space.combinations().sumOf { (a, b) -> abs(a.x - b.x) + abs(a.y - b.y) }
    println(result)

}

fun<T> Collection<T>.combinations() =
    take(size-1).mapIndexed { i, item -> drop(i+1).map { Pair(item, it) } }.flatten()

fun<T, R> Collection<Pair<T, T>>.pivot(vararg block: T.() -> R) =
    this.map { (a, b) -> block.map { Pair(it(a), it(b)) } }