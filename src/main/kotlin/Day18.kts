import java.io.File
import kotlin.math.ceil
import kotlin.math.floor

sealed class RegularOrFish(var parent: FishNumber? = null) {
    abstract fun magnitude(): Long
    abstract fun leftFirstSearch(predicate: (RegularOrFish) -> Boolean): RegularOrFish?
    abstract fun rightFirstSearch(predicate: (RegularOrFish) -> Boolean): RegularOrFish?
}

class RegularNumber(var value: Long) : RegularOrFish() {
    override fun magnitude() = value

    override fun leftFirstSearch(predicate: (RegularOrFish) -> Boolean): RegularOrFish? =
        if (predicate(this)) this else null

    override fun rightFirstSearch(predicate: (RegularOrFish) -> Boolean): RegularOrFish? =
        if (predicate(this)) this else null

    override fun toString(): String = value.toString()
}

class FishNumber(
    left: RegularOrFish,
    right: RegularOrFish,
) : RegularOrFish() {

    init {
        left.parent = this
        right.parent = this
    }

    private var left: RegularOrFish = left
        set(value) {
            value.parent = this
            field = value
        }
    private var right: RegularOrFish = right
        set(value) {
            value.parent = this
            field = value
        }

    fun replace(old: RegularOrFish, new: RegularOrFish) {
        if (old === left) {
            this.left = new
        } else if (old === right) {
            this.right = new
        } else {
            error("Trying to replace $old in pair where it wasn't present")
        }
    }

    fun depth(): Int {
        var depth = 0
        var curr = this
        while (curr.parent != null) {
            depth += 1
            curr = curr.parent!!
        }
        return depth
    }

    override fun toString(): String = "[${left},${right}]"

    override fun magnitude(): Long = 3 * left.magnitude() + 2 * right.magnitude()

    operator fun plus(other: FishNumber): FishNumber = FishNumber(this, other)

    override fun leftFirstSearch(predicate: (RegularOrFish) -> Boolean): RegularOrFish? =
        left.leftFirstSearch(predicate)
            ?: right.leftFirstSearch(predicate)
            ?: if (predicate(this)) this else null

    override fun rightFirstSearch(predicate: (RegularOrFish) -> Boolean): RegularOrFish? =
        right.rightFirstSearch(predicate)
            ?: left.rightFirstSearch(predicate)
            ?: if (predicate(this)) this else null

    fun reduce(): FishNumber {
        while (true) {
            if (explode()) continue
            if (split()) continue
            break
        }
        return this
    }

    private fun explode(): Boolean {
        val toExplode = leftFirstSearch {
            it is FishNumber && it.depth() == 4
        } as FishNumber?
        return if (toExplode != null) {
            val left = toExplode.left as RegularNumber
            val right = toExplode.right as RegularNumber
            val parent = toExplode.parent!! // Have to have a parent, we're at depth 4

            val rightNeighbor = rightNumberNeighbor(toExplode)
            if (rightNeighbor != null) {
                rightNeighbor.value += right.value
            }

            val leftNeighbor = leftNumberNeighbor(toExplode)
            if (leftNeighbor != null) {
                leftNeighbor.value += left.value
            }

            parent.replace(toExplode, RegularNumber(0L))
            true
        } else {
            false
        }
    }

    private fun rightNumberNeighbor(target: FishNumber): RegularNumber? {
        var prev = target
        var curr = target.parent
        while (curr != null) {
            if (curr!!.right != prev) {
                return curr!!.right.leftFirstSearch { it is RegularNumber } as RegularNumber
            }
            prev = curr!!
            curr = curr?.parent
        }
        return null
    }

    private fun leftNumberNeighbor(target: FishNumber): RegularNumber? {
        var prev = target
        var curr = target.parent
        while (curr != null) {
            if (curr!!.left != prev) {
                return curr!!.left.rightFirstSearch { it is RegularNumber } as RegularNumber
            }
            prev = curr!!
            curr = curr?.parent
        }
        return null
    }

    private fun split(): Boolean {
        val toSplit = leftFirstSearch {
            it is RegularNumber && it.value >= 10
        } as RegularNumber?
        return if (toSplit != null) {
            val parent = toSplit.parent!! // Numbers are always part of a pair
            val newPair = FishNumber(
                left = RegularNumber(floor(toSplit.value / 2.0).toLong()),
                right = RegularNumber(ceil(toSplit.value / 2.0).toLong())
            )
            parent.replace(toSplit, newPair)
            true
        } else {
            false
        }
    }

}

fun parse(input: Iterator<Char>): RegularOrFish {
    val next = input.next()
    return when {
        next == '[' -> {
            val left = parse(input)
            input.next().let { if (it != ',') error("Expected ',' but got '$it'" ) }
            val right = parse(input)
            input.next().let { if (it != ']') error("Expected ']' but got '$it'") }
            FishNumber(left, right)
        }
        next.isDigit() -> {
            RegularNumber(next.digitToInt().toLong())
        }
        else -> error("Expected '[' or digit but got '$next'")
    }
}

val input = File("Day18Input.txt").readLines()

// Part one
val addends = input.map { parse(it.iterator()) as FishNumber }
addends.reduce { acc, fishNumber -> (acc + fishNumber).reduce() }
    .magnitude()
    .run(::println)

// Part two
fun indexPairPermutations(range: IntRange): Sequence<Pair<Int, Int>> =
    sequence {
        range.forEach { left ->
            range.forEach { right ->
                if (left != right) yield(left to right)
            }
        }
    }

indexPairPermutations(input.indices)
    .map { (leftIdx, rightIdx) ->
        // Reducing is side-effecty, so we rebuild from the input fresh each time
        val left = parse(input[leftIdx].iterator()) as FishNumber
        val right = parse(input[rightIdx].iterator()) as FishNumber
        (left + right).reduce().magnitude()
    }
    .maxOrNull()
    .run(::println)
