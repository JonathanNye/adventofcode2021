import java.io.File

val measurements = File("Day1Input.txt")
    .readLines()
    .map { it.toInt() }

// Part one
measurements
    .zipWithNext { a, b -> b > a }
    .count { it }
    .apply(::println)

// Part two
measurements
    .windowed(size = 3, step = 1, partialWindows = false)
    .map { it.sum() }
    .zipWithNext { a, b -> b > a }
    .count { it }
    .apply(::println)