package day14

import helpers.readFile
import kotlin.IllegalStateException
import kotlin.math.ceil

fun main() {
    val data = readFile("Data/Day14.txt")
    val reactions = data.map { line ->
        val blocks = line.split(" => ")
        val header = blocks[1].split(" ")
        val components = blocks[0].split(", ").map { it.split(" ") }.map { el -> Pair(el[1], el[0].toLong()) }
        Reaction(header[1], header[0].toLong(), components)
    }
    val reactor = Reactor(reactions)
    reactor.make("FUEL", 1)
    println("Task 01: ${reactor.usedOre}")
    print("Task 02: ${findMax(reactions)}")
}

fun findMax(reactions: List<Reaction>): Long {
    var low = 1L
    var high = 100000000L
    while (low <= high) {
        val mid = (low + high) / 2
        val resMid = Reactor(reactions).make("FUEL", mid)
        if (!resMid && Reactor(reactions).make("FUEL", mid - 1)) {
            return mid-1
        } else if (!resMid) high = mid - 1
        else low = mid + 1
    }
    return -1
}

data class Reaction(val element: String, val number: Long, val components: List<Pair<String, Long>>)

class Reactor(reactionsInput: List<Reaction>) {
    private val reactions = reactionsInput.associateBy { it.element }
    private val stash = mutableMapOf("ORE" to 1000000000000L)
    var usedOre = 0L

    fun make(element: String, quantity: Long): Boolean {
        if (element == "ORE") return false
        val reaction = getReaction(element)
        val toMake =
            (ceil(quantity.toDouble() / reaction.number.toDouble()) * reaction.number).toLong()
        val numReactions = toMake / reaction.number
        for ((component, number) in reaction.components) {
            val available = checkStash(component)
            if (available < number * numReactions) if (!make(component, number * numReactions - available)) return false
            if (!getFromStash(component, number * numReactions)) return false
        }
        stash[element] = stash.getOrPut(element) { 0 } + toMake
        return true
    }

    private fun getReaction(element: String): Reaction {
        val output = reactions[element]
        if (output != null){
            return output
        }
        throw IllegalStateException("Invalid element for this reactor")
    }

    private fun checkStash(element: String): Long {
        if (!reactions.contains(element) && element != "ORE") throw IllegalStateException("Invalid element for this reactor")
        return stash.getOrPut(element) { 0 }
    }

    private fun getFromStash(element: String, number: Long): Boolean {
        if (element == "ORE") {
            if (number > stash["ORE"]!!) {
                return false
            }
            usedOre += number
        }
        val available = stash.getOrPut(element) { 0 }
        if (available < number) throw IllegalStateException("Not enough of $element in stash")
        stash[element] = stash[element]!! - number
        return true
    }
}