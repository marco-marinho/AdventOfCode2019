package day10

import helpers.readFile
import helpers.Point
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.abs

fun main() {
    val data = readFile("Data/Day10.txt")
    val asteroids = mutableListOf<Point>()
    data.forEachIndexed { i, el -> el.forEachIndexed { j, c -> if (c == '#') asteroids.add(Point(j, i)) } }
    val inView = mutableMapOf<Point, MutableMap<Double, Point>>()
    for (current in asteroids) {
        val toSearch = asteroids.filter { el -> el != current }.sortedBy { el -> el.distance(current) }
        val inViewRight = mutableMapOf<Double, Point>()
        for (other in toSearch) {
            val centered = other - current
            var vecAngle = atan2(centered.x.toDouble(), centered.y.toDouble())
            if (vecAngle < 0) vecAngle = PI + abs(vecAngle)
            if (inViewRight.contains(vecAngle)) continue
            inViewRight[vecAngle] = other
        }
        inView[current] = inViewRight
    }
    val maxList = inView.maxBy { el -> el.value.size}.value.map{ (angle, point) -> Pair(angle, point)}.sortedBy { it.first }
    println("Task 01: ${maxList.size}")
    val task02 = maxList[199].second.x * 100 + maxList[199].second.y
    println("Task 02: $task02")
}