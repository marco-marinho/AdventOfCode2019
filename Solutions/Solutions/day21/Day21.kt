package day21

import helpers.Computer
import helpers.readFileLongs

fun main() {
    val instructiont1 = """
            |NOT B J
            |NOT C T
            |OR T J
            |AND D J
            |NOT A T
            |OR T J
            |WALK
            """.trimMargin()
    runInstructions(instructiont1)
    println()
    val instructiont2 = """
            |NOT B J
            |NOT C T
            |OR T J
            |AND D J
            |AND H J
            |NOT A T
            |OR T J
            |RUN
            """.trimMargin()
    runInstructions(instructiont2)
}

fun runInstructions(instruction: String) {
    val data = readFileLongs("Data/Day21.txt")
    val computer = Computer(data.toMutableList())
    computer.executeUntilHalt()
    printOutput(computer)
    writeInstructions(instruction, computer)
    computer.executeUntilHalt()
    printOutput(computer)
}

fun printOutput(computer: Computer) {
    val screenBuffer = computer.output
    for (entry in screenBuffer) {
        if (entry <= 255) print(entry.toInt().toChar())
        else print(entry)
    }
    print("\n")
    computer.output.clear()
}

fun writeInstructions(instruction: String, computer: Computer) {
    println(instruction)
    for (entry in instruction) {
        computer.input.add(entry.code.toLong())
    }
    computer.input.add(10)
}