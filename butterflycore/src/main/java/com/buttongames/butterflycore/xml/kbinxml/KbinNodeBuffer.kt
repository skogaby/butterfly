package com.buttongames.butterflycore.xml.kbinxml

import java.nio.charset.Charset
import kotlin.math.roundToInt

internal class KbinNodeBuffer(bytes: ByteArray, val compressed: Boolean, val encoding: Charset) {
    constructor (compressed: Boolean, encoding: Charset) : this(byteArrayOf(), compressed, encoding)

    private val data = bytes.toMutableList()
    private var index = 0

    val size: Int
        get() = data.size

    fun readU8() = readBytes(1)[0].toUByte()

    fun readBytes(num: Int): ByteArray {
        val result = data.slice(index until index + num).toByteArray()
        index += num
        return result
    }

    fun reset() {
        index = 0
    }

    fun readString(): String {
        val length = readU8().toInt()
        if (compressed) {
            val toRead = Math.ceil(length * 6 / 8.0).roundToInt()
            val nameBytes = readBytes(toRead)
            return Sixbit.decode(nameBytes, length)
        } else {
            val readBytes = readBytes((length and 64.inv()) + 1)
            return readBytes.toString(encoding)
        }
    }

    fun writeBytes(bytes: ByteArray) {
        data.setOrAddAll(index, bytes)
        index += bytes.size
    }

    fun writeU8(byte: UByte) {
        writeBytes(byteArrayOf(byte.toByte()))
    }

    fun writeString(string: String) {
        val bytes: ByteArray
        if (compressed) {
            bytes = Sixbit.encode(string)
            writeU8(string.length.toUByte())
        } else {
            bytes = string.toByteArray(encoding)
            writeU8(((bytes.size - 1) or (1 shl 6)).toUByte())
        }
        writeBytes(bytes)
    }

    fun getContent(): ByteArray {
        return data.toByteArray()
    }

    fun pad() {
        while (data.size % 4 != 0) data.add(0)
    }
}