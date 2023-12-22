package day20

import java.io.File
import java.util.*
import LCM

enum class PulseType {
    Low,
    High
}

val counter = mutableMapOf (
    PulseType.Low to 0UL,
    PulseType.High to 0UL
)

abstract class Pulse(val type: PulseType, val from: Module, val to: Module) {
    init { counter[type] = counter[type]!! + 1UL }

    fun send() = to.handle(this)
}

class LowPulse(from: Module, to: Module) : Pulse(PulseType.Low, from, to)

class HighPulse(from: Module, to: Module) : Pulse(PulseType.High, from, to)

abstract class Module(val name: String) {
    lateinit var sink: Collection<Module>
    protected val input = mutableListOf<Pulse>()

    internal fun handle(pulse: Pulse) = input.add(pulse)

    protected abstract fun getOutput(): List<Pulse>

    internal fun sendOutput() = getOutput().also { input.clear() }

    protected fun allLow() = sink.map { LowPulse(this, it) }
    protected fun allHigh() = sink.map { HighPulse(this, it) }

    companion object {
        val Terminator = object: Module("output") {
            override fun getOutput() = emptyList<Pulse>()
        }
        val Button = object: Module("button") {
            override fun getOutput(): List<Pulse> = throw NotImplementedError("By design")
        }
    }
}

abstract class StaticOutput(name: String): Module(name) {
    abstract fun handleAndSet(pulse: Pulse): List<Pulse>

    override fun getOutput() = input.flatMap(::handleAndSet)
}

class FlipFlop(name: String): StaticOutput(name) {
    private var on = false

    override fun handleAndSet(pulse: Pulse): List<Pulse> {
        if (PulseType.High == pulse.type)
            return emptyList()
        else {
            on = !on
            return when (on) {
                true -> allHigh()
                false -> allLow()
            }
        }
    }
}

class Conjunction(name: String): Module(name) {
    lateinit var source: MutableMap<String, PulseType>

    fun setSource(modules: Collection<Module>) {
        source = modules.associate { it.name to PulseType.Low }.toMutableMap()
    }

    override fun getOutput() = buildList { input.forEach { pulse ->
        source[pulse.from.name] = pulse.type
        when (source.values.all { PulseType.High == it }) {
                true -> allLow()
                false -> allHigh()
        }.run(::addAll)
    } }
}

class Broadcast(name: String): StaticOutput(name) {
    override fun handleAndSet(pulse: Pulse) = when(pulse.type) {
        PulseType.Low -> allLow()
        PulseType.High -> allHigh()
    }
}

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
//    val result = counter.values.reduce(ULong::times)
//    println("$counter → $result")

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
        if (0UL == clicks % 10000UL)
            println(clicks)
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
