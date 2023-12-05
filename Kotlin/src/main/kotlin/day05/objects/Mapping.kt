package day05.objects

internal data class Mapping(val source: Almanac, val destination: Almanac) {
    companion object {
        private val mappingRe = Regex("""^(?<source>\w+)-to-(?<destination>\w+)""", RegexOption.IGNORE_CASE)
        operator fun invoke(name: String) = mappingRe.find(name)?.run {
            groupValues.takeLast(2).map(Almanac.Companion::get).let { (src, dst) -> Mapping(src, dst)  }
        }
    }
}