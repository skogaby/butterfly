package com.buttongames.butterflycore.xml.kbinxml

import java.nio.charset.Charset

internal class KbinDataBuffer(bytes: ByteArray, val encoding: Charset) {
    constructor(encoding: Charset) : this(byteArrayOf(), encoding)

    private val data = bytes.toMutableList()

    val size: Int
        get() = data.size

    private var pos8 = 0
    private var pos16 = 0
    private var pos32 = 0

    fun readBytes(num: Int): ByteArray {
        val result: ByteArray

        val debug: String

        when {
            num == 0 -> return byteArrayOf()
            num == 1 -> {
                result = byteArrayOf(data[pos8])
                debug = "$pos8"
            }
            num == 2 -> {
                result = data.slice(pos16 until pos16 + 2).toByteArray()
                debug = "$pos16 - ${pos16 + 2 - 1}"
            }
            num >= 3 -> {
                result = data.slice(pos32 until pos32 + num).toByteArray()
                debug = "$pos32 - ${pos32 + num - 1}"
            }
            else -> throw KbinException("Invalid read of $num bytes")
        }
        // println("Read bytes $debug")
        realign(num)
        return result
    }

    private fun realign(bytesRead: Int) {
        fun pos8Follows() = pos8 % 4 == 0
        fun pos16Follows() = pos16 % 4 == 0

        if (bytesRead == 1) {
            if (pos8Follows()) {
                pos32 += 4
            }
            pos8++
        }
        if (bytesRead == 2) {
            if (pos16Follows()) {
                pos32 += 4
            }
            pos16 += 2
        }
        if (bytesRead >= 3) {
            var newNum = bytesRead
            if (newNum % 4 != 0) newNum += 4 - (newNum % 4)
            pos32 += newNum
        }
        if (pos8Follows()) {
            pos8 = pos32
        }
        if (pos16Follows()) {
            pos16 = pos32
        }
        /*println("Index 4: $pos32")
        println("Index 2: $pos16")
        println("Index 1: $pos8")
        println()*/
    }

    fun realign4Byte(num: Int) {
        realign(
            num + (if (num % 4 != 0) {
                4 - (num % 4)
            } else 0)
        )
    }

    fun reset() {
        pos8 = 0; pos16 = 0; pos32 = 0
    }

    fun readU8(): UByte {
        val result = readBytes(1)[0].toUByte()
        return result
    }

    fun readU16(): UShort {
        val result = readBytes(2)
        return result.toUShort()
    }

    fun readU32(): UInt {
        val result = readBytes(4)
        return result.toUInt()
    }

    fun readFrom4Byte(num: Int): ByteArray {
        if (num == 0) return byteArrayOf()
        val read = data.slice(pos32 until pos32 + num)
        // println("Read bytes $pos32 - ${pos32 + num}")
        realign4Byte(num)
        return read.toByteArray()
    }

    fun readString(length: Int): String {
        var readBytes = readFrom4Byte(length)
        readBytes = readBytes.sliceArray(0 until readBytes.lastIndex)
        return readBytes.toString(encoding)
    }

    fun writeBytes(bytes: ByteArray) {
        val length = bytes.size
        when {
            length == 0 -> return
            length == 1 -> {
                data.setOrAddAll(pos8, bytes)
            }
            length == 2 -> {
                data.setOrAddAll(pos16, bytes)
            }
            length >= 3 -> {
                data.setOrAddAll(pos32, bytes)
            }
            else -> {
                throw KbinException("Invalid write of $length bytes")
            }
        }
        realign(length)
    }

    fun writeU8(value: UByte) {
        writeBytes(byteArrayOf(value.toByte()))
    }

    fun writeU32(value: UInt) {
        writeBytes(value.toInt().toByteArray())
    }

    fun writeTo4Byte(bytes: ByteArray) {
        data.setOrAddAll(pos32, bytes)
        realign4Byte(bytes.size)
    }

    fun writeString(string: String) {
        val bytes = string.toByteArray(encoding) + 0 // null byte
        writeU32(bytes.size.toUInt())
        writeTo4Byte(bytes)
    }

    fun getContent(): ByteArray {
        return data.toByteArray()
    }

    fun pad() {
        while (data.size % 4 != 0) data.add(0)
    }
}