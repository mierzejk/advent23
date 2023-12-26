package day23

import java.io.File

internal data class Node(val index: Int) {
    val outcoming = mutableSetOf<Arc>()

    fun addOutcoming(to: Node, length: Int) = outcoming.add(Arc(to, length))

    inner class Arc(val to: Node, val length: Int)
}

fun main() {
    val maze = File("src/main/resources/test.txt").useLines { file ->
        val iterator = file.iterator()
        val sb = StringBuilder(iterator.next())
        var height = 1
        val stride = sb.length
        iterator.forEachRemaining { sb.append(it); height++ }
        Maze(sb, stride, height)
    }
    val root = Node(1)
    val sink = Node(maze.size - 2)
    val nodes = mutableMapOf(root.index to root, sink.index to sink)

    class Path(val from: Node, private var next: Int?): Iterable<Int>, Iterator<Int> {
        var length = 0
        var current = from.index

        override fun iterator() = this

        override fun hasNext() = null != next

        override fun next(): Int {
            val preceding = current
            current = next ?: throw NoSuchElementException()
            length++
            next = maze.getAdjacent(current).filterNot { it == preceding }.let { when (it.size) {
                    1 -> it[0]
                    else -> null
                } }
            return current
        }
    }

    fun addNode(index: Int): List<Path> {
        if (index in nodes)
            return emptyList()

        val adjacent = maze.getAdjacent(index)
        if (adjacent.size < 2)
            return emptyList()

        val node = Node(index).also { nodes[index] = it }
        return adjacent.map { Path(node, it) }
    }

    // Breadth-First Search
    val queue = ArrayDeque<Path>().apply { add(Path(root, maze.indexDown(root.index))) }
    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()
        val end = path.last()
        val outcoming = addNode(end)
        nodes[end]?.let { path.from.addOutcoming(it, path.length) }
        if (outcoming.isNotEmpty())
            queue.addAll(outcoming)
    }

    // assert(maze is DAG)
    // Topological sort - Depth-First Search
    val stack = ArrayDeque<Node>(nodes.size)
    val distance = HashMap<Int, Int>(nodes.size)
    fun visit(node: Node) {
        if (node.index in distance)
            return

        distance[node.index] = 0
        node.outcoming.map { it.to }.forEach(::visit)
        stack.addFirst(node)
    }
    visit(root)

    // Longest path
    while(stack.isNotEmpty()) {
        val node = stack.removeFirst()
        val dist = distance[node.index]!!
        val outcoming = node.outcoming.groupBy { it.to.index }.map {
            (index, arcs) -> index to arcs.maxOf { dist + it.length } }
        for ((index, dst) in outcoming) {
            if (distance[index]!! < dst)
                distance[index] = dst
        }
    }

    // Part I
    print(distance[sink.index])
}

