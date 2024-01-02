package day24

import java.io.File
import day11.combinations
import org.la4j.Matrix
import org.la4j.linear.GaussianSolver
import org.la4j.Vector as ArrayVector

internal data class Point3(val x: Double, val y: Double, val z: Double)
internal data class Vector3(val x: Double, val y: Double, val z: Double)
@Suppress("PropertyName")
internal data class Hailstone3(val position: Point3, val velocity: Vector3) {
    val _x = position.x
    val _y = position.y
    val _z = position.z
    val _vx = velocity.x
    val _vy = velocity.y
    val _vz = velocity.z
}

internal fun checkParallel(a: Hailstone3, b: Hailstone3): Int {
    val crossProduct = listOf(
        a.velocity.x * b.velocity.y == b.velocity.x * a.velocity.y,
        a.velocity.x * b.velocity.z == b.velocity.x * a.velocity.z,
        a.velocity.y * b.velocity.z == b.velocity.y * a.velocity.z)
    return crossProduct.count { it }
}

internal operator fun ArrayVector.component1() = this[0]
internal operator fun ArrayVector.component2() = this[1]
internal operator fun ArrayVector.component3() = this[2]
internal operator fun ArrayVector.component4() = this[3]
internal operator fun ArrayVector.component5() = this[4]
internal operator fun ArrayVector.component6() = this[5]

