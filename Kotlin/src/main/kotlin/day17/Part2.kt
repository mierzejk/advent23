package day17

import java.io.File
import java.util.PriorityQueue

const val MinLength = 4
const val MaxLength = 10
val LengthRange = 1..MaxLength

fun main() {
    val input = File("src/main/resources/day_17_input.txt").readLines()
    val stride = input[0].length
    val maxPos = input.size * stride - 1
    val direction = object { val Up = -stride; val Right = 1; val Down = stride; val Left = -1 }

    class Node(val pos: Int, val cost: Int) {
        inner class Segment(val dir: Int,
                            val distance: Int,
                            private val map: List<Node>) {
            val pos: Int by this@Node::pos
            private fun getNext(positions: Iterable<Int>, way: Int) = sequence {
                assert(way != dir && way != -1 * dir)
                var value = distance
                positions.withIndex().forEach { (i, p) ->
                    value += map[p].cost
                    val len = i + 1
                    if (MinLength <= len)
                        yield(map[p].Segment(way, value, map))
                }
            }

            val directions get() = when (dir) {
                direction.Up, direction.Down -> (
                    getNext(LengthRange.map { pos - it }.takeWhile { pos - (pos % stride) <= it }, direction.Left) +
                    getNext(LengthRange.map { pos + it }.takeWhile { it < pos - (pos % stride) + stride }, direction.Right)).toList()
                else -> (
                    getNext(LengthRange.map { pos - it * stride }.takeWhile { 0 <= it }, direction.Up) +
                    getNext(LengthRange.map { pos + it * stride }.takeWhile { it <= maxPos }, direction.Down)).toList()
            }
        }
    }

    val map = input.flatMap { line -> line.map { it.code - 0x30 } }.mapIndexed { i, cost -> Node(i, cost) }
    val heap = PriorityQueue<Node.Segment> { a, b -> a.distance - b.distance }
    val visited = List(map.size) { HashMap<Int, Node.Segment>(5, 1f) }

    var verticalDistance = 0
    var horizontalDistance = 0
    (0..<MaxLength).forEach {
        verticalDistance += map[it * stride].cost
        horizontalDistance += map[it].cost
        if (it + 1 >= MinLength) {
            val verticaStart = map[it * stride].Segment(direction.Down, verticalDistance, map)
            heap.add(verticaStart)
            visited[it * stride][direction.Down] = verticaStart
            val horizontalStart = map[it].Segment(direction.Right, horizontalDistance, map)
            heap.add(horizontalStart)
            visited[it][direction.Right] = horizontalStart
        }
    }

    fun Node.Segment.getVisited() = visited[pos][dir]
    fun Node.Segment.setVisited() { visited[pos][dir] = this }

    @Suppress("DuplicatedCode")
    while (map.lastIndex != heap.peek().pos) {
        val segment = heap.poll()
        segment.directions.filter { s -> s.getVisited()?.let { s.distance < it.distance } ?: true }.forEach {
            it.setVisited()
            heap.add(it)
        }
    }

    println(heap.peek().distance - map[0].cost)
}