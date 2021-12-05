import java.io.File


val commands = File("Day2Input.txt")
    .readLines()
    .map {
        val tokens = it.split(" ")
        Pair(tokens[0], tokens[1].toInt())
    }

// Part one
var horizontal = 0
var depth = 0

commands.forEach { (action, amount) ->
    when(action) {
        "forward" -> horizontal += amount
        "down" -> depth += amount
        "up" -> depth -= amount
    }
}
println(horizontal * depth)

// Part two
horizontal = 0
depth = 0
var aim = 0

commands.forEach { (action, amount) ->
    when (action) {
        "forward" -> {
            horizontal += amount
            depth += aim * amount
        }
        "down" -> aim += amount
        "up" -> aim -= amount
    }
}
println(horizontal * depth)