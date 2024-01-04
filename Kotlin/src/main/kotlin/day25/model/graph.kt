package day25.model

import day25.cs

internal data class Vertex(val id: String): Comparable<Vertex> {
    private val edgesField = mutableMapOf<String, Pair<Vertex, Edge>>()
    lateinit var ids: Set<String>

    init {
        if (!::ids.isInitialized)
            ids = id.split(',').toSet()
    }

    val adjacent get() = edgesField.values.map(Pair<Vertex, *>::first).toSet()
    val adjacentEdges get() = edgesField.values.toSet()

    constructor(ids: Iterable<String>): this(ids.cs()) { this.ids = when (ids) {
        is Set -> ids
        else -> ids.toSet()
    } }

    override fun compareTo(other: Vertex) = this.id.compareTo(other.id)

    fun getOrAddEdge(to: Vertex, weight: Int) = edgesField.getOrPut(to.id) { Pair(to, Edge(this, to, weight)) }
    fun getOrPutEdge(to: Vertex, edge: Edge) = edge.also { assert (this in it.vertices) }.let {
        edgesField.getOrPut(to.id) { Pair(to, it) } }
    fun removeEdge(id: String) = edgesField.remove(id)
    fun removeEdge(edge: Edge) = edgesField.remove(edge.vertices.map(Vertex::id).single { it != id })!!

}

internal data class Edge(val vertices: Set<Vertex>, val weight: Int) {
    val id = vertices.sorted().joinToString("-", transform = Vertex::id)

    constructor(left: Vertex, right: Vertex, weight: Int): this(setOf(left, right), weight)

    init {
        assert (2 == vertices.size)
    }

    override fun toString() = "[${id.replace("-", "]-[")}]: $weight"

    override fun equals(other: Any?) = if (other is Edge) id == other.id else false

    override fun hashCode() = id.hashCode()
}