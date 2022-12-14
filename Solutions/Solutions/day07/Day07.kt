package day07

import helpers.Computer
import helpers.ComputerState
import helpers.readFileLongs
import helpers.permutations

fun main() {
    task01()
    task02()
}

fun task01() {
    val data = readFileLongs("Data/Day07.txt")
    val phases = listOf(0L, 1L, 2L, 3L, 4L)
    val outputs = mutableListOf<Long>()
    dfs(0, phases, data, outputs)
    println("Task 01: ${outputs.max()}")
}

fun task02() {
    val data = readFileLongs("Data/Day07.txt")
    val phases = permutations(listOf(5L, 6L, 7L, 8L, 9L))
    val results = mutableSetOf<Long>()
    for (phase in phases) {
        results.add(ampChain(phase, data))
    }
    println("Task 02: ${results.max()}")
}

fun ampChain(phases: List<Long>, program: List<Long>): Long {
    val computers = List(5) { Computer(program.toMutableList()) }
    for ((idx, computer) in computers.withIndex()) {
        computer.input.add(phases[idx])
    }
    var forwardInput = 0L
    var finished = false
    while (!finished) {
        for ((idx, computer) in computers.withIndex()) {
            computer.input.add(forwardInput)
            val state = computer.executeUntilHalt()
            forwardInput = computer.output.last()
            if (idx == 4 && state == ComputerState.FINISHED) {
                finished = true
            }
        }
    }
    return computers.last().output.last()
}

fun dfs(input: Long, availablePhases: List<Long>, program: List<Long>, output: MutableList<Long>) {
    if (availablePhases.isEmpty()) {
        output.add(input)
        return
    }
    for (phase in availablePhases) {
        val ampOut = runAmplifier(phase, input, program)
        val nextPhases = availablePhases.filter { it != phase }
        dfs(ampOut, nextPhases, program, output)
    }
}

fun runAmplifier(phase: Long, input: Long, program: List<Long>): Long {
    val computer = Computer(program.toMutableList())
    computer.input.add(phase)
    computer.input.add(input)
    computer.executeAll()
    return computer.output.first()
}