package day05

import day05.objects.*
import java.io.File

private fun List<Range>.rangeMap(value: ULong) = this.firstNotNullOfOrNull { it[value] } ?: value

fun main() {
    val seeds = ArrayList<ULong>()
    val mappings = HashMap<Mapping, List<Range>>()

    fun Almanac.getMap(value: ULong, final: Almanac): ULong =
        mappings.keys.first { this == it.source }.run {mappings[this]!!.rangeMap(value).let {
            when (destination) {
                final -> it
                else -> destination.getMap(it, final) } } }


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
    seeds.minOf { Almanac.seed.getMap(it, Almanac.location) }.also(::println)
}