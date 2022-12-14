package day06

import helpers.readFile

fun main() {
    val data = readFile("Data/Day06.txt")
    val orbits = data.map { entry ->
        val orbits = entry.split(")")
        Pair(orbits[0], orbits[1])
    }
    val center = Center("COM")
    getOrbiters(center, orbits)
    println("Task 01: ${countChildren(center)}")
    val you = find("YOU", center)
    val san = find("SAN", center)
    if (you != null && san != null) {
        if (you.parent != null && san.parent != null){
            println("Task 02: ${dijkstras(you.parent, san.parent.name)}")
        }
    }
}

fun getOrbiters(center: Center, orbits: List<Pair<String, String>>){
    for (orbit in orbits){
        if (orbit.first != center.name) continue
        center.children.add(Center(orbit.second, parent = center))
    }
    for (child in center.children) {
        getOrbiters(child, orbits)
    }
}

fun dijkstras(start: Center, destination: String): Int{
    val toVisit: MutableList<Pair<Center, Int>> = mutableListOf()
    toVisit.add(Pair(start, 0))
    val visited: MutableSet<String> = mutableSetOf()
    while (toVisit.size > 0) {
        val min = toVisit.minBy { element -> element.second }
        val (current, dist) = min
        toVisit.remove(min)
        if (current.name == destination) return dist
        if (visited.contains(current.name)) continue
        visited.add(current.name)
        for (child in current.children){
            if (!visited.contains(child.name)) toVisit.add(Pair(child, dist+1))
        }
        if (current.parent != null){
            if (!visited.contains(current.parent.name)) toVisit.add(Pair(current.parent, dist+1))
        }
    }
    return Int.MAX_VALUE
}

fun find(name: String, current: Center): Center? {
    if (current.name == name) return current
    for (child in current.children) {
        val res = find(name, child)
        if (res != null) return res
    }
    return null
}

fun countChildren(center: Center, depth: Int = 0): Int{
    var output = depth
    for (child in center.children){
        output += countChildren(child, depth+1)
    }
    return output
}


data class Center(val name: String, val children: MutableList<Center> = mutableListOf(), val parent: Center? = null){
    override fun toString(): String {
        val nameList = children.map { it.name }
        val name = parent?.name
        return "[$name : $nameList]"
    }
}