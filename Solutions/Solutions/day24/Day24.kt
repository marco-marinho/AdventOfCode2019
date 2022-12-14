package day24

import helpers.*

fun main() {
    var grid = readFile("Data/Day24.txt").map { it.toCharArray().toList() }
    val history = mutableSetOf<String>()
    history.add(grid.toString())
    grid = genNext(grid)
    while(!history.contains(grid.toString())){
        history.add(grid.toString())
        grid = genNext(grid)
    }
    println("Task 01: ${calcDiversity(grid)}")

    grid = readFile("Data/Day24.txt").map { it.toCharArray().toList() }
    val bugs = mutableSetOf<Point3D>()
    grid.mapIndexed { x, it -> it.mapIndexed { y, el -> if (el == '#') bugs.add(Point3D(x, y, 0)) } }
    for (minute in 1..200) {
        val toRemove = mutableSetOf<Point3D>()
        val toAdd = mutableMapOf<Point3D, Int>()
        for (bug in bugs) {
            val (count, neighbours) = countAdjacentRecursive(bug, bugs)
            if (count != 1) toRemove.add(bug)
            for (neighbour in neighbours) {
                toAdd[neighbour] = toAdd.getOrDefault(neighbour, 0) + 1
            }
        }
        for (entry in toAdd) {
            if (bugs.contains(entry.key)) continue
            if (entry.value == 1 || entry.value == 2) {
                bugs.add(entry.key)
            }
        }
        bugs.removeAll(toRemove)
    }
    println("Task 02: ${bugs.size}")
}

fun printDepth(bugs: MutableSet<Point3D>, level: Int){
    val output = genMatrix(5, 5, '.')
    for (x in 0 .. 4){
        for (y in 0 .. 4){
            if (bugs.contains(Point3D(x, y, level))) output[x][y] = '#'
        }
    }
    printMatrixGeneric(output)
}

fun calcDiversity(grid: Grid): Long {
    return grid.flatten().foldIndexed(0L) { idx, acc, el -> if (el == '#') acc + (1 shl idx) else acc }
}

fun genNext(grid: Grid): List<List<Char>> {
    val out = grid.mapIndexed { x, it ->
        it.mapIndexed { y, el ->
            val count = countAdjacent(Point(x, y), grid)
            if (el == '#' && count != 1) '.'
            else if (el == '.' && (count == 1 || count == 2)) '#'
            else el
        }
    }
    return out
}

fun countAdjacent(pos: Point, grid: Grid): Int {
    var count = 0
    for (neighbour in pos.neightbours()) {
        if (neighbour.x < 0 || neighbour.y < 0 || neighbour.x >= grid.size || neighbour.y >= grid[0].size) continue
        if (grid[neighbour.x][neighbour.y] == '#') count++
    }
    return count
}

fun countAdjacentRecursive(pos: Point3D, bugs: Set<Point3D>): Pair<Int, MutableSet<Point3D>> {
    var count = 0
    val neighbours = mutableSetOf<Point3D>()
    for (neighbour in pos.neightboursFixedZ()) {
        if (neighbour.y < 0) {
            neighbours.add(Point3D(2, 1, pos.z - 1))
            if (bugs.contains(Point3D(2, 1, pos.z - 1))) count++
        } else if (neighbour.y > 4) {
            neighbours.add(Point3D(2, 3, pos.z - 1))
            if (bugs.contains(Point3D(2, 3, pos.z - 1))) count++
        } else if (neighbour.x < 0) {
            neighbours.add(Point3D(1, 2, pos.z - 1))
            if (bugs.contains(Point3D(1, 2, pos.z - 1))) count++
        } else if (neighbour.x > 4) {
            neighbours.add(Point3D(3, 2, pos.z - 1))
            if (bugs.contains(Point3D(3, 2, pos.z - 1))) count++
        } else if (neighbour.x == 2 && neighbour.y == 2 && pos.x == 2 && pos.y == 1) {
            for (x in 0..4) {
                neighbours.add(Point3D(x, 0, pos.z + 1))
                if (bugs.contains(Point3D(x, 0, pos.z + 1))) count++
            }
        } else if (neighbour.x == 2 && neighbour.y == 2 && pos.x == 2 && pos.y == 3) {
            for (x in 0..4) {
                neighbours.add(Point3D(x, 4, pos.z + 1))
                if (bugs.contains(Point3D(x, 4, pos.z + 1))) count++
            }
        } else if (neighbour.x == 2 && neighbour.y == 2 && pos.x == 1 && pos.y == 2) {
            for (y in 0..4) {
                neighbours.add(Point3D(0, y, pos.z + 1))
                if (bugs.contains(Point3D(0, y, pos.z + 1))) count++
            }
        } else if (neighbour.x == 2 && neighbour.y == 2 && pos.x == 3 && pos.y == 2) {
            for (y in 0..4) {
                neighbours.add(Point3D(4, y, pos.z + 1))
                if (bugs.contains(Point3D(4, y, pos.z + 1))) count++
            }
        } else {
            neighbours.add(neighbour)
            if (bugs.contains(neighbour)) count++
        }
    }
    return Pair(count, neighbours)
}