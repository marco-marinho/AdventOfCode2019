package day08

import helpers.readFile

fun main() {
    val data = readFile("Data/Day08.txt")[0]
    val width = 25
    val height = 6
    val layers = data.length / (width * height)
    var idx = 0
    val image = Array(layers) { Array(height) { Array(width) { data[idx++].digitToInt() } } }
    val zeros = image.map { el -> countElements(el, 0) }
    val leastZeros = zeros.indices.minBy { zeros[it] }
    val task01 = countElements(image[leastZeros], 1) * countElements(image[leastZeros], 2)
    println("Task 01: $task01")
    println("Task 02:")
    decodeImage(image)
}

fun countElements(matrix: Array<Array<Int>>, element: Int) =
    matrix.sumOf { el -> el.count { it == element } }

fun decodeImage(image: Array<Array<Array<Int>>>){
    val layers = image.size
    val height = image[0].size
    val width = image[0][0].size
    val sb = StringBuilder(width*2)
    for (row in 0 until height){
        for (col in 0 until width){
            for (layer in 0 until layers){
                when(image[layer][row][col]){
                    2 -> continue
                    1 -> sb.append("##")
                    0 -> sb.append("  ")
                    else -> continue
                }
                break
            }
        }
        println(sb.toString())
        sb.clear()
    }
}