import java.io.File
/*
 0  1  2  3  4
 5  6  7  8  9
10 11 12 13 14
15 16 17 18 19
20 21 22 23 24
*/
class BingoCard(private val cells: List<Cell>, private val size: Int = 5) {

    companion object {
        fun fromInputLines(lines: List<String>): BingoCard {
            val cells = lines.flatMap { line ->
                val regex = """^\s*(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s*$""".toRegex()
                regex.matchEntire(line)?.groupValues?.drop(1) ?: throw Error("Didn't match line \"$line\"")
            }.map {
                Cell(it.toInt())
            }
            return BingoCard(cells)
        }
    }

    class Cell(val value: Int, var marked: Boolean = false)

    init {
        if (cells.size != size * size)
            throw IllegalArgumentException("Got cells of size ${cells.size}, expected ${size * size}")
    }

    fun mark(number: Int) {
        cells
            .filter { it.value == number }
            .forEach { it.marked = true }
    }

    fun clear() {
        cells.forEach { it.marked = false }
    }

    fun isAWinner(): Boolean {
        val rowStartOffsets = 0 .. (size * (size - 1)) step size
        rowStartOffsets.forEach { rowStart ->
            val row = cells.subList(rowStart, rowStart + size)
            if (row.all { it.marked }) return true
        }
        val columnStartOffsets = 0 until size
        columnStartOffsets.forEach { columnStart ->
            val column = cells.filterIndexed { index, _ ->
                (index - columnStart) % size == 0
            }
            if (column.all { it.marked }) return true
        }
        return false
    }

    fun score(lastCalledNumber: Int): Int =
        cells.filter { !it.marked }.sumOf { it.value } * lastCalledNumber

}
val inputFile = File("Day4Input.txt").readLines().iterator()

val calledNumbers = inputFile
    .next()
    .split(",")
    .map { it.toInt() }

val cards = mutableListOf<BingoCard>()
while (inputFile.hasNext()) {
    inputFile.next() // skip empty line
    val lines = (0 until 5).map {
        inputFile.next()
    }
    cards.add(BingoCard.fromInputLines(lines))
}

// Part one
for (number in calledNumbers) {
    cards.forEach { card -> card.mark(number) }
    val winner = cards.firstOrNull { it.isAWinner() }
    if (winner != null) {
        println("First winner score: ${winner.score(number)}")
        break
    }
}

// Part two
cards.forEach { it.clear() }

for (number in calledNumbers) {
    cards.forEach { card -> card.mark(number) }
    if (cards.size > 1) {
        cards.removeAll { it.isAWinner() }
    }
    if (cards.size == 1) {
        val lastCard = cards.first()
        if (lastCard.isAWinner()) {
            println("Last winner score: ${lastCard.score(number)}")
            break
        }
    }
}

