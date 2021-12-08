import Day8.Segment.*
import java.io.File

val signalsToOutputs = File("Day8Input.txt").readLines()
    .map {
        val splitLine = it.split(" | ")
        splitLine[0].split(" ") to splitLine[1].split(" ")
    }
// Part one
val definiteLengths = listOf(2, 3, 4, 7)
signalsToOutputs.sumOf { (_, outputs) ->
    outputs.count { it.length in definiteLengths }
}.run(::println)

// Part two
enum class Segment {
    A, B, C, D, E, F, G;

    companion object {
        fun fromChar(char: Char): Segment = when (char) {
            'a' -> A
            'b' -> B
            'c' -> C
            'd' -> D
            'e' -> E
            'f' -> F
            'g' -> G
            else -> throw IllegalArgumentException("Can't make Segment from $char")
        }
    }
}

fun digitFromSet(segments: Set<Segment>): Char = when (segments) {
    setOf(A, B, C, E, F, G) -> '0'
    setOf(C, F) -> '1'
    setOf(A, C, D, E, G) -> '2'
    setOf(A, C, D, F, G) -> '3'
    setOf(B, C, D, F) -> '4'
    setOf(A, B, D, F, G) -> '5'
    setOf(A, B, D, E, F, G) -> '6'
    setOf(A, C, F) -> '7'
    setOf(A, B, C, D, E, F, G) -> '8'
    setOf(A, B, C, D, F, G) -> '9'
    else -> throw IllegalArgumentException("Can't make digit from segments $segments")
}

val segmentSignalsToOutputs = signalsToOutputs.map { signalsToOutput ->
    val signals = signalsToOutput.first.map { it.map { char -> Segment.fromChar(char) }.toSet() }.toSet()
    val output = signalsToOutput.second.map {
        it.map { char -> Segment.fromChar(char) }.toSet()
    }
    signals to output
}

/*
  aaaa
 b    c
 b    c
  dddd
 e    f
 e    f
  gggg
*/
fun makeDecoder(signals: Set<Set<Segment>>): (Segment) -> Segment {
    fun <T> Set<T>.remainder(): T = if (this.size != 1) {
        // Fail faster than something like first() to make debugging easier
        throw Exception("Expected exactly one element in $this")
    } else {
        this.first()
    }

    val oneSet = signals.first { it.size == 2 }
    val sevenSet = signals.first { it.size == 3 }
    val fourSet = signals.first { it.size == 4 }
    val eightSet = signals.first { it.size == 7 }

    val zeroSixNine = signals.filter { it.size == 6 }

    val encodedA = (sevenSet - oneSet).remainder()
    val encodedBFG = zeroSixNine.reduce { acc, next -> acc.intersect(next) } - encodedA

    val encodedF = encodedBFG.intersect(oneSet).remainder()
    val encodedC = (oneSet - encodedF).remainder()
    val encodedG = (encodedBFG - fourSet).remainder()
    val encodedB = (encodedBFG - encodedF - encodedG).remainder()
    val encodedD = (fourSet - encodedB - encodedC - encodedF).remainder()
    val encodedE = (eightSet - encodedA - encodedB - encodedC - encodedD - encodedF - encodedG).remainder()

    if (setOf(encodedA, encodedB, encodedC, encodedD, encodedE, encodedF, encodedG).size != 7) {
        throw Error("Non-unique decoded value")
    }

    return { input ->
        when (input) {
            encodedA -> A
            encodedB -> B
            encodedC -> C
            encodedD -> D
            encodedE -> E
            encodedF -> F
            encodedG -> G
            else -> throw Error()
        }
    }
}

segmentSignalsToOutputs.sumOf { (signalSet, outputs) ->
    val decoder = makeDecoder(signalSet)
    outputs.map { encodedSet ->
        encodedSet.map { decoder(it) }.toSet()
    }.map { decodedSet ->
        digitFromSet(decodedSet)
    }.joinToString(separator = "").toInt()
}

