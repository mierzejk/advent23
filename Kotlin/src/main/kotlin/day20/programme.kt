package day20

import java.io.File
import java.util.*

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

    internal open fun handle(pulse: Pulse) { }

    abstract fun getOutput(): List<Pulse>

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
    private var output = emptyList<Pulse>()

    abstract fun handleAndSet(pulse: Pulse): List<Pulse>

    override fun handle(pulse: Pulse) {
        output = handleAndSet(pulse)
    }

    override fun getOutput() = output.also { output = emptyList() }
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
    private var triggered = false

    fun setSource(modules: Collection<Module>) {
        source = modules.associate { it.name to PulseType.Low }.toMutableMap()
    }

    override fun handle(pulse: Pulse) {
        source[pulse.from.name] = pulse.type
        triggered = true
    }

    override fun getOutput() = (
        if (triggered) {
            when (source.values.all { PulseType.High == it }) {
                true -> allLow()
                false -> allHigh()
            }
        }
        else {
            emptyList()
        } ).also { triggered = false }
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
    val input = File("src/main/resources/test.txt").readLines()
    // Read the list
    val moduleList = buildList {
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
    val broadcaster = modules.single { it is Broadcast }

    // Connect sinks
    for ((module, sinkList) in moduleList) {
        module.sink = sinkList.map(moduleMap::terminated)
    }

    // Connect sources
    modules.filterIsInstance<Conjunction>().forEach {
        it.source = moduleList.filter { (_, sink) -> it.name in sink }
            .associate { (module, _) -> module.name to PulseType.Low }.toMutableMap()
    }

    repeat(1000) {
        var pending = listOf<Pulse>(LowPulse(Module.Button, broadcaster))
        while (pending.isNotEmpty()) {
            pending.forEach(Pulse::send)
            pending = modules.flatMap(Module::getOutput)
        }
    }

    val result = counter.values.reduce(ULong::times)
    println("$counter → $result")
}
