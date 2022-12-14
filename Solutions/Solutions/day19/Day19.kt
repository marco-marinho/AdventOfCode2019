package day19

import helpers.readFileLongs
import helpers.Computer
import helpers.Point
import helpers.genMatrix

fun main() {
    val reader = BeamReader()
    val grid = reader.getField(50, 50)
    val count = grid.fold(0){acc, el -> acc + el.count { it == '#' }}
    println("Task 01: $count")
    val pos = reader.getFirstBlock(100, 100)
    val res = pos.x * 10000 + pos.y
    println("Task 02: $res")
}

class BeamReader {
    private val data = readFileLongs("Data/Day19.txt")
    private val cache = mutableMapOf<Point, Char>()

    private fun getOutput(pos: Point): Char {
        if (cache.contains(pos)) return cache[pos]!!
        val computer = Computer(data.toMutableList())
        computer.input.add(pos.x.toLong())
        computer.input.add(pos.y.toLong())
        computer.executeUntilHalt()
        val output = if (computer.output.last() == 1L) '#' else '.'
        cache[pos] = output
        return output
    }

    fun getField(xLim: Int, yLim: Int): List<MutableList<Char>> {
        val output = genMatrix(xLim, yLim, '?')
        for (x in 0 until xLim) {
            for (y in 0 until yLim) {
                output[x][y] = getOutput(Point(x, y))
            }
        }
        return output
    }

    fun getFirstBlock(xLim: Int, yLim: Int): Point {
        var start = 0
        while (true) {
            for (x in 2 until 10000) {
                var startFound = false
                for (y in start until 10000) {
                    val temp = getOutput(Point(x, y))
                    if (temp == '#' && !startFound) {
                        start = y
                        startFound = true
                    }
                    if (temp == '.' && startFound) break
                    val res = testPoint(Point(x, y), xLim, yLim)
                    if (res) return Point(x, y)
                }
            }
        }
    }

    private fun testPoint(point: Point, limX: Int, limY: Int): Boolean {
        return getOutput(point) == '#'
                && getOutput(point + Point(0, limY-1)) == '#'
                && getOutput(point + Point(limX-1, 0)) == '#'
                && getOutput(point + Point(limX-1, limY-1)) == '#'
    }

}
