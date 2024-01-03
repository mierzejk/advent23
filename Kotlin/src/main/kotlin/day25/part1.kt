package day25

import java.io.File

internal fun Iterable<String>.cs() = this.sorted().joinToString(",")

internal data class Vertex(val id: String): Comparable<Vertex> {
    private val edgesField = mutableMapOf<String, Edge>()
    lateinit var ids: Set<String>

    init {
        if (!::ids.isInitialized)
            ids = id.split(',').toSet()
    }

    val edges get() = edgesField.values.toSet()

    constructor(ids: Iterable<String>): this(ids.cs()) { this.ids = when (ids) {
        is Set -> ids
        else -> ids.toSet()
    } }

    override fun compareTo(other: Vertex) = this.id.compareTo(other.id)

    fun getOrAddEdge(to: Vertex) = edgesField.getOrPut(to.id) { Edge(this, to) }
    fun getOrPutEdge(to: Vertex, edge: Edge) = edge.also { this in it.vertices }.let {
        edgesField.getOrPut(to.id) { it } }
    fun getOrPutEdge(edge: Edge) = edge.vertices.also { assert(this in it) }.single { this != it }.let {
        edgesField.getOrPut(it.id) { edge } }
}

internal data class Edge(val vertices: Set<Vertex>, val weight: Int = 1) {
    val id = vertices.sorted().joinToString("-", transform = Vertex::id)

    constructor(left: Vertex, right: Vertex, weight: Int = 1): this(setOf(left, right), weight)

    init {
        assert (2 == vertices.size)
    }

    override fun toString() = "$id: $weight"

    override fun equals(other: Any?) = if (other is Edge) id == other.id else false

    override fun hashCode() = id.hashCode()
}

fun main() {
    val vertices = mutableMapOf<String, Vertex>()
    val edges = mutableSetOf<Edge>()

    fun getOrPutVertex(id: String) = vertices.getOrPut(id) { Vertex(listOf(id)) }
    fun getOrPutVertex(ids: List<String>) = vertices.getOrPut(ids.cs()) { Vertex(ids) }

    File("src/main/resources/day_25_input.txt").useLines { file -> file.forEach { line ->
        val (from, toList) = line.split(":")
        val fromVertex = getOrPutVertex(from.trim())
        edges.addAll(toList.split(" ").filterNot { it.isEmpty() }.map(::getOrPutVertex). map { toVertex ->
            fromVertex.getOrAddEdge(toVertex).also { toVertex.getOrPutEdge(fromVertex, it) } })
    } }
}

