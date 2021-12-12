import java.io.File

data class Node(val name: String) {
    val neighbors: MutableSet<Node> = mutableSetOf()
    val alwaysVisitable = name.all { it.isUpperCase() }
    val isSmallRoom = name != "start" && name != "end" && name.all { it.isLowerCase() }
}

val edges = File("Day12Input.txt").readLines()
    .map {
        val split = it.split("-")
        split[0] to split[1]
    }

val nodes = edges
    .flatMap { listOf(it.first, it.second) }
    .toSet()
    .map { Node(it) }

val start = nodes.first { it.name == "start" }
val end = nodes.first { it.name == "end" }

// build graph
edges.forEach { (leftName, rightName) ->
    val left = nodes.first { it.name == leftName }
    val right = nodes.first { it.name == rightName }
    // Don't bother adding edges pointing *at* start because we can't go back there anyway. Makes life easier too.
    if (right != start) {
        left.neighbors.add(right)
    }
    if (left != start) {
        right.neighbors.add(left)
    }
}

// This would have problems if the input contains any adjacent of alwaysVisitable nodes
fun findPossiblePaths(
    path: List<Node>,
    extraSmallVisit: Boolean
): List<List<Node>> {
    val curNode = path.last()
    return if (curNode == end) {
        listOf(path)
    } else {
        curNode.neighbors
            .filter { neighbor -> neighbor.alwaysVisitable || neighbor !in path || extraSmallVisit }
            .flatMap { neighbor ->
                val spentSmallVisit = extraSmallVisit && neighbor.isSmallRoom && neighbor in path
                findPossiblePaths(
                    path = path + neighbor,
                    extraSmallVisit = extraSmallVisit && !spentSmallVisit
                )
            }
    }
}

// Part one
val paths = findPossiblePaths(listOf(start), extraSmallVisit = false)
println(paths.size)

// Part two
val paths2 = findPossiblePaths(listOf(start), extraSmallVisit = true)
println(paths2.size)
