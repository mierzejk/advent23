import day11.combinations
import java.util.*
import kotlin.collections.HashMap

fun<T> MutableCollection<T>.pop() = with(iterator()) { next().also { remove() } }

fun<T: Comparable<T>> List<T>.bisectDistinct(element: T): List<T> {
    if (isEmpty())
        return listOf(element)

    var index = binarySearch(element)
    if (index < 0)
        index = -index - 1
    else if (0 == get(index).compareTo(element))
        return this

    return buildList {
        addAll(this@bisectDistinct.slice(0..<index))
        add(element)
        addAll(this@bisectDistinct.slice(index..this@bisectDistinct.lastIndex))
    }
}

fun<T> combinations(collection: List<T>, length: Int): List<List<T>> {
    if (collection.size < length || length < 1)
        return emptyList()
    if (1 == length)
        return collection.map(::listOf)

    return buildList { (0..collection.size-length).forEach {
        for (tail in combinations(collection.drop(it + 1), length - 1))
            add(listOf(collection[it]) + tail)
    } }
}

class DefaultMap<K, V>(private val defaultValue: (key: K) -> V): HashMap<K, V>() {
    override fun get(key: K) = super.get(key) ?: defaultValue(key).also { this[key] = it }
}

class DefaultSortedMap<K: Comparable<K>, V>(private val defaultValue: (key: K) -> V): TreeMap<K, V>() {
    override fun get(key: K) = super.get(key) ?: defaultValue(key).also { this[key] = it }
}