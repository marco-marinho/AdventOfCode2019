package day23

import helpers.Computer
import helpers.ComputerState
import helpers.readFileLongs
import helpers.removeRange

fun main(){

    val data = readFileLongs("Data/Day23.txt")
    val computers = List(50) { Computer(data.toMutableList()) }
    computers.mapIndexed{ idx, it -> it.input.add(idx.toLong())}

    val queue = mutableMapOf<Int, MutableList<Long>>()

    for (i in computers.indices){
        queue[i] = mutableListOf()
    }
    queue[255] = mutableListOf()

    val setYNat = mutableSetOf<Long>()

    while(true) {

        if (queue[255]!!.size > 0){
            val temp = queue[255]!!.takeLast(2)
            if (setYNat.contains(temp[1])) break
            setYNat.add(temp[1])
            computers[0].input.addAll(temp)
        }
        var idle = false
        var idleCount = 0
        while (!idle) {
            for ((idx, computer) in computers.withIndex()) {
                val status = computer.execute()
                if (status == ComputerState.WAITING_FOR_INPUT) {
                    if (queue[idx]!!.size < 2) {
                        computer.input.add(-1)
                        computer.execute()
                    } else {
                        computer.input.addAll(queue[idx]!!.take(2))
                        queue[idx]!!.removeRange(0..1)
                        computer.execute()
                    }
                }
                if (computer.output.size == 0) continue
                while (computer.output.size < 3) computer.execute()
                queue[computer.output[0].toInt()]!!.add(computer.output[1])
                queue[computer.output[0].toInt()]!!.add(computer.output[2])
                computer.output.removeRange(0..2)
            }
            val queueCount = queue.count { it.key != 255 && it.value.size > 0 }
            if (queueCount == 0) idleCount++
            if (idleCount >= 10000) idle = true
        }
    }

    println("Task 01: ${queue[255]!![1]}")
    println("Task 02: ${queue[255]!!.last()}")
}
