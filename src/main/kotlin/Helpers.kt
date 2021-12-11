// Made after our second grid-based problem on Day 11
class GridList<T>(
    private val backingList: List<T>,
    private val width: Int,
    private val height: Int = width
) : List<T> by backingList {
    init {
        if (backingList.size != width * height) {
            throw IllegalArgumentException("Expected list of size $width * $height (${width * height}), got ${backingList.size}")
        }
    }

    fun get(x: Int, y: Int) = get(y * width + x)

    fun neighborOffsets(offset: Int, includeDiagonals: Boolean = false): List<Int> {
        val x = offset % width
        val y = (offset - x) / width
        val diagonals = if (includeDiagonals)
            listOfNotNull(
                if (x > 0 && y > 0) offset - 1 - width else null, // up-left
                if (y > 0 && x < width - 1) offset - width + 1 else null, // up-right
                if (x > 0 && y < height - 1) offset + width - 1 else null, // down-left
                if (y < height - 1 && x < width - 1) offset + width + 1 else null, // down-right
            )
        else emptyList()
        return listOfNotNull(
            if (y > 0) offset - width else null, // up
            if (x > 0) offset - 1 else null, // left
            if (x < width - 1) offset + 1 else null,  // right
            if (y < height - 1) offset + width else null, // down
        ) + diagonals
    }

    fun neighborOffsets(x: Int, y: Int, includeDiagonals: Boolean = false): List<Int> =
        neighborOffsets(y * width + x, includeDiagonals)

    fun neighbors(offset: Int, includeDiagonals: Boolean = false): List<T> =
        neighborOffsets(offset, includeDiagonals)
            .map { idx -> backingList[idx] }

    fun neighbors(x: Int, y: Int, includeDiagonals: Boolean = false): List<T> =
        neighbors(y * width + x, includeDiagonals)
}
