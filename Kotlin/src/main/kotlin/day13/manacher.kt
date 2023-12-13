data class Palindrome(val center: Int, val radius: Int) : Comparable<Palindrome> {
    val range: Int
        get() = center + radius
    init { assert(radius <= center) }
    override operator fun compareTo(other: Palindrome): Int = range.compareTo(other.range)
    fun withRadius(centre: Int) = Palindrome(centre, radius)
    fun withRange(centre: Int) = Palindrome(centre, range - centre)
}

@Suppress("SpellCheckingInspection")
fun<T> manacher(input: Iterable<T>, bogus: T) =
    buildList { add(bogus); input.forEach { add(it); add(bogus) } }.run {
        val palindromes = Array(size) { Palindrome(it, 0) }
        var rightmost = palindromes[0]
        val getMirrored = { i: Int -> (2 * rightmost.center - i).let { when {
            rightmost.range < i || it < 0 -> rightmost
            else -> palindromes[it].withRadius(i)
        } } }
        (1..<size-1).forEach {
            val mirrored = getMirrored(it)
            palindromes[it] = when {
                // Copy
                mirrored < rightmost -> mirrored
                // Cap
                mirrored > rightmost -> rightmost.withRange(it)
                // Expand
                else -> (mirrored.range+1..lastIndex).map {i -> Pair(2 * it - i, i)}
                    .takeWhile { (left, right) -> 0 <= left && (this[left] == this[right]) }
                    .lastOrNull()?.let { (_, right) -> Palindrome(it, right - it) } ?: mirrored
            }.also { result -> if (rightmost < result) rightmost = result }
        }
        palindromes.slice(1..<size-1).map { (it.radius + 1) / 2 }
    }