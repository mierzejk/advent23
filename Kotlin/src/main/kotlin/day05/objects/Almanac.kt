package day05.objects

@Suppress("EnumEntryName")
internal enum class Almanac {
    seed,
    soil,
    fertilizer,
    water,
    light,
    temperature,
    humidity,
    location;

    companion object {
        operator fun get(name: String) = enumValueOf<Almanac>(name.lowercase())
    }
}