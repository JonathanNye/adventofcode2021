import java.io.File
import kotlin.math.max
import kotlin.math.min

val input = File("Day5Input.txt").readLines()
val lineRegex = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()

data class Point(val x: Int, val y: Int)

class Segment(private val start: Point, private val end: Point) {
    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(Point(x1, y1), Point(x2, y2))

    fun coveredPoints(includeDiagonals: Boolean): List<Point> = when {
        start.x == end.x -> // vertical
            (min(start.y, end.y)..max(start.y, end.y))
                .map { Point(start.x, it) }
        start.y == end.y -> // horizontal
            (min(start.x, end.x)..max(start.x, end.x))
                .map { Point(it, start.y) }
        includeDiagonals -> {
            // Assume slope is 1 or -1...
            val leftPoint = if (start.x < end.x) start else end
            val rightPoint = if (leftPoint == start) end else start
            if (leftPoint.y < rightPoint.y) {
                // Slope = 1
                (0..(rightPoint.x - leftPoint.x)).map { offset ->
                    Point(leftPoint.x + offset, leftPoint.y + offset)
                }
            } else {
                // Slope = -1
                (0..(rightPoint.x - leftPoint.x)).map { offset ->
                    Point(leftPoint.x + offset, leftPoint.y - offset)
                }
            }
        }
        else -> emptyList() // Skip unsupported segment types
    }
}

val segments = input.mapIndexed { index, line ->
    val groups = lineRegex
        .matchEntire(line)
        ?.groupValues
        ?.drop(1)
        ?.map { it.toInt() }
        ?: throw Error("Couldn't parse line $index: $line")
    Segment(groups[0], groups[1], groups[2], groups[3])
}

// Part one
segments.flatMap { it.coveredPoints(false) }
    .groupingBy { it }
    .eachCount()
    .count { it.value >= 2 }
    .apply(::println)

// Part two
segments.flatMap { it.coveredPoints(true) }
    .groupingBy { it }
    .eachCount()
    .count { it.value >= 2 }
    .apply(::println)
