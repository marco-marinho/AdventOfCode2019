package day20

import helpers.*
import java.util.PriorityQueue

fun main() {
    val data = readFile("Data/Day20.txt")
    val grid = data.map { it.toCharArray().toList() }
    val maze = Maze(grid)
    val task01 = maze.getPath(maze.start, maze.end)
    val task02 = maze.getPath3D(maze.start, maze.end)
    println("Task 01: $task01")
    println("Task 02: $task02")
}

fun getPortals(grid: Grid): MutableMap<String, List<Point>> {
    val output = mutableMapOf<String, List<Point>>()
    for (x in grid.indices) {
        for (y in grid[x].indices) {
            val curChar = grid[x][y]
            if (!curChar.isUpperCase()) continue
            if (grid.size >= x + 2 && grid[x + 1][y].isUpperCase()) {
                val label = "$curChar${grid[x + 1][y]}"
                if ((x - 1) >= 0 && grid[x - 1][y] == '.') output[label] =
                    output.getOrDefault(label, listOf()) + Point(x - 1, y)
                else output[label] = output.getOrDefault(label, listOf()) + Point(x + 2, y)
            }
            if (grid[x].size >= y + 2 && grid[x][y + 1].isUpperCase()) {
                val label = "$curChar${grid[x][y + 1]}"
                if ((y - 1) >= 0 && grid[x][y - 1] == '.') output[label] =
                    output.getOrDefault(label, listOf()) + Point(x, y - 1)
                else output[label] = output.getOrDefault(label, listOf()) + Point(x, y + 2)
            }
        }
    }
    return output
}

class Maze(private val grid: Grid) {

    val start: Point
    val end: Point
    private val connections = mutableMapOf<Point, Point>()

    init {
        val portals = getPortals(grid)
        start = portals["AA"]!![0]
        end = portals["ZZ"]!![0]
        for (entry in portals) {
            if (entry.key == "AA" || entry.key == "ZZ") continue
            connections[entry.value[0]] = entry.value[1]
            connections[entry.value[1]] = entry.value[0]
        }
    }

    fun getPath(start: Point, end: Point): Int {
        val toVisit = PriorityQueue<Path>()
        val visited = mutableSetOf<Point>()
        toVisit.add(Path(start, 0))
        while (toVisit.isNotEmpty()) {
            val current = toVisit.remove()
            if (visited.contains(current.pos)) continue
            visited.add(current.pos)
            if (current.pos == end) return current.dist
            for (neighbour in current.pos.neightbours()) {
                if (grid[neighbour.x][neighbour.y] == '.') toVisit.add(Path(neighbour, current.dist + 1))
            }
            val connection = connections[current.pos]
            if (connection != null) {
                toVisit.add(Path(connections[current.pos]!!, current.dist + 1))
            }

        }
        return -1
    }

    fun getPath3D(start: Point, end: Point): Int {
        val toVisit = PriorityQueue<Path3D>()
        val visited = mutableSetOf<Point3D>()
        toVisit.add(Path3D(start.as3D(0), 0))
        while (toVisit.isNotEmpty()) {
            val current = toVisit.remove()
            if (visited.contains(current.pos)) continue
            visited.add(current.pos)
            if (current.pos == end.as3D(0)) return current.dist
            for (neighbour in current.pos.neightboursFixedZ()) {
                if (grid[neighbour.x][neighbour.y] == '.') toVisit.add(Path3D(neighbour, current.dist + 1))
            }
            val connection = connections[current.pos.as2D()]
            if (connection != null) {
                val modifier: Int = if (current.pos.x == 2 || current.pos.x == grid.size - 3 || current.pos.y == 2 || current.pos.y == grid[0].size - 3) {
                    -1
                } else {
                    1
                }
                if (current.pos.z + modifier >= 0) {
                    toVisit.add(Path3D(connection.as3D(current.pos.z + modifier), current.dist + 1))
                }
            }
        }
        return -1
    }

}

data class Path(val pos: Point, val dist: Int) : Comparable<Path> {
    override fun compareTo(other: Path): Int {
        return dist - other.dist
    }
}

data class Path3D(val pos: Point3D, val dist: Int) : Comparable<Path3D> {
    override fun compareTo(other: Path3D): Int {
        return dist - other.dist
    }
}
