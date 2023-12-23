package day20.objects

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