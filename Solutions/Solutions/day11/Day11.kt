package day11

import helpers.*

fun main(){
    val task01 = runRobot(0L).size
    println("Task 01: $task01")
    val task02 = runRobot(1L)
    val minX = task02.minBy { it.key.x }.key.x
    val maxX = task02.maxBy { it.key.x }.key.x + 1
    val minY = task02.minBy { it.key.y }.key.y
    val maxY = task02.maxBy { it.key.y }.key.y + 1
    val board = genMatrix((maxY - minY), (maxX - minX) * 2, "  ")
    task02.forEach{(point, color) -> if (color == 1L) board[point.y - minY][point.x - minX] = "##"}
    println("Task 02: ")
    printMatrix(board)
}

fun runRobot(firstBlock: Long): MutableMap<Point, Long> {
    val data = readFileLongs("Data/Day11.txt")
    val blocks = mutableMapOf<Point, Long>()
    val computer = Computer(data.toMutableList())
    var heading = 'u'
    var state = ComputerState.READY
    var position = Point(0, 0)
    blocks[position] = firstBlock

    while (state != ComputerState.FINISHED){
        val input = blocks.getOrPut(position){0}
        computer.input.add(input)
        state = computer.executeUntilHalt()
        val color = computer.output.dropLast(1).last()
        val turn = computer.output.last()
        blocks[position] = color
        heading = if (turn == 0L) Move.turn(heading, 'l') else Move.turn(heading, 'r')
        position += Move.walk(heading)
    }
    return blocks
}

object Move {

    private val possibilities = charArrayOf('u', 'r', 'd', 'l')
    private val movements = listOf(Point(0,-1), Point(1, 0), Point(0, 1), Point(-1, 0))

    fun turn(heading: Char, turnDirection: Char): Char {
        var curIdx = possibilities.indexOf(heading)
        if (turnDirection == 'r') curIdx++
        else curIdx--
        if (curIdx < 0) curIdx = possibilities.size - 1
        curIdx %= possibilities.size
        return possibilities[curIdx]
    }

    fun walk(heading: Char): Point{
        val idx = possibilities.indexOf(heading)
        return movements[idx]
    }
}