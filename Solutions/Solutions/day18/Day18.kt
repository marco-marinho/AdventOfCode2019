package day18

import helpers.Grid
import helpers.readFile
import helpers.Point
import helpers.indexOf2D
import java.util.PriorityQueue

typealias LocationSet = Pair<MutableMap<Char, Point>, MutableMap<Char, Point>>

data class State(val pos: Point, val steps: Int, val keys: Set<Char>) {
    fun stringRep() = pos.toString() + ":" + keys.toCharArray().sorted().joinToString("")
}

data class MultiState(val positions: MutableList<Point>, val steps: Int,
                      val keys: Set<Char> = setOf()) {

    fun stringRep() = "${positions[0]}-${positions[1]}-${positions[2]}-${positions[3]}:" + keys.toCharArray().sorted()
        .joinToString("")


}

data class Path(val pos: Point, val dist: Int, val neededKeys: Set<Char>, val keysGotten: Set<Char>) :
    Comparable<Path> {
    override fun compareTo(other: Path): Int {
        return dist - other.dist
    }
}

fun main() {
    val data = readFile("Data/Day18.txt")
    val grid = data.map { it.toList() }
    val locations = findDoorsAndKeys(grid)

    task01(grid, locations)
    task02(grid, locations)
}

fun task02(rawGrid: Grid, locations: LocationSet) {
    val (_, keys) = locations
    val start = indexOf2D(rawGrid, '@')
    val grid = updateGrid(rawGrid, start)
    val initialState =
        MultiState(mutableListOf(start + Point(-1, -1), start + Point(-1, 1),
            start + Point(1, -1), start + Point(1, 1)), 0, setOf())
    val states = mutableListOf(initialState)
    val pathFinder = PathFinder(grid)
    val minPos = mutableMapOf<String, Int>()
    var idx = 0
    while (idx < states.size) {
        val curState = states[idx]
        var minPosKey = curState.stringRep()
        var minDist = minPos.getOrDefault(minPosKey, Int.MAX_VALUE)
        if (minDist < curState.steps) {
            idx++
            continue
        }
        for ((botNum, bot) in curState.positions.withIndex()) {
            for (key in keys) {
                if (curState.keys.contains(key.key)) continue
                val path = pathFinder.findPath(bot, key.value)
                if (path.neededKeys.subtract(curState.keys).isEmpty()
                    && path.keysGotten.subtract(curState.keys).isNotEmpty()
                    && path.pos.x != -1
                ) {
                    val newPos = curState.positions.toMutableList()
                    newPos[botNum] = key.value
                    val state = MultiState(newPos, curState.steps + path.dist, curState.keys + path.keysGotten)
                    minPosKey = state.stringRep()
                    minDist = minPos.getOrDefault(minPosKey, Int.MAX_VALUE)
                    if (minDist <= state.steps) {
                        continue
                    }
                    minPos[minPosKey] = state.steps
                    states.add(state)
                }
            }

        }
        idx++
    }
    println("Task 02: ${findMinMulti(states, locations)}")
}


fun task01(grid: Grid, locations: LocationSet) {
    val (_, keys) = locations
    val loc = indexOf2D(grid, '@')
    val initialState = State(loc, 0, setOf())
    val states = mutableListOf(initialState)
    var idx = 0
    val pathFinder = PathFinder(grid)
    val minPos = mutableMapOf<String, Int>()
    while (idx < states.size) {
        val curState = states[idx]
        var minPosKey = curState.stringRep()
        var minDist = minPos.getOrDefault(minPosKey, Int.MAX_VALUE)
        if (minDist < curState.steps) {
            idx++
            continue
        }
        for (key in keys) {
            if (curState.keys.contains(key.key)) continue
            val path = pathFinder.findPath(curState.pos, key.value)
            if (path.neededKeys.subtract(curState.keys).isEmpty()
                && path.keysGotten.subtract(curState.keys).isNotEmpty()
            ) {
                val state = State(key.value, curState.steps + path.dist, curState.keys + path.keysGotten)
                minPosKey = state.stringRep()
                minDist = minPos.getOrDefault(minPosKey, Int.MAX_VALUE)
                if (minDist <= state.steps) {
                    continue
                }
                minPos[minPosKey] = state.steps
                states.add(state)
            }
        }
        idx++
    }
    println("Task 01: ${findMin(states, locations)}")
}

