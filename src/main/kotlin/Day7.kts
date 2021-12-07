import java.io.File

val input = File("Day7Input.txt").readLines().first()

val offsetToQuantity = input
    .split(",")
    .map { it.toInt() }
    .groupingBy { it }
    .eachCount()

val startOffset = offsetToQuantity.keys.minOrNull() ?: throw Error("Couldn't get a minimum offset")
val endOffset = offsetToQuantity.keys.maxOrNull() ?: throw Error("Couldn't get a maximum offset")

// Part one
(startOffset .. endOffset).map { targetOffset ->
    offsetToQuantity.entries.sumOf { entry -> entry.value * kotlin.math.abs(entry.key - targetOffset) }
}.minOrNull().apply(::println)

// Part two
val costLookup = (0 .. (endOffset - startOffset)).runningReduce(Int::plus)

(startOffset .. endOffset).map { targetOffset ->
    offsetToQuantity.entries.sumOf { entry -> entry.value * costLookup[kotlin.math.abs(entry.key - targetOffset)] }
}.minOrNull().apply(::println)
