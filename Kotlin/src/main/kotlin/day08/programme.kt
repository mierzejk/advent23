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

@Suppress("FunctionName")
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

    fun getSteps(start: Node): Int {
        var node = start
        for ((i, direction) in getDirections().withIndex()) {
            if (node.isFinish)
                return i

            node = node.next(direction).let(nodes::get)!!
        }
        throw IllegalArgumentException()
    }

    // Part I
    println(getSteps(nodes["AAA"]!!))

    // Part II
    tailrec fun GCF(a: ULong, b: ULong): ULong = if (0UL == b) a else GCF(b, a%b)
    fun LCM(a: ULong, b: ULong) = a * (b / GCF(a, b))
    fun Collection<ULong>.LCM(): ULong {
        if (isEmpty())
            throw IllegalArgumentException()

        val iter = this.iterator()
        var result = iter.next()
        iter.forEachRemaining { result = LCM(result, it) }
        return result
    }

    val steps = nodes.values.filter(Node::isStarting).map(::getSteps).map(Int::toULong)
    println(steps.LCM())
}