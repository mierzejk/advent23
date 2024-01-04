package day25

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

    fun getOrPutVertex(id: String) = vertices.getOrPut(id) { Vertex(listOf(id)) }

    fun mergeVertices(a: Vertex, b: Vertex) {
        vertices.remove(a.id)!!.removeEdge(b.id)
        vertices.remove(b.id)!!.removeEdge(a.id)
        val union = a.adjacentEdges + b.adjacentEdges
        val adjacentBoth =  union.filter { it.first in a.adjacent intersect b.adjacent }.toSet()
        val adjacentOnlyA = (a.adjacentEdges - adjacentBoth).associate { it.first to it.second.weight }
        val adjacentOnlyB = (b.adjacentEdges - adjacentBoth).associate { it.first to it.second.weight }
        val grouped= adjacentBoth.groupingBy(Pair<Vertex, *>::first).fold(0) { weight, (_, edge) -> weight + edge.weight }
        union.forEach { (vertex, edge) -> vertex.removeEdge(edge) }
        val merged = Vertex(buildSet { addAll(a.ids); addAll(b.ids) })
        sequenceOf(adjacentOnlyA, adjacentOnlyB, grouped).flatMap { it.asSequence() }.forEach { (vertex, weight) ->
            merged.getOrAddEdge(vertex, weight=weight).second.also { vertex.getOrPutEdge(merged, it) }
        }
        vertices[merged.id] = merged

        println(vertices)
    }

    File("src/main/resources/test.txt").useLines { file -> file.forEach { line ->
        val (from, toList) = line.split(":")
        val fromVertex = getOrPutVertex(from.trim())
        toList.split(" ").filterNot { it.isEmpty() }.map(::getOrPutVertex).forEach { toVertex ->
            fromVertex.getOrAddEdge(toVertex, weight=1).second.also { toVertex.getOrPutEdge(fromVertex, it) } }
    } }
    while(1 < vertices.size) {
        val (cutS, cutT, cutWeight) = minimumCutPhase(vertices.values)
        mergeVertices(cutS, cutT)
    }
    println(vertices)
}
