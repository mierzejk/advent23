package day20.objects

enum class PulseType {
    Low,
    High
}

abstract class Pulse(val type: PulseType, val from: Module, val to: Module) {
    init { counter[type] = counter[type]!! + 1UL }

    fun send() = to.handle(this)

    companion object {
        val counter = mutableMapOf (
            PulseType.Low to 0UL,
            PulseType.High to 0UL
        )
    }
}

class LowPulse(from: Module, to: Module) : Pulse(PulseType.Low, from, to)

class HighPulse(from: Module, to: Module) : Pulse(PulseType.High, from, to)