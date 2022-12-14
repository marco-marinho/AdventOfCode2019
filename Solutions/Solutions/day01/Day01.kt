package day01

import helpers.readFile

fun main() {
    val task01 = readFile("Data/Day01.txt").map { entry -> entry.toInt() / 3 - 2}.reduce { acc, i -> acc+i }
    val task02 = readFile("Data/Day01.txt").map { entry -> calcFuel(entry.toInt()) }.reduce { acc, i -> acc+i }
    println("Task 01: $task01");
    println("Task 02: $task02");
}

fun calcFuel(entry: Int): Int{
    val fuel = entry / 3 - 2
    if (fuel <= 0) return 0;
    return fuel+ calcFuel(fuel)
}