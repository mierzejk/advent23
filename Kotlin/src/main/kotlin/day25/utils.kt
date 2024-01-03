package day25

internal fun Iterable<String>.cs() = this.sorted().joinToString(",")