package day22

import helpers.readFile
import java.math.*

fun main() {

    val data = readFile("Data/Day22.txt")
    val deque = Deque(10007)
    for (line in data) {
        val pieces = line.split(" ")
        if (pieces.last() == "stack") deque.dealNew()
        else if (pieces.first() == "cut") deque.cut(pieces.last().toInt())
        else deque.dealWith(pieces.last().toInt())
    }
    println("Task 01: ${deque.idxOf(2019)}")

    val poly = PolyGen(BigInteger("119315717514047"))
    for (line in data.reversed()) {
        val pieces = line.split(" ")
        if (pieces.last() == "stack") poly.dealNew()
        else if (pieces.first() == "cut") poly.cut(pieces.last().toLong())
        else poly.dealWith(pieces.last().toLong())
    }
    println("Task 02: ${poly.shuffle(101741582076661, 2020)}")
}

class PolyGen(val L: BigInteger) {
    var a = BigInteger("1")
    var b = BigInteger("0")

    fun dealNew() {
        a = -a
        b = L - b - BigInteger("1")
    }

    fun cut(n: Long) {
        b = (b + BigInteger(n.toString())).mod(L)
    }

    fun dealWith(n: Long) {
        val z = (n.toBigInteger().modPow((L - BigInteger("2")), L))
        a = (a * z).mod(L)
        b = (b * z).mod(L)
    }

    fun polypow(a: BigInteger, b: BigInteger, m: BigInteger, n: BigInteger): Pair<BigInteger, BigInteger> {
        if (m == BigInteger("0")) {
            return Pair(BigInteger("1"), BigInteger("0"))
        }
        if (m.mod(BigInteger("2")) == BigInteger("0")) {
            return polypow((a * a).mod(n), (a * b + b).mod(n), m / BigInteger("2"), n)
        } else {
            val (c, d) = polypow(a, b, m - BigInteger("1"), n)
            return Pair((a * c).mod(n), (a * d + b).mod(n))
        }
    }

    fun shuffle(N: Long, pos: Long): BigInteger {
        val (a, b) = polypow(a, b, BigInteger(N.toString()), L)
        return (BigInteger(pos.toString()) * a + b).mod(L)
    }

}

class Deque(size: Int) {

    var deque = MutableList(size) { it.toLong() }
    fun dealNew() {
        deque.reverse()
    }

    fun idxOf(n: Int): Int {
        return deque.indexOf(n.toLong())
    }

    fun cut(n: Int) {
        deque = if (n > 0) {
            (deque.drop(n) + deque.take(n)).toMutableList()
        } else {
            (deque.takeLast(-n) + deque.dropLast(-n)).toMutableList()
        }
    }

    fun dealWith(n: Int) {
        val output = MutableList(deque.size) { 0L }
        var idx = 0
        for (i in 0 until deque.size) {
            output[idx] = deque[i]
            idx += n
            idx %= deque.size
        }
        deque = output
    }
}