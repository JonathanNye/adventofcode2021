import java.io.File

val input = File("Day11Input.txt").readLines()

val width = input.first().length
val height = input.size

class Cell(var value: Int, var flashed: Boolean = false) {
    val canFlash: Boolean get() = value > 9 && !flashed
}

val grid = input
    .joinToString(separator = "")
    .map { Cell(it.digitToInt()) }
    .let { GridList(it, width, height) }

var flashCount = 0

fun step(grid: GridList<Cell>) {
    grid.forEach { cell -> cell.value += 1 }

    while (grid.any { it.canFlash }) {
        val index = grid.indexOfFirst { it.canFlash }
        val cell = grid[index]
        flashCount += 1
        for (neighbor in grid.neighbors(index, includeDiagonals = true)) {
            neighbor.value += 1
        }
        cell.flashed = true
    }

    grid.forEach { cell ->
        if (cell.value > 9) {
            cell.value = 0
        }
        cell.flashed = false
    }
}

// Part 1
repeat((0 until 100).count()) {
    step(grid)
}
println("Total flashes: $flashCount")

// Part 2
val grid2 = input
    .joinToString(separator = "")
    .map { Cell(it.digitToInt()) }
    .let { GridList(it, width, height) }

var stepsToSync = 0
while (true) {
    step(grid2)
    stepsToSync += 1
    if (grid2.all { cell -> cell.value == 0 }) {
        break
    }
}
println("First sync step: $stepsToSync")