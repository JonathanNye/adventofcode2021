import java.io.File
import java.lang.Error

val binaryStrings = File("Day3Input.txt").readLines()
val numBits = binaryStrings.maxOfOrNull { it.length } ?: 0

class BitHistogram(var zeroCount: Int = 0, var oneCount: Int = 0) {
    val mostFrequent: Char?
        get() = when {
            zeroCount == oneCount -> null
            zeroCount > oneCount -> '0'
            else -> '1'
        }
    val leastFrequent: Char?
        get() = when {
            zeroCount == oneCount -> null
            zeroCount > oneCount -> '1'
            else -> '0'
        }
}

fun positionHistogram(input: List<String>, position: Int): BitHistogram {
    val histogram = BitHistogram()
    input.forEach {
        when(it.getOrNull(position)) {
            '0' -> histogram.zeroCount++
            '1' -> histogram.oneCount++
        }
    }
    return histogram
}
// LinkedHashMap preserves insertion order for iteration
val positionHistograms: Array<BitHistogram> = Array(numBits) { position ->
    positionHistogram(binaryStrings, position)
}
val gammaRate = positionHistograms.map { histogram ->
    histogram.mostFrequent ?: '0'
}.joinToString(separator = "").toInt(radix = 2)

val epsilonRate = positionHistograms.map { histogram ->
    histogram.leastFrequent ?: '1'
}.joinToString(separator = "").toInt(radix = 2)

println(gammaRate * epsilonRate)

// Part two
var workingList = binaryStrings.toList()

for (position in 0 until numBits) {
    val histogram = positionHistogram(workingList, position)
    val mostFrequent = histogram.mostFrequent ?: '1'
    workingList = workingList.filter {
        it[position] == mostFrequent
    }
    if (workingList.size == 1) break
}
val oxygenRating = if (workingList.size == 1) {
    workingList.first().toInt(radix = 2)
} else throw Error("Didn't filter down to a single oxygen rating")

workingList = binaryStrings.toList()
for (position in 0 until numBits) {
    val histogram = positionHistogram(workingList, position)
    val leastFrequent = histogram.leastFrequent ?: '0'
    workingList = workingList.filter {
        it[position] == leastFrequent
    }
    if (workingList.size == 1) break
}
val co2rating = if (workingList.size == 1) {
    workingList.first().toInt(radix = 2)
} else throw Error("Didn't filter down to a single CO2 rating")

println(oxygenRating * co2rating)