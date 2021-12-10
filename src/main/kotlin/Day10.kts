import java.io.File

val lines = File("Day10Input.txt").readLines()

val chunkPairs = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>'
)

sealed class Error {
    abstract val score: Long

    class Corrupted(char: Char) : Error() {
        override val score = when (char) {
            ')' -> 3L
            ']' -> 57L
            '}' -> 1197L
            '>' -> 25137L
            else -> throw Error("Unexpected error char: $char")
        }
    }

    class Incomplete(remainingOpeners: List<Char>) : Error() {
        override val score = run {
            remainingOpeners
                .reversed()
                .fold(0L) { acc, next ->
                    acc * 5 + when (next) {
                        '(' -> 1L
                        '[' -> 2L
                        '{' -> 3L
                        '<' -> 4L
                        else -> throw Error("Unexpected remaining opener $next")
                    }
                }

        }
    }
}

fun validate(line: String): Error {
    val stack = ArrayDeque<Char>()
    for (char in line) {
        when (char) {
            in chunkPairs.keys -> stack.addLast(char)
            in chunkPairs.values -> {
                val opener = stack.removeLastOrNull()
                if (opener == null || chunkPairs[opener] != char) {
                    return Error.Corrupted(char)
                }

            }
            else -> throw Error("Unexpected input: $char")
        }
    }
    return Error.Incomplete(stack)
}

val errors = lines.map(::validate)

// Part one
errors
    .filterIsInstance<Error.Corrupted>()
    .sumOf { it.score }
    .run(::println)

// Part two
errors
    .filterIsInstance<Error.Incomplete>()
    .map { it.score }
    .sorted()
    .let {
        it[it.size / 2]
    }
    .run(::println)
