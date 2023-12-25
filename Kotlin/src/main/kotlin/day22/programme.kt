package day22

import DefaultMap
import java.io.File
import kotlin.math.max
import kotlin.math.min

internal data class Point(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun invoke(string: String): Point {
            val (x, y, z) = string.split(',').map(String::toInt)
            return Point(x, y, z)
        }
    }
}

@Suppress("MemberVisibilityCanBePrivate")
internal data class Block(private val corner1: Point, private val corner2: Point) {
    val x1 = min(corner1.x, corner2.x)
    val x2 = max(corner1.x, corner2.x)
    val y1 = min(corner1.y, corner2.y)
    val y2 = max(corner1.y, corner2.y)
    val z1 = min(corner1.z, corner2.z)
    val z2 = max(corner1.z, corner2.z)
    val height = 1 + z2 - z1

    companion object {
        fun invoke(string: String): Block {
            val (corner1, corner2) = string.split('~').map(Point::invoke)
            return Block(corner1, corner2)
        }
    }
}

internal class Plane(private val ground: Block) {
    private val stride = ground.x2 + 1
    private val length = stride * (ground.y2 + 1)
    private val levels: MutableList<MutableList<Block?>> = mutableListOf(List(length) { ground }.toMutableList())
    private val safeBlocks = mutableSetOf(ground)
    val supports = DefaultMap<Block, MutableSet<Block>> { mutableSetOf() }
    val supported = mutableMapOf<Block, Set<Block>>()

    val safe: Set<Block>
        get() = safeBlocks

    private fun Block.toCoordinates() = (y1..y2).flatMap { y -> (x1..x2).map { x -> y * stride + x } }

    fun addBlock(block: Block) {
        val coordinates = block.toCoordinates()
        val topLevel = levels.lastIndex - levels.reversed().indexOfFirst { level -> coordinates.any { null != level[it] } }

        val supporting = levels[topLevel].filterIndexed { i, _ -> i in coordinates }.filterNotNull().toSet()
        supporting.singleOrNull()?.let(safeBlocks::remove)
        supporting.forEach { supports[it].add(block) }
        supported[block] = supporting
        safeBlocks.add(block)

        for (l in topLevel+1..topLevel+block.height) {
            val level = when {
                levels.lastIndex < l -> List<Block?>(length) { null }.toMutableList().also(levels::add)
                else -> levels[l]
            }
            coordinates.forEach { assert(null == level[it]); level[it] = block }
        }
    }
}

fun main() {
    val input = File("src/main/resources/day_22_input.txt").useLines { file ->
        file.map(Block::invoke).toList().sortedBy(Block::z1)
    }
    val (minX, minY) = listOf(input.minOf(Block::x1), input.minOf(Block::y1))
    val (maxX, maxY) = listOf(input.maxOf(Block::x2), input.maxOf(Block::y2))
    assert(0 == minX && 0 == minY) // Input data assertion.
    val ground = Block(Point(minX, minY, 0), Point(maxX, maxY, 0))
    val plane = Plane(ground)
    input.forEach(plane::addBlock)
    println("Part I: ${plane.safe.size}")

    fun getFalling(block: Block): Set<Block> {
        val fallen = mutableSetOf<Block>()
        var pending = setOf(block)
        while (pending.isNotEmpty()) {
            fallen.addAll(pending)
            pending = pending.flatMap{ plane.supports[it] }.filter { plane.supported[it]!!.all(fallen::contains) }.toSet()
        }

        fallen.remove(block)
        return fallen
    }

    val unsafe = input subtract plane.safe
    val fallenCount = unsafe.sumOf { getFalling(it).size }
    println("Part II: $fallenCount")
}

