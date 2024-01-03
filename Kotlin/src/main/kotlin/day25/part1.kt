package day25

import day25.model.Edge
import day25.model.Vertex
import pop
import java.io.File

internal fun minimumCutPhase(graph: Collection<Vertex>): Triple<Vertex, Vertex, Int> {
    val remaining: MutableSet<Vertex> = HashSet(graph)
    val connected = LinkedHashMap<Vertex, Int>(remaining.size).apply { set(remaining.pop(), 0) }
    while (remaining.isNotEmpty()) {
        val weighted = remaining.associateWith { v -> v.adjacentEdges.filter { it.first in connected }.sumOf { it.second.weight } }
        val (maxVertex, cutWeight) = weighted.maxBy(Map.Entry<*, Int>::value)
        remaining.remove(maxVertex)
        connected[maxVertex] = cutWeight
    }

    val (t, cutWeight) = connected.pollLastEntry()
    val s = connected.pollLastEntry().key
    return Triple(s, t, cutWeight)
}

fun main() {
    val vertices = mutableMapOf<String, Vertex>()
//    val edges = mutableSetOf<Edge>()

    fun getOrPutVertex(id: String) = vertices.getOrPut(id) { Vertex(listOf(id)) }
    fun getOrPutVertex(vararg ids: String) = ids.let(Array<out String>::toList).let { vertices.getOrPut(it.cs()) { Vertex(it) } }

    fun mergeVertices(a: Vertex, b: Vertex) {
        val union = a.adjacentEdges union b.adjacentEdges
        val adjacentBoth =  union.filter { it.first in a.adjacent intersect b.adjacent }.toSet()
        val adjacentOnlyA = a.adjacentEdges subtract adjacentBoth
        val adjacentOnlyB = b.adjacentEdges subtract adjacentBoth
        println(adjacentBoth)
    }

    File("src/main/resources/test.txt").useLines { file -> file.forEach { line ->
        val (from, toList) = line.split(":")
        val fromVertex = getOrPutVertex(from.trim())
//        edges.addAll(toList.split(" ").filterNot { it.isEmpty() }.map(::getOrPutVertex).map { toVertex ->
//            fromVertex.getOrAddEdge(toVertex).second.also { toVertex.getOrPutEdge(fromVertex, it) } })
        toList.split(" ").filterNot { it.isEmpty() }.map(::getOrPutVertex).forEach { toVertex ->
            fromVertex.getOrAddEdge(toVertex).second.also { toVertex.getOrPutEdge(fromVertex, it) } }
    } }
    while(1 < vertices.size) {
        val (cutS, cutT, cutWeight) = minimumCutPhase(vertices.values)
        mergeVertices(cutS, cutT)
    }
}
