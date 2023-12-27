package day23

import java.io.File

internal data class Node(val index: Int) {
    val outcoming = mutableSetOf<Arc>()

    fun addOutcoming(to: Node, length: Int) = Arc(to, length).also(outcoming::add)

    inner class Arc(val to: Node, val length: Int)
}

fun main() {
    var maze = File("src/main/resources/day_23_input.txt").useLines { file ->
        val iterator = file.iterator()
        val sb = StringBuilder(iterator.next())
        var height = 1
        val stride = sb.length
        iterator.forEachRemaining { sb.append(it); height++ }
        Maze(sb, stride, height)
    }
    var root = Node(1)
    val sink = Node(maze.size - 2)
    val nodes = mutableMapOf(root.index to root, sink.index to sink)
    val arcs = ArrayList<Node.Arc>()

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

    fun bfs(starting: Path) {
        val queue = ArrayDeque<Path>().apply { add(starting) }
        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val end = path.last()
            val outcoming = addNode(end)
            nodes[end]?.let { path.from.addOutcoming(it, path.length).also(arcs::add) }
            if (outcoming.isNotEmpty())
                queue.addAll(outcoming)
        }
    }

    // Breadth-First Search to create the graph.
    bfs(Path(root, maze.indexDown(root.index)))

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
    println(distance[sink.index])

    // Part II
    class Route(val head: Node, val tail: Set<Int>, val length: Int) {
        // Assertion - there is no loops.
        fun expand(): List<Route> {
            return head.outcoming.filterNot { it.to.index in tail }.map {
                Route(it.to, tail + head.index, it.length + length) }
        }
    }

    nodes.clear()
    arcs.clear()
    maze = Maze(maze.array.map { when(it) {
        '#' -> '#'
        else -> '.'
    } }, maze.stride, maze.height)
    root = Node(1)
    nodes[root.index] = root
    nodes[sink.index] = sink

    bfs(Path(root, maze.indexDown(root.index)))
    val completeRoutes = mutableListOf<Route>()
    val routeQueue = ArrayDeque<Route>()
    routeQueue.add(Route(root, emptySet(), 0))
    while(routeQueue.isNotEmpty()) {
        val routes = routeQueue.removeFirst().expand()
        val (finished, pending) = routes.partition { it.head === sink }
        completeRoutes.addAll(finished)
        routeQueue.addAll(pending)
    }

    print(completeRoutes.maxOf(Route::length))
}

