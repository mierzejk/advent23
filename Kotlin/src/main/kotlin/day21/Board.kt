package day21

@Suppress("MemberVisibilityCanBePrivate")
internal open class Board<T>(val array: List<T>, val stride: Int, height: Int? = null) {
    private val lastButStride = array.lastIndex - stride
    private val adjacentIndices = listOf(::indexLeft::invoke, ::indexUp::invoke, ::indexRight::invoke, ::indexDown::invoke)
    val height = height ?: (array.size / stride)

    init { assert (this.height * stride == array.size) }

    operator fun get(index: Int) = array[index]

    fun spaceLeft(index: Int) = 0 != index % stride
    fun indexLeft(index: Int) = if (spaceLeft(index)) index - 1 else null
    fun distanceLeft(index: Int) = index % stride
    fun elementLeft(index: Int, default: T? = null) = indexLeft(index)?.let(array::get) ?: default
    fun spaceUp(index: Int) = stride <= index
    fun indexUp(index: Int) = if (spaceUp(index)) index - stride else null
    fun distanceUp(index: Int) = index / stride
    fun elementUp(index: Int, default: T? = null) = indexUp(index)?.let(array::get) ?: default
    fun spaceRight(index: Int) = 0 != (index + 1) % stride
    fun indexRight(index: Int) = if (spaceRight(index)) index + 1 else null
    fun distanceRight(index: Int) = stride - 1 - distanceLeft(index)
    fun elementRight(index: Int, default: T? = null) = indexRight(index)?.let(array::get) ?: default
    fun spaceDown(index: Int) = index <= lastButStride
    fun indexDown(index: Int) = if (spaceDown(index)) index + stride else null
    fun distanceDown(index: Int) = height - 1 - distanceUp(index)
    fun elementDown(index: Int, default: T? = null) = indexDown(index)?.let(array::get) ?: default

    open fun getAdjacent(index: Int) = adjacentIndices.mapNotNull { it(index) }

    override fun toString() = array.map(Any?::toString).chunked(stride).joinToString("\n") { it.joinToString(" ") }
}

@Suppress("MemberVisibilityCanBePrivate")
internal open class MutableBoard<T>(protected val mutableArray: MutableList<T>, stride: Int, height: Int? = null)
    : Board<T>(mutableArray, stride, height) {
        operator fun set(index: Int, value: T) { mutableArray[index] = value }
    }