package day05

import helpers.Computer
import helpers.readFileLongs

fun main(){
    val data = readFileLongs("Data/Day05.txt")
    var cpu = Computer(data.toMutableList())
    cpu.input.add(1)
    cpu.executeAll()
    println("Task 01: ${cpu.output.last()}")
    cpu = Computer(data.toMutableList())
    cpu.input.add(5)
    cpu.executeAll()
    println("Task 02: ${cpu.output.last()}")
}
