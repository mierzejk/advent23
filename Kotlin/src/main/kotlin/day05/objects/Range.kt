package day05.objects

internal class Range(destinationStart: ULong, sourceStart: ULong, length: ULong) {
    private val destinationRange = destinationStart..<destinationStart+length
    internal val sourceRange = sourceStart..<sourceStart+length
    internal val delta = destinationStart - sourceStart

    operator fun get(value: ULong) = if (value in sourceRange) value + delta else null
    fun reverse(value: ULong) = if (value in destinationRange) value - delta else null

    companion object {
        private val rangeRe = Regex("""\D*(?<destination>\d+)\s+(?<source>\d+)\s+(?<length>\d+)\D*""")
        operator fun invoke(range: String) = rangeRe.find(range)?.run {
            groupValues.takeLast(3).map(String::toULong).let { (src, dst, len) -> Range(src, dst, len) }
        }
    }
}