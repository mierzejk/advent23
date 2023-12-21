package day19

import java.io.File

const val MAX_VALUE = 4000
val workflowRe = Regex("""(?<name>\w+)\{(?<rules>.*)}""")
val ruleRe = Regex("""(?<attr>\w+)(?<sign>[<>])(?<value>\d+):(?<dest>\w+)""")

@Suppress("SpellCheckingInspection")
internal class Node(val name: String, val rules: List<String>, val depth: Int = 0) {
    lateinit var outcoming: List<Arc>
}

internal class Arc(val attr: String? = null, val acceptable: Set<Int>? = null, val to: Node)

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
        val subNode = when(rules.size) {
            2 -> topNodes[rules[1]]!!
            else -> Node("${name}_$nextDepth", rules.drop(1), depth = nextDepth)
        }
        outcoming = listOf(
            Arc(attrName, when(sign[0]) {
                '<' -> 1..<value.toInt()
                '>' -> 1+value.toInt()..MAX_VALUE
                else -> throw IllegalArgumentException(sign)
            }.toSet(), topNodes[dest]!!),
            Arc(attrName, when(sign[0]) {
                '>' -> 1..<value.toInt()
                '<' -> 1+value.toInt()..MAX_VALUE
                else -> throw IllegalArgumentException(sign)
            }.toSet(), subNode))
        if (2 < rules.size)
            subNode.processRules()
    }

    // Build arcs
    topNodes.values.forEach(Node::processRules)
    println(topNodes)
}
