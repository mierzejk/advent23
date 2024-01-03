package day25.model

import day25.cs

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