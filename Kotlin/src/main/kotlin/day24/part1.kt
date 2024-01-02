@file:Suppress("FunctionName")

package day24

import day11.combinations
import java.io.File
import kotlin.math.sign

internal data class Point(val x: Double, val y: Double) {
    operator fun plus(vector: Vector) = Point(x + vector.x, y + vector.y)
}
internal data class Vector(val x: Double, val y: Double) {
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)

    operator fun times(vector: Vector) = this dot vector

    @Suppress("MemberVisibilityCanBePrivate")
    infix fun dot(vector: Vector) = (x * vector.x) + (y * vector.y)
}
internal data class Hailstone(val position: Point, val velocity: Vector)

internal fun getScalars(a: Hailstone, b: Hailstone): Point? {
    // Parallel → cross product == 0
    if (a.velocity.x * b.velocity.y == b.velocity.x * a.velocity.y) {
        val opposite = a.velocity * b.velocity < 0.0
        return null // TODO
    }

    val eqx = listOf(a.velocity.x, -b.velocity.x, b.position.x - a.position.x)
    val eqy = listOf(a.velocity.y, -b.velocity.y, b.position.y - a.position.y)
    val scaleX = eqy[0] / eqx[0]

    val eliminatedY = (eqy zip eqx.map { it * scaleX }).map { (y, x) -> y - x }.toDoubleArray()
    assert (0.0 == eliminatedY[0])
    eliminatedY[2] /= eliminatedY[1]
    eliminatedY[1] = 1.0
    val eliminatedX = arrayOf(1.0, 0.0, (eqx[2] - (eqx[1] * eliminatedY[2])) / eqx[0])
    val aScalar = eliminatedX[2]
    val bScalar = eliminatedY[2]
    if (listOf(aScalar, bScalar).map(::sign).any { it < 0.0 })
        return null

    val aInter = a.position + a.velocity * aScalar
    val bInter = b.position + b.velocity * bScalar
    assert(aInter == bInter)
    return aInter
}

fun main() {
    val hailstones = File("src/main/resources/day_24_input.txt").useLines { file -> file.map {
        val (position, velocity) = it.split('@')
        val (px, py) = position.split(',').map(String::trim).map(String::toDouble)
        val (vx, vy) = velocity.split(',').map(String::trim).map(String::toDouble)
        Hailstone(Point(px, py), Vector(vx, vy))
    }.toList() }

    val scalars = hailstones.combinations().map { (a, b) -> getScalars(a, b) }
    print(scalars.filterNotNull().count { (x, y) ->
        @Suppress("ConvertTwoComparisonsToRangeCheck")
        2E14 <= x && x <= 4E14 && 2E14 <= y && y <= 4E14
    })
}
