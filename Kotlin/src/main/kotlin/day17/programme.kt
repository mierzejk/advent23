package day17

import java.io.File
import java.util.PriorityQueue

fun main() {
    val input = File("src/main/resources/day_17_input.txt").readLines()
    val stride = input[0].length
    val lastRow = (input.size - 1) * stride
    val direction = object { val Up = -stride; val Right = 1; val Down = stride; val Left = -1 }

    class Node(val pos: Int, val cost: Int) {
        private val directions = buildList {
            if (stride <= pos) add(direction.Up)
            if (0 != (pos + 1) % stride) add(direction.Right)
            if (pos < lastRow) add(direction.Down)
            if (0 != pos % stride) add(direction.Left)
        }

        inner class Segment(val dir: Int,
                            val length: Int = 1,
                            val distance: Int = this@Node.cost,
                            private val map: List<Node>) {
            val pos: Int by this@Node::pos
            val nextMoves: List<Segment> by lazy {
                directions.filter { dir != -1 * it && (dir != it || length < 3) }
                    .map { map[it + pos].let { n ->
                        n.Segment(it, if (it == dir) length + 1 else 1, distance + n.cost, map) } }
            }
        }
    }

    val map = input.flatMap { line -> line.map { it.code - 0x30 } }.mapIndexed { i, cost -> Node(i, cost) }
    val start = listOf(
        map[1].Segment(direction.Right, 1, map=map),
        map[stride].Segment(direction.Down, 1, map=map)
    )
    val heap = PriorityQueue<Node.Segment> { a, b -> a.distance - b.distance }.apply { start.forEach(::add) }
    val visited = List(map.size) { HashMap<Int, Node.Segment>(5, 1f) }
    fun getVisited(segment: Node.Segment) = visited[segment.pos][segment.dir]
    fun setVisited(segment: Node.Segment) { visited[segment.pos][segment.dir] = segment }
    start.forEach(::setVisited)

    @Suppress("DuplicatedCode")
    while (map.lastIndex != heap.peek().pos) {
        val segment = heap.poll()
        segment.nextMoves.filter { s -> getVisited(s)?.let { s.length < it.length } ?: true }.forEach {
            setVisited(it)
            heap.add(it)
        }
    }
    println(heap.peek().distance)
}