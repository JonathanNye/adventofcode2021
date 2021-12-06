import java.io.File

val input = File("Day6Input.txt").readLines().first()

val NEW_FISH_OFFSET = 8
val RESET_OFFSET = 6

val initialDistribution = input
    .split(",")
    .map { it.toLong() }
    .groupingBy { it }
    .eachCount()
    .let { counts ->
        LongArray(NEW_FISH_OFFSET + 1) { idx ->
            counts[idx.toLong()]?.toLong() ?: 0L
        }
    }

// Does more intermediate allocations than optimal but it's cute
fun LongArray.rotateLeft(n: Int) = (drop(n) + take(n)).toLongArray()

fun step(distribution: LongArray): LongArray {
    val toReset = distribution[0]
    val next = distribution.rotateLeft(1)
    next[RESET_OFFSET] += toReset
    return next
}

// Part one
(0 until 80).fold(initialDistribution) { distribution, _ ->
    step(distribution)
}.sum().apply(::println)


// Part two
(0 until 256).fold(initialDistribution) { distribution, _ ->
    step(distribution)
}.sum().apply(::println)
