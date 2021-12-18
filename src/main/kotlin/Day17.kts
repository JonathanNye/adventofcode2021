import java.io.File
import kotlin.math.abs
import kotlin.math.max

val inputMatcher = Regex("""^target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)$""")

// I could've directly modeled the input myself since it's so tiny but...
val targetArea = File("Day17Input.txt").readText().let { input ->
    inputMatcher
        .matchEntire(input)
        ?.groupValues
        ?.drop(1)
        ?.map { it.toInt() }
        ?.let { groups ->
            groups[0]..groups[1] to groups[2]..groups[3]
        }?: error("Input didn't match expected format")
}

val (xRange, yRange) = targetArea

// Part one
val maxYVel = abs(yRange.first) - 1
val maxHeight = (maxYVel downTo 1).reduce(Int::plus)
println(maxHeight)

// Part two
val minXVel = (1 until xRange.first).first { xVel ->
    (xVel downTo 1).reduce(Int::plus) >= xRange.first
}

val maxXVel = xRange.last
val minYVel = yRange.first

operator fun Pair<IntRange, IntRange>.contains(point: Pair<Int, Int>): Boolean =
    point.first in first && point.second in second

fun willHit(initialXVel: Int, initialYVel: Int, target: Pair<IntRange, IntRange>): Boolean {
    var x = initialXVel; var y = initialYVel
    var xVel = initialXVel; var yVel = initialYVel
    // This wouldn't work if our target were up or left of 0,0
    while(x <= target.first.last && y >= target.second.first) {
        if ((x to y) in target) return true
        xVel = max(0, xVel - 1)
        yVel -= 1
        x += xVel
        y += yVel
    }
    return false
}

// Could count procedurally with a var and nested forEach but hey, Sequences!
(minXVel..maxXVel).asSequence()
    .flatMap { xVel ->
        (minYVel..maxYVel).asSequence().map { yVel ->
            xVel to yVel
        }
    }
    .map { (xVel, yVel) -> if (willHit(xVel, yVel, targetArea)) 1 else 0 }
    .sum()
    .run(::println)