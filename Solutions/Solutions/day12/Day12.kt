package day12

import helpers.readFile
import kotlin.math.sign
import helpers.genMatrixImut
import kotlin.math.abs
import helpers.lcm

fun main() {
    val data = readFile("Data/Day12.txt")
    var pos = data.map { line ->
        line.replace("<x=", "")
            .replace("y=", "")
            .replace("z=", "")
            .replace(">", "")
            .split(", ")
            .map { it.toInt() }
    }
    val viewed = listOf(mutableSetOf(), mutableSetOf(), mutableSetOf<String>())
    val foundPeriod = mutableListOf(false, false, false)
    val period = mutableListOf(0, 0, 0L)
    var vel = genMatrixImut(pos.size, pos[0].size, 0)
    for (step in 0 until 10000000L) {
        if (step == 101L){
            val pot = pos.map { el -> abs(el[0]) + abs(el[1]) + abs(el[2]) }
            val kin = vel.map { el -> abs(el[0]) + abs(el[1]) + abs(el[2]) }
            val energies = pot.zip(kin).map { (p, k) -> p * k }
            println("Task 01: ${energies.sum()}")
        }
        if (step != 0L) {
            step(pos, vel).let {
                pos = it.first
                vel = it.second
            }
        }
        for (i in 0..2){
            if (!foundPeriod[i]) {
                val state = pos.map { it[i] }.zip(vel.map { it[i] }).toString()
                if (viewed[i].contains(state)) {
                    foundPeriod[i] = true
                    period[i] = step
                } else{
                    viewed[i].add(state)
                }
            }
        }
        if (foundPeriod.reduce {acc, el -> acc && el}) break
    }
    val time = period.reduce{acc, el -> lcm(acc, el)}
    println("Task 02: $time")
}

fun step(pos: List<List<Int>>, vel: List<List<Int>>): Pair<List<List<Int>>, List<List<Int>>> {
    val nextVel = pos.mapIndexed { idx, el ->
        pos.filter { it != el }
            .fold(listOf(vel[idx][0], vel[idx][1], vel[idx][2]))
            { acc, other ->
                val x = (other[0] - el[0]).sign
                val y = (other[1] - el[1]).sign
                val z = (other[2] - el[2]).sign
                listOf(acc[0] + x, acc[1] + y, acc[2] + z)
            }
    }
    val nextPos = pos.mapIndexed { idx, el ->
        val accEl = nextVel[idx]
        listOf(el[0] + accEl[0], el[1] + accEl[1], el[2] + accEl[2])
    }
    return Pair(nextPos, nextVel)
}