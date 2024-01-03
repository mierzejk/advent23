package day25

import day25.model.Edge
import day25.model.Vertex
import java.io.File

fun main() {
    val vertices = mutableMapOf<String, Vertex>()
    val edges = mutableSetOf<Edge>()

    fun getOrPutVertex(id: String) = vertices.getOrPut(id) { Vertex(listOf(id)) }
    fun getOrPutVertex(ids: List<String>) = vertices.getOrPut(ids.cs()) { Vertex(ids) }

    File("src/main/resources/test.txt").useLines { file -> file.forEach { line ->
        val (from, toList) = line.split(":")
        val fromVertex = getOrPutVertex(from.trim())
        edges.addAll(toList.split(" ").filterNot { it.isEmpty() }.map(::getOrPutVertex). map { toVertex ->
            fromVertex.getOrAddEdge(toVertex).also { toVertex.getOrPutEdge(fromVertex, it) } })
    } }
    println(edges)
}

