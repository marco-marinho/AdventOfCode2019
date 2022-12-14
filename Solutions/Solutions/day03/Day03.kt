package day03

import helpers.readFile
import helpers.Point

fun main() {
    val wire1 = readFile("Data/Day03.txt")[0].split(",")
    val wire2 = readFile("Data/Day03.txt")[1].split(",")
    val visited1 = getVisited(wire1)
    val visited2 = getVisited(wire2)
    val intersection = visited1.toSet().intersect(visited2.toSet())
    val distances1 = intersection.map { point -> point.mahattanDist(0, 0) }
    val distances2 = intersection.map { point -> visited1.indexOf(point) + visited2.indexOf(point) + 2 }
    val task1 = distances1.min()
    val task2 = distances2.min()
    println("Task 01: $task1")
    println("Task 02: $task2")
}

fun getVisited(wire: List<String>): MutableList<Point> {
    var current = Point(0, 0)
    val visited = mutableListOf<Point>()
    for (entry in wire) {
        val newEntries = walk(entry, current)
        visited.addAll(newEntries)
        current = newEntries.last()
    }
    return visited
}

fun walk(input: String, currentInput: Point): List<Point> {
    var current = currentInput
    val direction = input[0]
    val steps = input.slice(1 until input.length).toInt()
    val change = when (direction) {
        'R' -> Pair(1, 0)
        'L' -> Pair(-1, 0)
        'U' -> Pair(0, 1)
        'D' -> Pair(0, -1)
        else -> Pair(0, 0)
    }
    val output = List(steps) {
        current += change
        current
    }
    return output
}