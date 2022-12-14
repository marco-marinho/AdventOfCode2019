package helpers

private fun parse(input: MutableList<Long>, ip: Long): Instruction {
    val ip = ip.toInt()
    val header = parseOpCode(input[ip])

    val output = when (header.operation) {
        99L -> {
            Instruction(header, 0, 0, 0)
        }

        1L, 2L, 7L, 8L -> {
            Instruction(header, input[ip + 1], input[ip + 2], input[ip + 3])
        }

        3L, 4L, 9L -> {
            Instruction(header, input[ip + 1], 0, 0)
        }

        5L, 6L -> {
            Instruction(header, input[ip + 1], input[ip + 2], 0)
        }

        else -> {
            throw IllegalStateException("Invalid Opcode")
        }
    }
    return output
}

private fun parseOpCode(input: Long): Header {
    val opcode = input % 100
    val mode1 = (input / 100) % 10
    val mode2 = (input / 1000) % 10
    val mode3 = (input / 10000) % 10
    return Header(opcode, mode1, mode2, mode3)
}

private data class Header(val operation: Long, val mode1: Long, val mode2: Long, val mode3: Long)

private data class Instruction(val header: Header, val param1: Long, val param2: Long, val param3: Long)

enum class ComputerState {
    READY, FINISHED, WAITING_FOR_INPUT
}

data class Computer(
    val memory: MutableList<Long>,
    var pointer: Long = 0,
    var base: Long = 0,
    val input: MutableList<Long> = mutableListOf(),
    val output: MutableList<Long> = mutableListOf(),
    val extraMemory: MutableMap<Long, Long> = mutableMapOf()
) {

    fun clone(): Computer{
        return this.copy(memory = this.memory.toMutableList(), pointer = this.pointer, base = this.base, input = this.input.toMutableList(), output = this.output.toMutableList(), extraMemory = this.extraMemory.toMutableMap())
    }

    fun executeAll() {
        do {
            val state = execute()
        } while (state != ComputerState.FINISHED)
    }

    fun executeUntilHalt(): ComputerState {
        var state = ComputerState.READY
        while (state == ComputerState.READY) {
            state = execute()
        }
        return state
    }

    fun execute(): ComputerState {
        val instruction = parse(memory, pointer)
        val param1 = when (instruction.header.mode1) {
            1L -> instruction.param1
            2L -> memGet(base + instruction.param1)
            else -> memGet(instruction.param1)
        }
        val param2 = when (instruction.header.mode2) {
            1L -> instruction.param2
            2L -> memGet(base + instruction.param2)
            else -> memGet(instruction.param2)
        }
        val param3 = when (instruction.header.mode3) {
            0L -> instruction.param3
            2L -> base + instruction.param3
            else -> throw IllegalStateException("Parameter 3 set to direct")
        }
        when (instruction.header.operation) {
            99L -> {
                return ComputerState.FINISHED
            }

            1L -> {
                memSet(param3, param1 + param2)
                pointer += 4
            }

            2L -> {
                memSet(param3, param1 * param2)
                pointer += 4
            }

            3L -> {
                if (input.size == 0) {
                    return ComputerState.WAITING_FOR_INPUT
                }
                when(instruction.header.mode1) {
                    0L -> memSet(instruction.param1, input[0])
                    2L -> memSet(base + instruction.param1, input[0])
                    else -> throw IllegalStateException("Parameter set to direct when writing")
                }
                input.removeFirst()
                pointer += 2
            }

            4L -> {
                output.add(param1)
                pointer += 2
            }

            5L -> {
                if (param1 != 0L) pointer = param2
                else pointer += 3
            }

            6L -> {
                if (param1 == 0L) pointer = param2
                else pointer += 3
            }

            7L -> {
                val toStore = if (param1 < param2) 1L else 0L
                memSet(param3, toStore)
                pointer += 4
            }

            8L -> {
                val toStore = if (param1 == param2) 1L else 0L
                memSet(param3, toStore)
                pointer += 4
            }

            9L -> {
                base += param1
                pointer += 2
            }

            else -> {
                throw IllegalStateException("Invalid Opcode")
            }
        }
        return ComputerState.READY
    }

    private fun memGet(idx: Long): Long {
        if (idx < memory.size) {
            return memory[idx.toInt()]
        }
        return extraMemory.getOrPut(idx) { 0L }
    }

    private fun memSet(idx: Long, value: Long) {
        if (idx < memory.size) {
            memory[idx.toInt()] = value
        } else {
            extraMemory[idx] = value
        }
    }

}
