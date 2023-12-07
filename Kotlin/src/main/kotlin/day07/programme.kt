package day07

import java.io.File

internal class Bid(hand: String, val value: Int) : day07.partii.Hand(hand) {
    constructor(line: String): this(line.take(5), line.drop(6).toInt())
    override fun toString() = "${cards.joinToString("")} [$value]: $type"
}

fun main() {
    val deck = ArrayList<Bid>()
    File("src/main/resources/day_7_input.txt").useLines { file -> file.mapTo(deck, ::Bid) }
    deck.sort()
    deck.foldIndexed(0) { index, acc, bid -> (index + 1) * bid.value + acc }.let(::println)
}