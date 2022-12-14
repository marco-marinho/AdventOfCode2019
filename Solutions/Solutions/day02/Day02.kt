package day02

import helpers.readFileIntegers

fun main() {
    println("Task 01: ${runOnce(12, 2)}")
    println("Task 02: ${search(19690720)}")
}

fun search(target: Int): Int {
    for (noun in 0..99) {
        for (verb in 0..99) {
            if (runOnce(noun, verb) != target) continue
            return 100 * noun + verb
        }
    }
    return -1
}

fun runOnce(noun: Int, verb: Int): Int {
    val memory = GetData("Data/Day02.txt")
    memory[1] = noun
    memory[2] = verb
    process(memory, 0)
    return memory[0]
}

object GetData{
    private var data: List<Int> = emptyList()
    operator fun invoke(path: String): MutableList<Int>{
        if (data.isEmpty()){
            data = readFileIntegers(path)
        }
        return data.toMutableList()
    }
}

fun process(data: MutableList<Int>, start: Int) {
    if (data[start] == 99) return
    if (data[start] == 1) {
        data[data[start + 3]] = data[data[start + 1]] + data[data[start + 2]]
    }
    if (data[start] == 2) {
        data[data[start + 3]] = data[data[start + 1]] * data[data[start + 2]]
    }
    process(data, start + 4)
}