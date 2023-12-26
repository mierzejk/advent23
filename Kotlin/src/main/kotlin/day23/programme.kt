package day23

import java.io.File

internal data class Node(val index: Int) {
    val outcoming = mutableSetOf<Arc>()

    inner class Arc(val to: Node, val length: Int)
}

fun main() {
    val nodes = mutableMapOf<Int, Node>()
    fun addNode(index: Int) = Node(index).also { nodes[index] = it }

    val maze = File("src/main/resources/test.txt").useLines { file ->
        val iterator = file.iterator()
        val sb = StringBuilder(iterator.next())
        var height = 1
        val stride = sb.length
        iterator.forEachRemaining { sb.append(it); height++ }
        Maze(sb, stride, height)
    }

    class Path(val from: Node, var current: Int) {
        var length = 1
        var last = from.index

        fun next() = maze.getAdjacent(current).filterNot { it == last }.let { when (it.size) {
            1 -> run { length++; last = current; current = it[0]; true }
            else -> false
        } }
    }

    val root = addNode(1)
    val queue = ArrayDeque<Path>()
    val path = Path(root, maze.indexDown(root.index)!!)
    while (path.next()) { Unit }
    println("${path.current / maze.stride}, ${path.current % maze.stride}")
    println(maze[path.current])

}

