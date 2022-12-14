package day13

import helpers.*
import kotlin.math.sign

fun main(){
    val data = readFileLongs("Data/Day13.txt").toMutableList()
    data[0] = 2L
    val computer = Computer(data)
    var state = computer.executeUntilHalt()
    val tiles = computer.output.chunked(3)
    println("Task 01: ${tiles.count {it[2] == 2L}}")
    val game = Game(tiles.map{ it[1] }.max().toInt() + 1, tiles.map { it[0].toInt() }.max().toInt() + 1, false)
    while(state != ComputerState.FINISHED) {
        game.printGame(computer.output.chunked(3))
        computer.output.clear()
        computer.input.add(game.diff().toLong())
        state = computer.executeUntilHalt()
    }
    game.printGame(computer.output.chunked(3))
    println("Task 02: ${game.getScore()}")
}

class Game(height: Int, width: Int, private val draw: Boolean) {
    private var score = 0L
    private val screenBuffer = genMatrix(height, width, ' ')
    private var lastBallPos = Point(0, 0)
    private var lastPaddlePos = Point(0, 0)

    fun diff() :Int = (lastBallPos.y - lastPaddlePos.y).sign

    fun getScore(): Long = score

    fun printGame(tiles: List<List<Long>>) {
        for (entry in tiles){
            val x = entry[1].toInt()
            val y = entry[0].toInt()
            if (y < 0) {
                score = entry[2]
                continue
            }
            when(entry[2]){
                0L -> screenBuffer[x][y] = ' '
                1L -> screenBuffer[x][y] = '#'
                2L -> screenBuffer[x][y] = '@'
                3L -> {
                    screenBuffer[x][y] = '-'
                    lastPaddlePos = Point(x, y)
                }
                4L -> {
                    screenBuffer[x][y] = 'o'
                    lastBallPos = Point(x, y)
                }
            }
        }
        if (draw) printMatrix(screenBuffer)
    }
}