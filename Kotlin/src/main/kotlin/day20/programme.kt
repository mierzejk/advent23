package day20

import day20.objects.*
import java.io.File
import java.util.*
import LCM

fun Map<String, Module>.terminated(key: String) = when (key in this) {
    true -> this[key]!!
    false -> Module.Terminator
}

val lineRe = Regex("""(?<typeName>\S+)\s+->\s+(?<sink>.*)""")

fun main() {
    val rx = object: Module("rx") {
        var hit = false
        override fun getOutput(): List<Pulse> {
            if (PulseType.Low in input.map { it.type })
                hit = true

            return emptyList()
        }
    }

    val input = File("src/main/resources/day_20_input.txt").readLines()
    // Read the list
    val moduleList = buildList {
        rx to emptyList<String>()
        input.map(lineRe::matchEntire).forEach {
            val (typeName, sink) = it!!.destructured
            add (when (typeName[0]) {
                'b' -> Broadcast(typeName)
                '%' -> FlipFlop(typeName.drop(1))
                '&' -> Conjunction(typeName.drop(1))
                else -> throw IllegalArgumentException(typeName)
            } to sink.split(", "))
        }
    }
    val modules = moduleList.map(Pair<Module, *>::first)
    val moduleMap = modules.associateBy(Module::name)

    // Connect sinks
    for ((module, sinkList) in moduleList) {
        module.sink = sinkList.map(moduleMap::terminated)
    }

    // Connect sources
    modules.filterIsInstance<Conjunction>().forEach {
        it.source = moduleList.filter { (_, sink) -> it.name in sink }
            .associate { (module, _) -> module.name to PulseType.Low }.toMutableMap()
    }

    // Part I
//    repeat(1000) { modules.press() }
//    val result = Pulse.counter.values.reduce(ULong::times)
//    println("${Pulse.counter} → $result")

    // Part II
    var clicks = 0UL
    val bq = moduleMap["bq"]!!
    val bqIncoming = modules.filter { it.sink.contains(bq) }.map { it.name }.toMutableSet()
    val bqHighClicks = mutableMapOf<String, ULong>()
    fun verbose(pending: List<Pulse>) {
        pending.filter { PulseType.High == it.type && it.to === bq && it.from.name !in bqHighClicks }.forEach {
            bqHighClicks[it.from.name] = clicks
            bqIncoming.remove(it.from.name)
        }
        if (bqIncoming.isEmpty())
            rx.hit = true
    }

    while (!rx.hit) {
        clicks += 1UL
        modules.press(::verbose)
    }
    println(LCM(bqHighClicks.values))
}

fun List<Module>.press(action: ((List<Pulse>) -> Unit)? = null) {
    var pending = listOf<Pulse>(LowPulse(Module.Button, single { it is Broadcast }))
    while (pending.isNotEmpty()) {
        action?.invoke(pending)
        pending.forEach(Pulse::send)
        pending = flatMap(Module::sendOutput)
    }
}
