package day08

import java.io.File

internal enum class Direction {
    L,
    R;

    companion object {
        operator fun invoke(char: Char) = Direction.valueOf(char.uppercase())
    }
}

internal data class Node(val id: String, val left: String, val right: String) {
    fun next(direction: Direction) = when (direction) {
        Direction.L -> left
        Direction.R -> right
    }

    companion object {
        private val reNode = Regex("""(?<id>\w+)\W+(?<left>\w+)\W+(?<right>\w+)""")

        private fun toNode(match: MatchResult) = match.groupValues.drop(1).let {
            (id, left, right) -> Node(id, left, right)
        }

        operator fun invoke(line: String) = reNode.find(line)?.let(::toNode)
    }
}

fun main() {
    var route: Collection<Direction> = emptyList()
    val nodes = HashMap<String, Node>()
    File("src/main/resources/day_8_input.txt").useLines { file -> file.iterator().run {
        route = next().map { Direction(it) }
        this.asSequence().map { Node(it) }.filterNotNull().associateByTo(nodes, Node::id)
    } }

    fun getDirections() = sequence {
        while(true)
            yieldAll(route)
    }

    var node = nodes["AAA"]!!
    val finish = nodes["ZZZ"]!!
    for ((i, direction) in getDirections().withIndex()) {
        if (node === finish) {
            println(i)
            break
        }

        node = node.next(direction).let(nodes::get)!!
    }
}