import java.io.File

val lines = File("Day14Input.txt").readLines()

val template = lines[0]
val rules = lines
    .drop(2)
    .associate { line ->
        val split = line.split(" -> ")
        (split[0][0] to split[0][1]) to split[1][0]
    }

// Part one
fun String.applyInsertionRules(rules: Map<Pair<Char, Char>, Char>): String =
    this.zipWithNext()
        .flatMap { pair ->
            rules[pair]?.let { insertion ->
                "${pair.first}$insertion".toList()
            } ?: throw Error("No rule defined for $pair")
        }.plus(this.last()).joinToString(separator = "")

val polymer = (0 until 10).fold(template) { acc, _ ->
    acc.applyInsertionRules(rules)
}

val charCounts = polymer
    .groupingBy { it }
    .eachCount()
    .values

val mostCommonCount = charCounts.maxOrNull() ?: throw Error("No maximum count")
val leastCommonCount = charCounts.minOrNull() ?: throw Error("No minimum count")

println(mostCommonCount - leastCommonCount)

// Part two
// Can't build a long String for 40 iterations

operator fun <T> Map<T, Long>.plus(other: Map<T, Long>): Map<T, Long> =
    (keys + other.keys).associateWith { key ->
        getOrDefault(key, 0L) + other.getOrDefault(key, 0L)
    }

val initialCounts = template
    .groupingBy { it }
    .eachCount()
    .mapValues { it.value.toLong() }

// This takes a long time if we don't memoize.
val memoizedResults: MutableMap<Pair<Pair<Char, Char>, Int>, Map<Char, Long>> = mutableMapOf()

fun countInsertions(pair: Pair<Char, Char>, depth: Int): Map<Char, Long> {
    if (depth == 0) return emptyMap()
    val insertion = rules[pair] ?: throw Error("No rule defined for $pair")
    return memoizedResults[pair to depth] ?: run {
        val result = mapOf(insertion to 1L) +
            countInsertions(pair.first to insertion, depth - 1) +
            countInsertions(insertion to pair.second, depth - 1)
        memoizedResults[pair to depth] = result
        result
    }
}

val charCounts2 = template
    .zipWithNext()
    .fold(emptyMap<Char, Long>()) { acc, pair ->
        acc + countInsertions(pair, 40)
    } + initialCounts

val mostCommonCount2 = charCounts2.values.maxOrNull() ?: throw Error("No maximum count")
val leastCommonCount2 = charCounts2.values.minOrNull() ?: throw Error("No minimum count")
println(mostCommonCount2 - leastCommonCount2)
