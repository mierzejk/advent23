package day07.partii

@Suppress("EnumEntryName")
enum class Card(val strength: Byte) {
    A(0xE),
    K(0xD),
    Q(0xC),
    J(0x1), // Part II
    T(0xA),
    `9`(0x9),
    `8`(0x8),
    `7`(0x7),
    `6`(0x6),
    `5`(0x5),
    `4`(0x4),
    `3`(0x3),
    `2`(0x2);

    companion object {
        operator fun invoke(card: Char) = card.uppercase().let(::valueOf)
    }
}

enum class Type(val strength: Byte) {
    Five(0x7),
    Four(0x6),
    Full(0x5),
    Three(0x4),
    Two(0x3),
    One(0x2),
    High(0x1);

    companion object {
        private fun getType(cards: Collection<Card>) =
            with(cards.groupingBy { it }.eachCount().values.sortedDescending()) {
                when (size) {
                    1 -> Five
                    2 -> if (4 == this[0]) Four else Full
                    3 -> if (3 == this[0]) Three else Two
                    4 -> One
                    else -> High
            }
        }

        operator fun invoke(cards: Collection<Card>): Type {
            val (jokers, others) = cards.partition { Card.J == it }
            return when (jokers.size) {
                0 -> getType(cards)
                5 -> Five
                else -> others.groupingBy { it }.eachCount().maxBy { (_, count) -> count }.key.let { most ->
                    getType(List(jokers.size) { most } + others) }
            }
//            if (5 == jokers.size)
//                return Five
//
//            val most = others.groupingBy { it }.eachCount().maxBy { (_, count) -> count }.key
//            return getType(List(jokers.size) { most } + others)
        }
    }
}

open class Hand(hand: String) : Comparable<Hand> {
    protected val cards: List<Card>
    protected val type: Type
    private val strength: List<Byte>

    init {
        cards = hand.map { Card(it) }
        type = Type(cards)
        strength = listOf(type.strength) + cards.map(Card::strength)
    }

    override fun toString() = "${cards.joinToString("")}: $type"

    override fun compareTo(other: Hand) =
        if (this === other) 0
        else (strength zip other.strength).map { (a, b) -> a.compareTo(b) }.firstOrNull { 0 != it} ?: 0
}