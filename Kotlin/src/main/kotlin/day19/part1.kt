package day19

import java.io.File

internal open class Transition(open val workflow: String) {
    open fun eval (part: Map<String, Int>): String? = workflow
}

internal class Rule(val attribute: String, val greater: Int, val value: Int, workflow: String): Transition(workflow) {
    constructor(rule: String) : this(
        rule.take(rule.indexOfAny(charArrayOf('<', '>'))),
        when (rule[rule.indexOfAny(charArrayOf('<', '>'))]) {
            '<' -> -1
            '>' -> 1
            else -> throw IllegalArgumentException(rule)
        },
        rule.slice(rule.indexOfAny(charArrayOf('<', '>')) + 1..<rule.indexOf(':')).toInt(),
        rule.drop(rule.indexOf(':') + 1)
    )

    override fun eval(part: Map<String, Int>) = when {
        greater * part[attribute]!!.compareTo(value) > 0 -> workflow
        else -> null
    }
}

fun main() {
    val workflows = mutableMapOf<String, List<Transition>>()

    fun Map<String, Int>.process(): Boolean {
        var status = "in"
        while (status !in listOf("A", "R")) {
            status = workflows[status]!!.firstNotNullOf { it.eval(this) }
        }
        return when(status) {
            "A" -> true
            "R" -> false
            else -> throw IllegalArgumentException(status)
        }
    }

    File("src/main/resources/day_19_input.txt").useLines { file ->
        val iterator = file.iterator()
        var line: String
        while (iterator.next().also { line=it }.isNotBlank()) {
            val name = line.takeWhile { it != '{' }
            workflows[name] = line.drop(name.length).let { it.slice(1..<it.lastIndex) }.split(',')
                .map { when {
                    it.contains(':') -> ::Rule
                    else -> ::Transition
                }.invoke(it) }
        }

        val parts = buildList {
            iterator.forEachRemaining { str ->
                val part = str.slice(1..<str.lastIndex).split(',').associate {
                    val (attr, `val`) = it.split('=')
                    attr to `val`.toInt()
                }
                add(part)
            }
        }

        val result = parts.filter(Map<String, Int>::process).flatMap { it.values.map(Int::toLong) }.sum()
        println(result)
    }
}
