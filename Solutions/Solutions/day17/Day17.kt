package day17

import helpers.indexOf2D
import helpers.Point
import helpers.readFileLongs
import helpers.Computer

fun main() {
    val data = readFileLongs("Data/Day17.txt")
    var computer = Computer(data.toMutableList())
    computer.executeAll()
    var out = computer.output.map { it.toInt().toChar() }
    val lineLen = out.indexOf('\n')
    val grid = out.chunked(lineLen + 1).map { it.dropLast(1) }.dropLast(1)
    grid.forEach { el -> println(el.joinToString("")) }
    val intersections = getIntersections(grid)
    val alignment = intersections.fold(0) { acc, el -> acc + (el.x * el.y) }
    println("Task 01: $alignment")
    val target = grid.fold(0) { acc, row -> acc + row.count { it == '#' } }
    val paths = mutableListOf<String>()
    val soFar = "R"
    val pos = indexOf2D(grid, '^')
    traverse('R', pos, intersections, grid, setOf(), soFar, target, paths)
    val program = mutableListOf<Pair<List<String>, String>>()
    paths.forEach { getBlocks(it, 'A', program) }
    val (functions, main) = program[0]
    computer = Computer(data.toMutableList())
    computer.memory[0] = 2L
    computer.executeUntilHalt()
    out = computer.output.map { it.toInt().toChar() }
    out = out.drop(3199)
    print(out.joinToString(""))
    computer.output.clear()
    println(main)
    var code = main.map { it.code.toLong() } + 10L
    computer.input.addAll(code)
    computer.executeUntilHalt()
    out = computer.output.map { it.toInt().toChar() }
    print(out.joinToString(""))
    computer.output.clear()
    for (function in functions){
        code = function.map { it.code.toLong() } + 10L
        computer.input.addAll(code)
        println(function)
        computer.executeUntilHalt()
        out = computer.output.map { it.toInt().toChar() }
        print(out.joinToString(""))
        computer.output.clear()
    }
    println('n')
    computer.input.add('n'.code.toLong())
    computer.input.add(10L)
    computer.executeUntilHalt()
    println("Task 02: ${computer.output.last()}")
}

fun traverse(
    direction: Char,
    pos: Point,
    intersections: List<Point>,
    grid: List<List<Char>>,
    visited: Set<Pair<Point, Char>>,
    soFar: String,
    target: Int,
    solutions: MutableList<String>,
    walked: Int = 0,
    visitedPoints: MutableSet<Point> = mutableSetOf()
) {
    val neighbours = mapOf('U' to Point(-1, 0), 'D' to Point(1, 0), 'R' to Point(0, 1), 'L' to Point(0, -1))
    val turns = listOf('L', 'R', 'S')
    var current = pos
    var curWalked = walked
    while (true) {
        val next = current + neighbours[direction]!!
        if (next.x < 0 || next.y < 0 || next.x >= grid.size || next.y >= grid[0].size || grid[next.x][next.y] != '#') {
            for (turn in listOf('R', 'L')) {
                val tentative = getNextDirection(direction, turn)
                val nextTentative = current + neighbours[tentative]!!
                if (nextTentative.x < 0 || nextTentative.y < 0 || nextTentative.x >= grid.size || nextTentative.y >= grid[0].size) continue
                if (grid[nextTentative.x][nextTentative.y] == '#') {
                    val nextSoFar = "$soFar,$curWalked,$turn"
                    traverse(
                        tentative, current, intersections, grid, visited, nextSoFar, target, solutions,
                        0, visitedPoints.toMutableSet()
                    )
                }
            }
            return
        }
        current = next
        if (!visitedPoints.contains(current)) {
            visitedPoints.add(current)
        }
        curWalked++
        if (visitedPoints.size == target) {
            solutions.add("$soFar,$curWalked,")
            return
        }
        if (intersections.contains(current)) {
            for (turn in turns) {
                val nextDirection = getNextDirection(direction, turn)
                if (visited.contains(Pair(current, nextDirection))) continue
                val nextVisited = visited + Pair(current, nextDirection) + Pair(current, getInverseDirection(direction))
                var nextSoFar = soFar
                if (turn != 'S') {
                    nextSoFar = "$soFar,$curWalked,$turn"
                }
                traverse(
                    nextDirection, current, intersections, grid, nextVisited, nextSoFar, target, solutions,
                    curWalked, visitedPoints.toMutableSet()
                )
            }
            return
        }
    }
}

fun getInverseDirection(direction: Char): Char {
    return when (direction) {
        'D' -> 'U'
        'U' -> 'D'
        'L' -> 'R'
        'R' -> 'L'
        else -> throw IllegalStateException("Invalid direction")
    }
}

fun getBlocks(
    input: String,
    sub: Char,
    solutions: MutableList<Pair<List<String>, String>>,
    replacements: List<String> = listOf()
) {
    val idx = input.indexOfFirst { it != 'A' && it != 'B' && it != ',' }
    for (len in 0..20) {
        if (idx + len >= input.length) break
        val temp = input.slice(idx..idx + len)
        if (temp.last() == 'A' || temp.last() == 'B') break
        if (temp.last() != ',') continue
        if (temp.count { it == ',' } % 2 != 0) continue
        val next = input.replace(temp, "$sub,")
        if (sub != 'C') getBlocks(next, sub + 1, solutions, replacements + temp.dropLast(1))
        else {
            if (next.any { it !in arrayOf('A', 'B', 'C', ',') }) continue
            if (next.length > 21) continue
            solutions.add(Pair(replacements + temp.dropLast(1), next.dropLast(1)))
        }
    }
}

fun getNextDirection(direction: Char, turn: Char): Char {
    val directions = listOf('U', 'R', 'D', 'L')
    var idx = directions.indexOf(direction)
    var offset = 0
    if (turn == 'L') offset -= 1
    if (turn == 'R') offset += 1
    idx += offset
    if (idx < 0) idx = 3
    idx %= directions.size
    return directions[idx]
}

fun getIntersections(grid: List<List<Char>>): MutableList<Point> {
    val nRows = grid.size
    val nCols = grid[0].size
    val neighbours = listOf(listOf(1, 0), listOf(-1, 0), listOf(0, 1), listOf(0, -1))
    val output = mutableListOf<Point>()
    for (row in 1 until nRows - 1) {
        for (col in 1 until nCols - 1) {
            var found = grid[row][col] == '#'
            for (neighbour in neighbours) {
                found = found && grid[row + neighbour[0]][col + neighbour[1]] == '#'
            }
            if (!found) continue
            output.add(Point(row, col))
        }
    }
    return output
}
