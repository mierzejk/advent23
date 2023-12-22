package day19

import java.io.File
import java.util.LinkedList
import java.util.Queue

const val MAX_VALUE = 4000
val workflowRe = Regex("""(?<name>\w+)\{(?<rules>.*)}""")
val ruleRe = Regex("""(?<attr>\w+)(?<sign>[<>])(?<value>\d+):(?<dest>\w+)""")

@Suppress("SpellCheckingInspection")
internal class Node(val name: String, val rules: List<String>, val depth: Int = 0) {
    lateinit var outcoming: Arc
}

internal class Arc(val attr: String, val division: Int, val left: Node, val right: Node)

internal class Agent(private val properties: Map<String, Set<Int>>, val node: Node, segment: String) {
    private val path = "$segment→${node.name}"
    private val isDepleted get() = properties.values.any(Set<Int>::isEmpty)

    fun step(): List<Agent> {
        val arc = node.outcoming
        val left = Agent(buildMap {
            properties.forEach { (key, values) ->
                if (arc.attr == key)
                    this[key] = values intersect (1..<arc.division).toSet()
                else
                    this[key] = values.toMutableSet()
            }
        }, arc.left, path)
        val right = Agent(buildMap {
            properties.forEach { (key, values) ->
                if (arc.attr == key)
                    this[key] = values intersect (arc.division..MAX_VALUE).toSet()
                else
                    this[key] = values.toMutableSet()
            }
        }, arc.right, path)
        return listOf(left, right).filterNot(Agent::isDepleted)
    }
}

fun main() {
    val topNodes = File("src/main/resources/test.txt").readLines().takeWhile { it.isNotBlank() }
        .mapNotNull(workflowRe::matchEntire).map { Node(it.groupValues[1], it.groupValues[2].split(',')) }.associateBy(Node::name).toMutableMap()
    val nodeA = Node("A", emptyList()).apply { topNodes[name] = this }
    val nodeR = Node("R", emptyList()).apply { topNodes[name] = this }

    fun Node.processRules() {
        if (this === nodeA || this === nodeR)
            return

        assert (1 < rules.size)
        val (attrName, sign, value, dest) = ruleRe.matchEntire(rules[0])!!.destructured
        val nextDepth = depth + 1
        var division = value.toInt()
        val subNode = when(rules.size) {
            2 -> topNodes[rules[1]]!!
            else -> Node("${name}_$nextDepth", rules.drop(1), depth = nextDepth)
        }
        val children = arrayOf(topNodes[dest]!!, subNode)
        if (">" == sign) {
            children.reverse()
            division += 1
        }

        outcoming = Arc(attrName, division, children[0], children[1])
        if (2 < rules.size)
            subNode.processRules()
    }

    // Build arcs
    topNodes.values.forEach(Node::processRules)

    // Depth-first search
    val queue: Queue<Agent> = LinkedList()
    val accepted = mutableListOf<Agent>()
    queue.add(Agent(mapOf(
        "x" to (1..MAX_VALUE).toSet(),
        "m" to (1..MAX_VALUE).toSet(),
        "a" to (1..MAX_VALUE).toSet(),
        "s" to (1..MAX_VALUE).toSet()
    ), topNodes["in"]!!, ""))
    while (queue.isNotEmpty()) {
        val agent = queue.poll()
        agent.step().forEach {
            if (nodeA === it.node)
                accepted.add(it)
            else if (nodeR !== it.node)
                queue.add(it)
        }
        println("${agent.node.name}: ${queue.size}")
    }

    println(accepted.size)
}
