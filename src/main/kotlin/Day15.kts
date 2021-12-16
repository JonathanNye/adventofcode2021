import java.io.File
import java.util.*

data class Node(val id: Int, val cost: Int) : Comparable<Node> {
    var runningCost: Int = Int.MAX_VALUE
    var neighbors: Set<Node> = emptySet()
    override fun compareTo(other: Node): Int {
        return runningCost.compareTo(other.runningCost)
    }
}

val intLines = File("Day15Input.txt").readLines()
    .map { line ->
        line.map { it.digitToInt() }
    }
val width = intLines.first().size
val height = intLines.size
val nodes = intLines
    .flatten()
    .mapIndexed { idx, cost -> Node(id = idx, cost = cost) }

val grid = GridList(nodes, width, height)

// Build out graph
grid.forEachIndexed { index, node ->
    node.neighbors = grid.neighbors(index, includeDiagonals = false).toSet()
}

fun costOfTraversal(start: Node, end: Node, graph: List<Node>): Int {
    //val unvisitedSet = graph.toMutableSet()
    if (start !in graph || end !in graph) { throw Error("start and end must be in graph") }
    graph.forEach { it.runningCost = Int.MAX_VALUE }
    start.runningCost = 0

    // Priority should (hopefully) make it faster to determine the least-cost unvisited node
    val unvisitedSet = PriorityQueue<Node>()
    unvisitedSet.addAll(graph)
    while (unvisitedSet.isNotEmpty()) {
        val current = unvisitedSet.first()
        unvisitedSet.remove(current)

        if (current == end) {
            return current.runningCost
        }
        current.neighbors
            .filter { it in unvisitedSet }
            .forEach { neighbor ->
                val newRunningCost = current.runningCost + neighbor.cost
                if (newRunningCost < neighbor.runningCost) {
                    neighbor.runningCost = newRunningCost
                    // have to remove and re-add neighbor to make the priority queue work
                    unvisitedSet.remove(neighbor)
                    unvisitedSet.add(neighbor)
                }
            }
    }
    throw Error("Couldn't compute cost of reaching end")
}

// Part one
println(
    costOfTraversal(
        start = grid.first(),
        end = grid.last(),
        graph = nodes
    )
)

// Part two
// This takes a few minutes to run on my machine
val bigMapNodes = (0 until 5).flatMap { yGridOffset ->
    intLines.flatMap { line ->
        (0 until 5).flatMap { xGridOffset ->
            line.map {
                val withOffset = it + xGridOffset + yGridOffset
                if (withOffset > 9) {
                    withOffset - 9
                } else {
                    withOffset
                }
            }
        }
    }
}.mapIndexed { idx, cost -> Node(id = idx, cost = cost) }
val bigWidth = width * 5
val bigHeight = height * 5

val bigGrid = GridList(bigMapNodes, bigWidth, bigHeight)

// Build out graph
bigGrid.forEachIndexed { index, node ->
    node.neighbors = bigGrid.neighbors(index, includeDiagonals = false).toSet()
}

println(
    costOfTraversal(
        start = bigGrid.first(),
        end = bigGrid.last(),
        graph = bigMapNodes
    )
)
