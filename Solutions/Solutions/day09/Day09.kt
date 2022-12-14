package day09

import helpers.readFileLongs
import helpers.Computer

fun main(){
    val data = readFileLongs("Data/Day09.txt")
    var computer = Computer(data.toMutableList())
    computer.input.add(1L)
    computer.executeAll()
    println("Task 01: ${computer.output.last()}")
    computer = Computer(data.toMutableList())
    computer.input.add(2L)
    computer.executeAll()
    println("Task 02: ${computer.output.last()}")
}