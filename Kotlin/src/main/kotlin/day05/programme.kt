package day05

import day05.objects.*
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.system.measureTimeMillis

private fun List<Range>.rangeMap(value: ULong) = this.firstNotNullOfOrNull { it[value] } ?: value
private fun List<Range>.reverseRangeMap(value: ULong) = this.firstNotNullOfOrNull { it.reverse(value) } ?: value

@Suppress("DuplicatedCode")
fun main() {
    val seeds = ArrayList<ULong>()
    val mappings = HashMap<Mapping, List<Range>>()

    tailrec fun Almanac.getMap(value: ULong, final: Almanac): ULong {
        with (mappings.keys.first { this == it.source }.let { object {
            val output = mappings[it]!!.rangeMap(value)
            val dst = it.destination
        } }) {
            return if (dst == final) output else dst.getMap(output, final)
        }
    }

    fun<R> List<ULong>.getLowest(block: (ULong) -> R) = this.minOf { Almanac.seed.getMap(it, Almanac.location) }.let(block)

    File("src/main/resources/day_5_input.txt").useLines { file -> file.iterator().run {
        Regex("""\d+""").findAll(next()).mapTo(seeds) { it.value.toULong() }

        var mapping: Mapping?
        var ranges = ArrayList<Range>()
        do mapping = Mapping(next()) while (null == mapping)
        mappings[mapping] = ranges

        for (line in this) {
            mapping = Mapping(line)?.also { ranges = ArrayList(); mappings[it] = ranges }
                ?: Range(line)?.let(ranges::add)?.let { mapping }
            }
        } }

    // Part I
    seeds.getLowest { println("Part I: $it") }

    // Part II
    val mappingRanges = mappings.map { (mapping, ranges) ->
        MappingRange(mapping, ranges) }.associateBy(MappingRange::source)

    tailrec fun Almanac.getReverseMap(value: ULong, final: Almanac): ULong {
        with (mappings.keys.first { this == it.destination }.let { object {
            val output = mappings[it]!!.reverseRangeMap(value)
            val src = it.source
        } }) {
            return if (src == final) output else src.getReverseMap(output, final)
        }
    }

    val allSeeds = (seeds.filterIndexed { i, _ -> 0 == i%2 } zip seeds.filterIndexed { i, _ -> 1 == i%2 })
        .map { (a, b) -> a..<a+b }
    val threadsNumber = 4UL

    fun measure(type: String, func: () -> ULong) {
        var result: ULong
        println("Part II $type [${measureTimeMillis { result = func() } / 1000.0} sec]: $result")
    }

    tailrec fun rangeMap(almanac: Almanac, input: Collection<LongRange>): Collection<LongRange> {
        with(mappingRanges[almanac]) {
            return if (null == this) input else rangeMap(destination, mapRanges(input))
        }
    }

    val allSeedsLong = allSeeds.map{ LongRange(it.first.toLong(), it.last.toLong()) }
    measure("range map") {
        rangeMap(Almanac.seed, allSeedsLong).first().first.toULong()
    }

    measure("sequential") {
        (0UL .. ULong.MAX_VALUE).first { i ->
            allSeeds.any { Almanac.location.getReverseMap(i, Almanac.seed) in it } }
        }

    fun parallel() : ULong {
        var minLocation = ULong.MAX_VALUE
        val mutex = ReentrantLock()
        0UL.rangeUntil(threadsNumber).map { n ->
            thread {
                var i = n
                while (i < minLocation) {
                    if (allSeeds.any { Almanac.location.getReverseMap(i, Almanac.seed) in it })
                        mutex.withLock {
                            if (i < minLocation)
                                minLocation = i
                        }
                    i += threadsNumber
                }
            }
        }.forEach(Thread::join)

        return minLocation
    }

    measure("parallel threads", ::parallel)

    suspend fun coroutines(scope: CoroutineScope): ULong {
        var minLocation = ULong.MAX_VALUE
        val mutex = Mutex()
        0UL.rangeUntil(threadsNumber).map { n -> scope.launch {
            var i = n
            while (i < minLocation) {
                if (allSeeds.any { Almanac.location.getReverseMap(i, Almanac.seed) in it })
                    mutex.withLock {
                        if (i < minLocation)
                            minLocation = i
                    }
                i += threadsNumber
            } }
        }.joinAll()

        return minLocation
    }

    measure("coroutines") { runBlocking(Dispatchers.Default, ::coroutines) }
}