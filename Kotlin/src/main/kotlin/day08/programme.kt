package day08

import java.io.File
import LCM

internal enum class Direction {
    L, R;

    companion object {
        operator fun invoke(char: Char) = Direction.valueOf(char.uppercase())
    }
}

internal data class Node(val id: String, private val left: String, private val right: String) {
    val isStarting = 'A' == id.last()
    val isFinish = 'Z' == id.last()

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
    val nodes = HashMap<String, Node>()
    val route = File("src/main/resources/day_8_input.txt").useLines { file -> file.iterator().run {
        val route = next().map { Direction(it) }
        this.asSequence().map { Node(it) }.filterNotNull().associateByTo(nodes, Node::id)
        route
    } }

    fun getDirections(start: Node) = with(object { var node = start }) {
        sequence { while (true)
            yieldAll(route.asSequence().map { node.apply { node = next(it).let(nodes::get)!! } })
        }
    }

    fun getSteps(start: Node) = getDirections(start).indexOfFirst(Node::isFinish)

    // Part I
    println(getSteps(nodes["AAA"]!!))

    // Part II
    nodes.values.filter(Node::isStarting).map(::getSteps).map(Int::toULong).let(::LCM).also(::println)
}