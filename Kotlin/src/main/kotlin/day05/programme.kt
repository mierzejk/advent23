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
    var minLocation = ULong.MAX_VALUE

    var timeInMillis = measureTimeMillis {
        for (i in 0UL..ULong.MAX_VALUE)
            if (allSeeds.any { Almanac.location.getReverseMap(i, Almanac.seed) in it }) {
                minLocation = i
                break
            }
    }
    println("Part II sequential [${timeInMillis / 1000.0} sec]: $minLocation")
    minLocation = ULong.MAX_VALUE

    fun<T> compute(n: ULong, step: ULong, lock: (action: () -> Unit) -> T) {
        var i = n
        while (i < minLocation) {
            if (allSeeds.any { Almanac.location.getReverseMap(i, Almanac.seed) in it }) {
                lock {
                    if (i < minLocation)
                        minLocation = i
                }
            }
            i += step
        }
    }

    fun parallel() {
        val mutex = ReentrantLock()
        (0UL..3UL).map { n ->
            thread {
                compute(n, 4UL, mutex::withLock)
            }
        }.forEach(Thread::join)
    }

    timeInMillis = measureTimeMillis {
        parallel()
    }
    println("Part II parallel [${timeInMillis / 1000.0} sec]: $minLocation")
    minLocation = ULong.MAX_VALUE

    suspend fun coroutines() {
        val mutex = Mutex()
        coroutineScope {
            (0UL..3UL).map { n ->
                async {
                    compute(n, 4UL) { launch { mutex.withLock { it() } } }
                }
            }.awaitAll()
        }
    }

    timeInMillis = measureTimeMillis {
        runBlocking(Dispatchers.Default) { coroutines() }
    }
    println("Part II coroutines [${timeInMillis / 1000.0} sec]: $minLocation")
}