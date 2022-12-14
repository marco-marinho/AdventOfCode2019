package day15

import helpers.*

fun main(){
    val data = readFileLongs("Data/Day15.txt")
    val computer = Computer(data.toMutableList())
    val map = mutableMapOf(Point(0, 0) to 'D')
    search(Point(0, 0), computer, map)
    val board = mapToBoard(map)
    printMatrix(board)
    val start = findInBoard(board, 'D')
    val end = findInBoard(board, '@')
    val distances = bfs(board, start)
    println("Task 01: ${distances[end]}")
    val time = spreadOxygen(board, end)
    println("Task 02: $time")
}

fun spreadOxygen(board: List<MutableList<Char>>, start: Point): Int{
    var toSpread = mutableListOf(start)
    val nextHeads = mutableListOf<Point>()
    val movements = listOf(Point(0, 1), Point(0, -1), Point(1, 0), Point(-1, 0))
    var minutes = 0
    while(toSpread.isNotEmpty()){
        for (point in toSpread){
            for (movement in movements){
                val next = point+movement
                val element = board[next.x][next.y]
                if (element == '@' || element == '#') continue
                nextHeads.add(next)
                board[next.x][next.y] = '@'
            }
        }
        minutes++
        toSpread = nextHeads.toMutableList()
        nextHeads.clear()
    }
    return minutes - 1
}

fun bfs(board: List<MutableList<Char>>, start: Point): MutableMap<Point, Int> {
    val visited = mutableMapOf<Point, Int>()
    val inQueue = mutableMapOf(start to 0)
    val movement = listOf(Point(0, 1), Point(0, -1), Point(1, 0), Point(-1, 0))

    while(inQueue.isNotEmpty()){
        val current = inQueue.firstNotNullOf { it }
        movement.forEach{
            val next = current.key + it
            if (board[next.x][next.y] != '#' && !visited.contains(next) && !inQueue.contains(next)){
                inQueue[next] = current.value + 1
            }
        }
        visited[current.key] = current.value
        inQueue.remove(current.key)
    }

    return visited
}

fun mapToBoard(map: MutableMap<Point, Char>): List<MutableList<Char>> {
    val minX = map.minBy { it.key.x }.key.x
    val maxX = map.maxBy { it.key.x }.key.x + 1
    val minY = map.minBy { it.key.y }.key.y
    val maxY = map.maxBy { it.key.y }.key.y + 1
    val board = genMatrix((maxX - minX), (maxY - minY), ' ')
    map.forEach{ (pos, value) -> board[pos.x - minX][pos.y - minY] = value}
    return board
}

fun findInBoard(board: List<MutableList<Char>>, value: Char): Point {
    for( (i, row) in board.withIndex()){
        for ((j, char) in row.withIndex()){
            if (char == value) return Point(i, j)
        }
    }
    return Point(-1, -1)
}

fun search(position: Point, computer: Computer, map: MutableMap<Point, Char>){
    val movement = mapOf( 1L to Point(0, 1), 2L to Point(0, -1), 3L to Point(1, 0), 4L to Point(-1, 0))
    for (direction in 1L..4L){
        val nextPos = position + movement[direction]!!
        if (map.contains(nextPos)) continue
        val temp = computer.clone()
        temp.input.add(direction)
        temp.executeUntilHalt()
        val output = temp.output.last()
        temp.output.clear()
        when(output){
            0L -> {
                map[nextPos] = '#'
            }
            1L -> {
                map[nextPos] = '.'
                search(nextPos, temp, map)
            }
            2L -> {
                map[nextPos] = '@'
                search(nextPos, temp, map)
            }
            else -> throw IllegalStateException("Invalid report")
        }
    }
}