fun updateGrid(gridIn: Grid, pos: Point): Grid {
    val grid = gridIn.map { it.toMutableList() }.toMutableList()
    grid[pos.x - 1][pos.y - 1] = '@'
    grid[pos.x - 1][pos.y] = '#'
    grid[pos.x - 1][pos.y + 1] = '@'
    grid[pos.x][pos.y - 1] = '#'
    grid[pos.x][pos.y] = '#'
    grid[pos.x][pos.y + 1] = '#'
    grid[pos.x + 1][pos.y - 1] = '@'
    grid[pos.x + 1][pos.y] = '#'
    grid[pos.x + 1][pos.y + 1] = '@'
    return grid.map { it.toList() }.toList()
}

class PathFinder(private val grid: Grid) {

    private val cache = mutableMapOf<Pair<Point, Point>, Path>()

    fun findPath(start: Point, end: Point): Path {
        if (!cache.contains(Pair(start, end))) {
            val path = dijkstras(grid, start, end)
            cache[Pair(start, end)] = path
            cache[Pair(end, start)] = path
        }
        return cache[Pair(start, end)]!!
    }
}

fun findMin(states: List<State>, locations: LocationSet): Int {
    val (_, keys) = locations
    var min = Int.MAX_VALUE
    for (state in states) {
        if (state.keys.size == keys.size && state.steps < min) min = state.steps
    }
    return min
}

fun findMinMulti(states: List<MultiState>, locations: LocationSet): Int {
    val (_, keys) = locations
    var min = Int.MAX_VALUE
    for (state in states) {
        if (state.keys.size == keys.size && state.steps < min) min = state.steps
    }
    return min
}

fun dijkstras(grid: Grid, start: Point, target: Point): Path {
    val limX = grid.size
    val limY = grid[0].size
    val toVisit = PriorityQueue<Path>()
    val startEl = grid[start.x][start.y]
    var startKeys = setOf<Char>()
    if (startEl.isLowerCase()) startKeys = startKeys + startEl
    toVisit.add(Path(start, 0, setOf(), startKeys))
    val visited = mutableSetOf<Point>()
    while (toVisit.isNotEmpty()) {
        val current = toVisit.remove()
        if (visited.contains(current.pos)) continue
        if (current.pos == target) return current
        for (neighbor in current.pos.neightbours()) {
            var keysNeeded = current.neededKeys
            var keysGotten = current.keysGotten
            if (visited.contains(neighbor)) continue
            if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= limX || neighbor.y >= limY) continue
            val nextVal = grid[neighbor.x][neighbor.y]
            if (nextVal == '#') continue
            if (nextVal.isLowerCase()) keysGotten = keysGotten + nextVal
            if (nextVal.isUpperCase()) keysNeeded = keysNeeded + nextVal.lowercaseChar()
            toVisit.add(Path(neighbor, current.dist + 1, keysNeeded, keysGotten))
        }
        visited.add(current.pos)
    }
    return Path(Point(-1, -1), Int.MAX_VALUE, setOf(), setOf())
}

fun findDoorsAndKeys(grid: Grid): LocationSet {
    val keyLocs = mutableMapOf<Char, Point>()
    val doorLocs = mutableMapOf<Char, Point>()
    for (key in 'a'..'z') {
        var loc = indexOf2D(grid, key)
        if (loc.x != -1) keyLocs[key] = loc
        loc = indexOf2D(grid, key - 32)
        if (loc.x != -1) doorLocs[key - 32] = loc
    }
    return Pair(doorLocs, keyLocs)
}