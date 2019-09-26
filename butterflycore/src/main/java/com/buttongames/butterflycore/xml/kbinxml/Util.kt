package com.buttongames.butterflycore.xml.kbinxml

import java.nio.charset.Charset

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

internal fun ByteArray.unsigned(): UByteArray {
    val length = this.size
    var result = UByteArray(length) { 0x00u }
    for (i in 0 until length) {
        result[i] = this[i].toUByte()
    }
    return result
}

class KbinException internal constructor(override var message: String) : Exception(message)

internal fun MutableList<Byte>.setOrAdd(pos: Int, byte: Byte) {
    if (pos == this.size) {
        this.add(byte)
    } else {
        while (pos > this.size - 1) {
            this.add(0)
        }
        this[pos] = byte
    }
}

internal fun MutableList<Byte>.setOrAddAll(pos: Int, bytes: ByteArray) {
    var posMut = pos
    for (b in bytes) {
        this.setOrAdd(posMut, b)
        posMut++
    }
}

internal fun ByteArray.toString(encoding: String) = this.toString(Charset.forName(encoding))

/*internal fun String.splitAndJoin(count: Int): Array<String> {
    val input = this.split(" ")
    val output = Array(input.size / count) { "" }
    for (i in 0 until output.size) {
        output[i] = input.slice(i * count until (i + 1) * count).joinToString(" ")
    }
    return output
}*/

internal fun String.splitAndJoin(count: Int) =
    this.split(" ").chunked(count).map { it.joinToString(" ") }.toTypedArray()


fun measureMs(times: Int = 1, function: () -> Unit) {
    for (i in 0 until times) {
        val startTime = System.nanoTime()
        function()
        val endTime = System.nanoTime()

        val duration = endTime - startTime  //divide by 1000000 to get milliseconds.
        println("Took ${duration / 1000000.0} ms")
    }
}