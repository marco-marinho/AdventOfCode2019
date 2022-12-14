package helpers

import java.io.File
import kotlin.math.abs
import kotlin.math.pow
import java.util.Collections
import kotlin.math.sqrt

typealias Grid = List<List<Char>>

inline fun <reified T> MutableList<T>.removeRange(range: IntRange) {
    val fromIndex = range.start
    val toIndex = range.last
    if (fromIndex == toIndex) {
        return
    }

    if (fromIndex >= size) {
        throw IndexOutOfBoundsException("fromIndex $fromIndex >= size $size")
    }
    if (toIndex > size) {
        throw IndexOutOfBoundsException("toIndex $toIndex > size $size")
    }
    if (fromIndex > toIndex) {
        throw IndexOutOfBoundsException("fromIndex $fromIndex > toIndex $toIndex")
    }

    val filtered = filterIndexed { i, t -> i < fromIndex || i > toIndex }
    clear()
    addAll(filtered)
}
fun <T> printMatrix(matrix: List<MutableList<T>>, separator: String = "") {
    for (row in matrix) {
        println(row.joinToString(separator))
    }
}

fun <T> printMatrixGeneric(matrix: Collection<Collection<T>>, separator: String = "") {
    for (row in matrix) {
        println(row.joinToString(separator))
    }
}

fun <T> printMatrixUD(matrix: List<MutableList<T>>, separator: String = "") {
    for (row in matrix.reversed()) {
        println(row.joinToString(separator))
    }
}

fun <T> genMatrix(rows: Int, cols: Int, element: T): List<MutableList<T>> {
    return List(rows) { MutableList(cols) { element } }
}

fun <T> genMatrixImut(rows: Int, cols: Int, element: T): List<List<T>> {
    return List(rows) { List(cols) { element } }
}

fun <T> indexOf2D(matrix: List<List<T>>, element: T): Point {
    for (x in matrix.indices) {
        for (y in matrix[0].indices) {
            if (matrix[x][y] == element) return Point(x, y)
        }
    }
    return Point(-1, -1)
}

fun readFile(path: String): List<String> =
    File(path).useLines { line -> line.map { entry -> entry.replace("\r", "") }.toList() }

fun readFileIntegers(path: String): List<Int> = File(path).readLines()[0].split(",").map { entry -> entry.toInt() }
fun readFileLongs(path: String): List<Long> = File(path).readLines()[0].split(",").map { entry -> entry.toLong() }

data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun neightboursFixedZ(): List<Point3D> {
        return listOf(Point3D(x + 1, y, z), Point3D(x - 1, y, z), Point3D(x, y + 1, z), Point3D(x, y - 1, z))
    }
    fun as2D(): Point{
        return Point(x, y)
    }
}
class CombinationGenerator<T>(private val items: List<T>, choose: Int = 1) : Iterator<List<T>>, Iterable<List<T>> {
    private val indices = Array(choose) { it }
    private var first = true

    init {
        if (items.isEmpty() || choose > items.size || choose < 1)
            error("list must have more than 'choose' items and 'choose' min is 1")
    }

    override fun hasNext(): Boolean = indices.filterIndexed { index, it ->
        when (index) {
            indices.lastIndex -> items.lastIndex > it
            else -> indices[index + 1] - 1 > it
        }
    }.any()

    override fun next(): List<T> {
        if (!hasNext()) error("AINT NO MORE WHA HAPPEN")
        if (!first) {
            incrementAndCarry()
        } else
            first = false
        return List(indices.size) { items[indices[it]] }
    }

    private fun incrementAndCarry() {
        var carry = false
        var place = indices.lastIndex
        do {
            carry = if ((place == indices.lastIndex && indices[place] < items.lastIndex)
                || (place != indices.lastIndex && indices[place] < indices[place + 1] - 1)) {
                indices[place]++
                (place + 1..indices.lastIndex).forEachIndexed { index, i ->
                    indices[i] = indices[place] + index + 1
                }
                false
            } else
                true
            place--
        } while (carry && place > -1)
    }

    override fun iterator(): Iterator<List<T>> = this
}
data class Point(val x: Int, val y: Int) {

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun plus(other: Pair<Int, Int>): Point {
        return Point(x + other.first, y + other.second)
    }

    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }

    fun mahattanDist(other: Point): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    fun distance(other: Point): Double {
        return sqrt((x - other.x).toDouble().pow(2) + (y - other.y).toDouble().pow(2))
    }

    fun slope(other: Point): Double {
        return (other.y - y).toDouble() / ((other.x - x).toDouble())
    }

    fun vector(other: Point): Pair<Double, Double> {
        val x = (other.x - x).toDouble()
        val y = (other.y - y).toDouble()
        val mag = sqrt(x.pow(2) + y.pow(2))
        return Pair(x / mag, y / mag)
    }

    fun mahattanDist(X: Int, Y: Int): Int {
        return abs(x - X) + abs(y - Y)
    }

    override fun toString(): String = "[$x,$y]"

    fun neightbours(): List<Point> {
        return listOf(Point(x + 1, y), Point(x - 1, y), Point(x, y + 1), Point(x, y - 1))
    }

    fun as3D(z: Int): Point3D {
        return Point3D(x, y, z)
    }

}

fun <T> permutations(input: List<T>): List<List<T>> {
    val solutions = mutableListOf<List<T>>()
    permutationsRecursive(input, 0, solutions)
    return solutions
}


private fun <T> permutationsRecursive(input: List<T>, index: Int, answers: MutableList<List<T>>) {
    if (index == input.lastIndex) answers.add(input.toList())
    for (i in index..input.lastIndex) {
        Collections.swap(input, index, i)
        permutationsRecursive(input, index + 1, answers)
        Collections.swap(input, i, index)
    }
}

fun hcf(first: Long, second: Long): Long {
    var n1 = first
    var n2 = second
    while (n1 != n2) {
        if (n1 > n2) n1 -= n2
        if (n2 > n1) n2 -= n1
    }
    return n1
}

fun lcm(first: Long, second: Long): Long {
    return (first * second) / hcf(first, second)
}