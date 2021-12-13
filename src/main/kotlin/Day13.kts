import java.io.File

val lines = File("Day13Input.txt").readLines()

enum class Axis { X, Y }

data class Dot(val x: Int, val y: Int) {
    fun foldedAt(axis: Axis, foldAt: Int): Dot = when (axis) {
        Axis.X -> {
            // fold right to left
            if (x < foldAt) {
                this
            } else {
                val distance = x - foldAt
                Dot(x - distance - distance, y)
            }
        }
        Axis.Y -> {
            // fold bottom to top
            if (y < foldAt) {
                this
            } else {
                val distance = y - foldAt
                Dot(x, y - distance - distance)
            }
        }
    }
}

val initialDots = lines
    .filter { it.getOrNull(0)?.isDigit() ?: false }
    .map {
        val split = it.split(",")
        Dot(split[0].toInt(), split[1].toInt())
    }.toSet()

val folds = lines
    .filter { it.startsWith("fold") }
    .map { it.replace("fold along ", "") }
    .map {
        val split = it.split("=")
        val axis = when (split[0][0]) {
            'x' -> Axis.X
            'y' -> Axis.Y
            else -> throw Error("Unknown axis ${split[0][0]}")
        }
        axis to split[1].toInt()
    }

fun Set<Dot>.foldedAt(axis: Axis, foldAt: Int): Set<Dot> = map {
    it.foldedAt(axis, foldAt)
}.toSet()

// Part one
val dotsAfterOneFold = folds.first().let { (axis, foldAt) ->
    initialDots.foldedAt(axis, foldAt).size
}
println("$dotsAfterOneFold dots after one fold")

// Part two
// heard you like folds
val dotsAfterAllFolds = folds.fold(initialDots) { dots, (axis, foldAt) ->
    dots.foldedAt(axis, foldAt)
}
val width = dotsAfterAllFolds.maxOf { it.x }
val height = dotsAfterAllFolds.maxOf { it.y }
for (y in (0..height)) {
    val line = (0..width).map { x ->
        // 9608 is a unicode "FULL BLOCK"
        if (dotsAfterAllFolds.contains(Dot(x, y))) 9608.toChar() else ' '
    }.joinToString(separator = "")
    println(line)
}
