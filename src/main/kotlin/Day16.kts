import java.io.File

sealed class Packet(val version: Int, val typeId: Int) {
    abstract fun eval(): Long
    class Literal(version: Int, val value: Long) : Packet(version, 4) {
        override fun eval(): Long = value

    }
    class Operator(version: Int, typeId: Int) : Packet(version, typeId) {
        val subpackets = mutableListOf<Packet>()
        override fun eval(): Long = when (typeId) {
            0 -> subpackets.sumOf { it.eval() }
            1 -> subpackets.fold(1L) { acc, packet -> acc * packet.eval() }
            2 -> subpackets.minOf { it.eval() }
            3 -> subpackets.maxOf { it.eval() }
            5 -> if (subpackets[0].eval() > subpackets[1].eval()) 1L else 0L
            6 -> if (subpackets[0].eval() < subpackets[1].eval()) 1L else 0L
            7 -> if (subpackets[0].eval() == subpackets[1].eval()) 1L else 0L
            else -> error("Unsupported operator typeId $typeId")
        }
    }
}

val bits = File("Day16Input.txt")
    .readText()
    .map {
        it.digitToInt(radix = 16) }
    .flatMap {
        it.toString(radix = 2).padStart(length = 4, padChar = '0').toList()
    }

fun <T> Iterator<T>.take(n: Int): List<T> = (0 until n).map { next() }

fun List<Char>.value(): Int = joinToString(separator = "").toInt(radix = 2)
fun List<Char>.longValue(): Long = joinToString(separator = "").toLong(radix = 2)

fun readPackets(bits: ListIterator<Char>, packetLimit: Int? = null, totalBits: Int? = null): List<Packet> {
    val packets = mutableListOf<Packet>()
    var packetsRead = 0
    while(bits.hasNext()) {
        if (packetsRead == packetLimit) break
        if (totalBits != null) {
            val remainingBits = totalBits - bits.nextIndex()
            // There can be padding due to the hex representation. Packets have to be at least 11 bits long.
            if (remainingBits < 11) break
        }
        val version = bits.take(3).value()
        when (val typeId = bits.take(3).value()) {
            4 -> { // literal
                val literalBits = mutableListOf<Char>()
                var groupsRead = 0
                while (true) {
                    val groupLeader = bits.next()
                    groupsRead += 1
                    literalBits.addAll(bits.take(4))
                    if (groupLeader == '0') break
                }
                packets.add(Packet.Literal(version, literalBits.longValue()))
                packetsRead += 1
            }
            else -> { // operator
                val subpackets = when (bits.next()) {
                    '0' -> { // next 15 bits are bit-length of sub-packets
                        val subpacketsLength = bits.take(15).value()
                        readPackets(bits.take(subpacketsLength).listIterator())
                    }
                    '1' -> { // next 11 bits are number of sub-packets
                        val subpacketsQty = bits.take(11).value()
                        readPackets(bits, packetLimit = subpacketsQty)
                    }
                    else -> error("Unexpected length type bit")
                }
                packets.add(
                    Packet.Operator(version, typeId).apply {
                        this.subpackets.addAll(subpackets)
                    }
                )
                packetsRead += 1
            }
        }
    }
    return packets
}

val packets = readPackets(bits = bits.listIterator(), totalBits = bits.size)

// Part one
fun List<Packet>.versionSum(): Int = fold(0) { acc, packet ->
    acc + packet.version + ((packet as? Packet.Operator)?.subpackets?.versionSum() ?: 0)
}
println(packets.versionSum())

// Part two
println(packets[0].eval())
