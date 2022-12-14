package day16

import kotlin.math.abs
import helpers.readFile

fun main() {
    val inputN = readFile("Data/Day16.txt")[0]
    val input = toDigits(inputN)
    val offset = inputN.take(7).toInt()
    apply(input, 100)
    applyWithRepeat(input, 100, offset)
}

fun apply(input: List<Int>, phases: Int) {
    val size = input.size
    var output = input.toList()

    for (phase in 1..phases) {
        output = List(size) { idx ->
            abs((output.zip(factorForIdx(idx + 1, size)).sumOf { it.first * it.second } ) % 10)
        }
    }
    print("Task 01: ")
    println(output.take(8).joinToString(""))
}

fun applyWithRepeat(inputI: List<Int>, phases: Int, offSet: Int) {
    val input = List(10000){inputI}.flatten()
    var output = input.toList().drop(offSet)

    for (phase in 1..phases) {
        var acc = 0
        val temp = output.reversed().map { acc= (acc + it)%10; acc }
        output = temp.reversed()
    }
    print("Task 02: ")
    println(output.take(8).joinToString(""))
}

fun factorForIdx(idx: Int, size: Int): List<Int> {
    val factors = listOf(0, 1, 0, -1)
    val output = factors.flatMap { el -> List(idx) { el } }
    val repeats = (size / (4 * idx)) + 1
    return List(repeats) { output }.flatten().drop(1)
}

fun toDigits(input: String): List<Int> {
    return input.map { it.digitToInt() }
}