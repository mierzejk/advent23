package day05.objects

internal class Range(private val destinationStart: ULong, sourceStart: ULong, length: ULong) {
    private val sourceRange = sourceStart..<sourceStart+length

    operator fun contains(value: ULong) = value in sourceRange
    operator fun get(value: ULong) = if (value in this) value - sourceRange.first + destinationStart else null

    companion object {
        private val rangeRe = Regex("""\D*(?<destination>\d+)\s+(?<source>\d+)\s+(?<length>\d+)\D*""")
        operator fun invoke(range: String) = rangeRe.find(range)?.run {
            groupValues.takeLast(3).map(String::toULong).let { (src, dst, len) -> Range(src, dst, len) }
        }
    }
}