@Suppress("LocalVariableName")
internal fun solve(a: Hailstone3, b: Hailstone3, c: Hailstone3): Double {
    /*
    Uppercase - unknown, lowercase - given
    1) X + t⋅VX = x + t⋅vx → t = (x - X) / (VX - vx)
    2) Y + t⋅VY = y + t⋅vy → t = (y - Y) / (VY - vy)
    3) Z + t⋅VZ = z + t⋅vz → t = (z - Z) / (VZ - vz)
    1 + 2)
    (x - X) / (VX - vx) = (y - Y) / (VY - vy) → (x - X)(VY - vy) = (y - Y)(VX - vx)
  → x⋅VY - x⋅vy - X⋅VY + vy⋅X = y⋅VX - y⋅vx - Y⋅VX + Y⋅vx
 !→ Y⋅VX - X⋅VY = y⋅VX - y⋅vx + Y⋅vx - x⋅VY + x⋅vy - X⋅vy
    1 + 3)
    (x - X) / (VX - vx) = (z - Z) / (VZ - vz) → (x - X)(VZ - vz) = (z - Z)(VX - vx)
  → x⋅VZ - x⋅vz - X⋅VZ + X⋅vz = z⋅VX - z⋅vx - Z⋅VX + Z⋅vx
 !→ Z⋅VX - X⋅VZ = z⋅VX - z⋅vx + Z⋅vx - x⋅VZ + x⋅vz - X⋅vz
    2 + 3)
    (y - Y) / (VY - vy) = (z - Z) / (VZ - vz) → (y - Y)(VZ - vz) = (z - Z)(VY - vy)
  → y⋅VZ - y⋅vz - Y⋅VZ + Y⋅vz = z⋅VY - z⋅vy - Z⋅VY + Z⋅vy
 !→ Z⋅VY - Y⋅VZ = z⋅VY - z⋅vy + Z⋅vy - y⋅VZ + y⋅vz - Y⋅vz
 1] a + b / 1 + 2)
    a_y⋅VX - a_y⋅a_vx + Y⋅a_vx - a_x⋅VY + a_x⋅a_vy - X⋅a_vy = b_y⋅VX - b_y⋅b_vx + Y⋅b_vx - b_x⋅VY + b_x⋅b_vy - X⋅b_vy
  → a_y⋅VX + Y⋅a_vx - a_x⋅VY - a_vy⋅X - b_y⋅VX - Y⋅b_vx + b_x⋅VY + b_vy⋅X = b_x⋅b_vy - b_y⋅b_vx + a_y⋅a_vx - a_x⋅a_vy
  → a_y⋅VX - b_y⋅VX + b_x⋅VY - a_x⋅VY + b_vy⋅X - a_vy⋅X + Y⋅a_vx - Y⋅b_vx = b_x⋅b_vy - b_y⋅b_vx + a_y⋅a_vx - a_x⋅a_vy
 1] (a_y - b_y)VX + (b_x - a_x)VY + 0⋅VZ + (b_vy - a_vy)X + (a_vx - b_vx)Y + 0⋅Z = b_x⋅b_vy - b_y⋅b_vx + a_y⋅a_vx - a_x⋅a_vy
 2] a + b / 1 + 3)
    a_z⋅VX - a_z⋅a_vx + Z⋅a_vx - a_x⋅VZ + a_x⋅a_vz - X⋅a_vz = b_z⋅VX - b_z⋅b_vx + Z⋅b_vx - b_x⋅VZ + b_x⋅b_vz - X⋅b_vz
  → a_z⋅VX + Z⋅a_vx - a_x⋅VZ - X⋅a_vz - b_z⋅VX - Z⋅b_vx + b_x⋅VZ + X⋅b_vz = a_z⋅a_vx - b_z⋅b_vx + b_x⋅b_vz - a_x⋅a_vz
  → a_z⋅VX - b_z⋅VX + b_x⋅VZ - a_x⋅VZ + X⋅b_vz - X⋅a_vz + Z⋅a_vx - Z⋅b_vx = a_z⋅a_vx - b_z⋅b_vx + b_x⋅b_vz - a_x⋅a_vz
 2] (a_z - b_z)VX + 0⋅VY + (b_x - a_x)VZ + (b_vz - a_vz)X + 0⋅Y + (a_vx - b_vx)Z = a_z⋅a_vx - b_z⋅b_vx + b_x⋅b_vz - a_x⋅a_vz
 3] a + b / 2 + 3)
    a_z⋅VY - a_z⋅a_vy + Z⋅a_vy - a_y⋅VZ + a_y⋅a_vz - Y⋅a_vz = b_z⋅VY - b_z⋅b_vy + Z⋅b_vy - b_y⋅VZ + b_y⋅b_vz - Y⋅b_vz
  → a_z⋅VY + Z⋅a_vy - a_y⋅VZ - Y⋅a_vz - b_z⋅VY - Z⋅b_vy + b_y⋅VZ + Y⋅b_vz = a_z⋅a_vy - b_z⋅b_vy + b_y⋅b_vz - a_y⋅a_vz
  → a_z⋅VY - b_z⋅VY + b_y⋅VZ - a_y⋅VZ + Y⋅b_vz - Y⋅a_vz + Z⋅a_vy - Z⋅b_vy = a_z⋅a_vy - b_z⋅b_vy + b_y⋅b_vz - a_y⋅a_vz
 3] 0⋅VX + (a_z - b_z)VY + (b_y - a_y)VZ + 0⋅X + (b_vz - a_vz)Y + (a_vy - b_vy)Z = a_z⋅a_vy - b_z⋅b_vy + b_y⋅b_vz - a_y⋅a_vz
 4] a + c / 1 + 2)
 4] (a_y - c_y)VX + (c_x - a_x)VY + 0⋅VZ + (c_vy - a_vy)X + (a_vx - c_vx)Y + 0⋅Z = c_x⋅c_vy - c_y⋅c_vx + a_y⋅a_vx - a_x⋅a_vy
 5] a + c / 1 + 3)
 5] (a_z - c_z)VX + 0⋅Y + (c_x - a_x)VZ + (c_vz - a_vz)X + 0⋅VY + (a_vx - c_vx)Z = a_z⋅a_vx - c_z⋅c_vx + c_x⋅c_vz - a_x⋅a_vz
 6] a + c / 2 + 3)
 6] 0⋅VX + (a_z - c_z)VY + (c_y - a_y)VZ + 0⋅X + (c_vz - a_vz)Y + (a_vy - c_vy)Z = a_z⋅a_vy - c_z⋅c_vy + c_y⋅c_vz - a_y⋅a_vz
    */
    val A = arrayOf(
        // 1]
        doubleArrayOf(a._y - b._y, b._x - a._x, 0.0, b._vy - a._vy, a._vx - b._vx, 0.0),
        // 2]
        doubleArrayOf(a._z - b._z, 0.0, b._x - a._x, b._vz - a._vz, 0.0, a._vx - b._vx),
        // 3]
        doubleArrayOf(0.0, a._z - b._z, b._y - a._y, 0.0, b._vz - a._vz, a._vy - b._vy),
        // 4]
        doubleArrayOf(a._y - c._y, c._x - a._x, 0.0, c._vy - a._vy, a._vx - c._vx, 0.0),
        // 5]
        doubleArrayOf(a._z - c._z, 0.0, c._x - a._x, c._vz - a._vz, 0.0, a._vx - c._vx),
        // 6]
        doubleArrayOf(0.0, a._z - c._z, c._y - a._y, 0.0, c._vz - a._vz, a._vy - c._vy)
    )
    val B = doubleArrayOf(
        // 1]
        (b._x * b._vy) - (b._y * b._vx) + (a._y * a._vx) - (a._x * a._vy),
        // 2]
        (a._z * a._vx) - (b._z * b._vx) + (b._x * b._vz) - (a._x * a._vz),
        // 3]
        (a._z * a._vy) - (b._z * b._vy) + (b._y * b._vz) - (a._y * a._vz),
        // 4]
        (c._x * c._vy) - (c._y * c._vx) + (a._y * a._vx) - (a._x * a._vy),
        // 5]
        (a._z * a._vx) - (c._z * c._vx) + (c._x * c._vz) - (a._x * a._vz),
        // 6]
        (a._z * a._vy) - (c._z * c._vy) + (c._y * c._vz) - (a._y * a._vz)
    )
    val solver = GaussianSolver(Matrix.from2DArray(A))
    val result = solver.solve(ArrayVector.fromArray(B))
    val (_, _, _, X, Y, Z) = result  // VX, VY, VZ, X, Y, Z
    return X + Y + Z
}

fun main() {
    val hailstones = File("src/main/resources/day_24_input.txt").useLines { file -> file.map {
        val (position, velocity) = it.split('@')
        val (px, py, pz) = position.split(',').map(String::trim).map(String::toDouble)
        val (vx, vy, vz) = velocity.split(',').map(String::trim).map(String::toDouble)
        Hailstone3(Point3(px, py, pz), Vector3(vx, vy, vz))
    }.toList() }

    val (a, b) = hailstones.combinations().filter {
        (a, b) -> 0 == checkParallel(a, b) }.drop(1).take(2)
    println(String.format("%.0f", solve(a.first, a.second, b.second)))
    // drop  0: 849377770236861 - too low
    // drop  1: 849377770236905 - OK
    // drop  2: 849377770236905
    // drop  3: 849377770236904
    // drop  4: 849377770236904
    // drop  5: 849377770236906 - too high
    // drop  6: 849377770236905
}