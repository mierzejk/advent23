package day05.objects

import kotlin.math.max
import kotlin.math.min

internal class MappingRange(val source: Almanac, val destination: Almanac, ranges: Collection<Range>) {
    private val ranges: List<Range>

    data class Range(val start: Long, val end: Long, val delta: Long) {
        constructor(range: day05.objects.Range):
                this(range.sourceRange.first.toLong(), range.sourceRange.last.toLong(), range.delta.toLong())

        fun map(input: LongRange) = delta + max(start, input.first)..min(end, input.last) + delta
    }

    constructor(mapping: Mapping, ranges: Collection<day05.objects.Range>):
            this(mapping.source, mapping.destination, ranges.map(::Range))

    init {
        if (ranges.isEmpty())
            this.ranges = listOf(Range(0L, 0L, 0L))
        else {
            val sorted = ranges.sortedBy(Range::start)
            this.ranges = sequence {
                var current = sorted.first()
                if (0L < current.start)
                    yield(Range(0L, current.start - 1L, 0L))

                for (next in sorted.drop(1)) {
                    assert(current.end < next.start)
                    yield(current)
                    if (current.end + 1L != next.start)
                        yield(Range(current.end + 1L, next.start - 1L, 0L))

                    current = next
                }

                yield(current)
                if (current.end < Long.MAX_VALUE)
                    yield(Range(current.end + 1L, Long.MAX_VALUE, 0L))
            }.toList()
        }
    }

    fun mapRanges(input: Collection<LongRange>): Collection<LongRange> {
        val mapped = ranges.flatMap { input.map(it::map) }.filterNot(LongRange::isEmpty).sortedBy(LongRange::last)
            .toMutableList()
        for (i in mapped.size - 1 downTo 1) {
            val current = mapped[i]
            val previous = mapped[i-1]
            if (current.first <= previous.last) {
                mapped[i-1] = min(current.first, previous.first)..current.last
                mapped.removeAt(i)
            }
        }

        return mapped
    }
}