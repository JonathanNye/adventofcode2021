import java.io.File

data class Node<T>(val id: Int, val value: T) {
    // Don't include neighbors in the primary constructor or generated methods
    // will overflow due to circular references
    var neighbors: Set<Node<T>> = emptySet()
}

val inputLines = File("Day9Input.txt")
    .readLines()

val width = inputLines.first().length
val height = inputLines.size

val nodes = inputLines
    .joinToString(separator = "")
    .mapIndexed { idx, char -> Node(idx, char.digitToInt()) }

// Build out graph edges
for (x in (0 until width)) {
    for (y in (0 until height)) {
        val offset = y * width + x
        val node = nodes[offset]
        node.neighbors = setOfNotNull(
            if (x == 0) null else nodes.elementAtOrNull(offset - 1), // left
            if (x == width - 1) null else nodes.elementAtOrNull(offset + 1), // right
            nodes.elementAtOrNull(offset - width), // above
            nodes.elementAtOrNull(offset + width) // below
        )
    }
}

val localMinima = nodes
    .filter { it.neighbors.all { neighbor -> neighbor.value > it.value } }

// Part 1
localMinima.sumOf { it.value + 1 }
    .run(::println)

// Part 2
fun Node<Int>.findBasin(resultSet: MutableSet<Node<Int>>) {
    resultSet.add(this)
    neighbors
        .filter { it.value < 9 && it !in resultSet }
        .forEach { it.findBasin(resultSet) }
}

localMinima
    .map {
        val result = HashSet<Node<Int>>()
        it.findBasin(result)
        result.size
    }
    .sortedDescending()
    .take(3)
    .reduce(Int::times)
