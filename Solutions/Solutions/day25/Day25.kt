package day25

import helpers.*
import kotlin.math.max
import java.util.PriorityQueue

data class Node(val name: String, val connections: MutableMap<String, Node> = mutableMapOf(), val paths: MutableSet<String> = mutableSetOf()){
    override fun toString(): String {
        return name
    }
}

data class Path(val node: Node, val dist: Int, val command: String, val previous: Path?) :
    Comparable<Path> {
    override fun compareTo(other: Path): Int {
        return dist - other.dist
    }
}

fun main() {

    val data = readFileLongs("Data/Day25.txt")
    val computer = Computer(data.toMutableList())
    val nameRegex = "== (.+?) ==".toRegex()
    val codeRegex = " ([0-9]+) ".toRegex()
    val cur = "Hull Breach"

    val unvisited = mutableSetOf(cur)
    val visited = mutableSetOf<String>()
    val nodes = mutableMapOf(cur to Node(cur))

    while (unvisited.isNotEmpty()) {
        val current = unvisited.first()
        unvisited.remove(current)
        visited.add(current)
        computer.executeUntilHalt()
        val output = computer.output.map { it.toInt().toChar() }.joinToString("")
        printOutput(computer)
        val name = nameRegex.find(output)!!.groups.last()!!.value
        if (!nodes.contains(name)) nodes[name] = Node(name)
        takeItems(output, computer)
        val neighbours = findPaths(output)
        nodes[name]!!.paths.addAll(neighbours)
        if (name != "Security Checkpoint") {
            for (path in nodes[name]!!.paths) {
                writeInstructions(path, computer)
                computer.executeUntilHalt()
                val tempOutput = computer.output.map { it.toInt().toChar() }.joinToString("")
                val tempName = nameRegex.find(tempOutput)!!.groups.last()!!.value
                printOutput(computer)
                if (!visited.contains(tempName)) unvisited.add(tempName)
                if (!nodes.contains(tempName)) nodes[tempName] = Node(tempName)
                nodes[name]!!.connections[path] = nodes[tempName]!!
                nodes[tempName]!!.connections[reverse(path)] = nodes[name]!!
                writeInstructions(reverse(path), computer)
                computer.executeUntilHalt()
                printOutput(computer)
            }
        }
        val next = if (unvisited.isNotEmpty()) unvisited.first() else "Security Checkpoint"
        val path = dijkstras(nodes, current, next)
        val commands = pathToCommands(path)
        for (command in commands) {
            printOutput(computer)
            writeInstructions(command, computer)
            computer.executeUntilHalt()
        }

    }
    printOutput(computer)
    writeInstructions("inv", computer)
    computer.executeUntilHalt()
    val output = computer.output.map { it.toInt().toChar() }.joinToString("")
    val start = output.indexOf("inventory:\n")+11
    val end = output.indexOf("Command")-2
    val items = output.slice(start..end).split("\n").map { it.replace("- ", "") }.dropLast(1)
    var code = ""
    var entered = false
    printOutput(computer)
    for (drop in 1 .. 7) {
        val combinations = CombinationGenerator(items, drop)
        for (combination in combinations) {
            val saveState = computer.clone()
            for (item in combination) {
                writeInstructions("drop $item", saveState)
                saveState.executeUntilHalt()
                printOutput(saveState)
            }
            writeInstructions("north", saveState)
            saveState.executeUntilHalt()
            val tempOutput = saveState.output.map { it.toInt().toChar() }.joinToString("")
            val found = codeRegex.find(tempOutput)
            if (found != null) {
                code = found.groups[1]!!.value
                entered = true
                printOutput(saveState)
                break
            }
            printOutput(saveState)
        }
        if (entered) break
    }
    println("Task 01: $code")
}

fun pathToCommands(path: Path): List<String> {
    val output = mutableListOf<String>()
    var head: Path? = path
    while (head != null) {
        output.add(head.command)
        head = head.previous
    }
    return output.reversed().drop(1)
}

fun reverse(command: String): String{
    return when(command){
        "north" -> "south"
        "south" -> "north"
        "east" -> "west"
        "west" -> "east"
        else -> throw IllegalStateException("Invalid command")
    }
}

fun findPaths(output: String): MutableList<String> {
    val res = mutableListOf<String>()
    if (output.contains("Doors here lead:")) {
        val start = output.indexOf("lead:\n") + 6
        val end = max(output.indexOf("Command"), output.indexOf("Items")) - 2
        val paths = output.slice(start..end)
        val possiblePaths = paths.split("\n").map { it.replace("- ", "") }
        for (path in possiblePaths) {
            if (path == "north") res.add("north")
            if (path == "south") res.add("south")
            if (path == "west") res.add("west")
            if (path == "east") res.add("east")
        }
    }
    return res
}


fun dijkstras(nodes: MutableMap<String, Node>, start: String, target: String): Path {
    val toVisit = PriorityQueue<Path>()
    toVisit.add(Path(nodes[start]!!, 0, "", null))
    val visited = mutableSetOf<String>()
    while (toVisit.isNotEmpty()) {
        val current = toVisit.remove()
        if (visited.contains(current.node.name)) continue
        if (current.node.name == target) return current
        for (entry in current.node.connections) {
            val (path, neighbor) = entry
            if (visited.contains(neighbor.name)) continue
            toVisit.add(Path(neighbor, current.dist + 1, path, current))
        }
        visited.add(current.node.name)
    }
    return Path(Node("Null"), Int.MAX_VALUE, "", null)
}

fun takeItems(output: String, computer: Computer) {
    val avoidItems = setOf("infinite loop", "molten lava", "giant electromagnet", "escape pod", "photons")
    if (output.contains("Items here:")) {
        val start = output.indexOf("Items here:") + 12
        val stop = output.indexOf("Command?") - 2
        val items = output.slice(start..stop)
        val itemList = items.split("\n").map { it.replace("- ", "").replace("\n", "") }
        for (item in itemList) {
            if (item.isNotEmpty() && !avoidItems.contains(item)) {
                writeInstructions("take $item", computer)
                computer.executeUntilHalt()
                printOutput(computer)
            }
        }
    }
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
