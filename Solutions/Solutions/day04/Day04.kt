package day04

fun main() {
    var validFirst = 0
    var validSecond = 0
    for (i in 147981..691423) {
        if (checkFirst(i)) validFirst++
        if (checkSecond(i)) validSecond++
    }
    println("Task 01: $validFirst")
    println("Task 02: $validSecond")
}

fun checkFirst(input: Int): Boolean {
    val num = input.toString()
    val cond = num.zipWithNext().fold(Pair(true, false)) { valid, pair ->
        Pair(
            valid.first && pair.first <= pair.second,
            valid.second || pair.first == pair.second
        )
    }
    return cond.first && cond.second
}

fun checkSecond(input: Int): Boolean {
    val num = input.toString()
    val increasing = num.zipWithNext().fold(true) { valid, pair -> valid && pair.first <= pair.second }
    val pair = num.groupBy { it }.any { it.value.size == 2 }
    return increasing && pair
}