package com.buttongames.butterflycore.xml.kbinxml

import com.buttongames.butterflycore.xml.kbinxml.ByteConv.E.longToBytes
import java.lang.Double
import java.lang.Float
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

internal class ByteConv {
    companion object E {
        fun bytesToLong(array: ByteArray, numBytes: Int, littleEndian: Boolean = false): Long {
            var bytes = array
            if (!littleEndian) {
                bytes = array.reversedArray()
            }
            val actualArray = bytes.unsigned()
            var result: Long = 0
            for (i in 0 until numBytes) {
                result = result or (actualArray[i].toLong() shl (i * 8))
            }
            return result
        }

        fun longToBytes(input: Long, numBytes: Int): ByteArray {
            val result = ByteArray(numBytes)
            for (i in 0 until numBytes) {
                result[numBytes - i - 1] = (input shr (i * 8)).toByte()
            }
            return result
        }

        /*fun binToString(array: ByteArray): String {
            val sb = StringBuilder()
            for (e in array) {
                var current = e.toUInt().toInt().toString(16)
                if (current.length == 1) current = "0$current"
                sb.append(current)
            }
            return sb.toString()
        }*/

        private val hexArray = "0123456789abcdef".toCharArray()
        fun binToString(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].posInt()
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }

        fun stringToBin(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }

        fun ipToString(array: ByteArray) = array.joinToString(".") { it.toUByte().toString() }

        fun stringToIp(string: String) = string.split(".").map { it.toUByte().toByte() }.toByteArray()

        fun floatToString(array: ByteArray) = String.format(Locale.US, "%.6f", ByteBuffer.wrap(array).float)
        fun doubleToString(array: ByteArray) = String.format(Locale.US, "%.6f", ByteBuffer.wrap(array).double)

        fun stringToFloat(string: String) = Float.floatToRawIntBits(Float.parseFloat(string)).toByteArray()
        fun stringToDouble(string: String) = Double.doubleToRawLongBits(Double.parseDouble(string)).toByteArray()

        fun boolToString(array: ByteArray) = (array[0] and 1).toString()
        fun stringToBool(string: String) = byteArrayOf(if (string == "1") 1 else 0)
    }
}

internal fun ByteArray.toByte(): Byte = this[0]
internal fun ByteArray.toShort(): Short = ByteConv.bytesToLong(this, 2).toShort()
internal fun ByteArray.toInt(): Int = ByteConv.bytesToLong(this, 4).toInt()
internal fun ByteArray.toLong(): Long = ByteConv.bytesToLong(this, 8)
internal fun ByteArray.toUByte(): UByte = this[0].toUByte()
internal fun ByteArray.toUShort(): UShort = this.toShort().toUShort()
internal fun ByteArray.toUInt(): UInt = this.toInt().toUInt()
internal fun ByteArray.toULong(): ULong = this.toLong().toULong()

internal fun Short.toByteArray() = longToBytes(this.toLong(), 2)
internal fun Int.toByteArray() = longToBytes(this.toLong(), 4)
internal fun Long.toByteArray() = longToBytes(this, 8)

internal fun String.toByteA() = byteArrayOf(BigInteger(this).toByte())
internal fun String.toShortBytes() = BigInteger(this).toShort().toByteArray()
internal fun String.toIntBytes() = BigInteger(this).toInt().toByteArray()
internal fun String.toLongBytes() = BigInteger(this).toLong().toByteArray()
internal inline fun String.toUByteA() = this.toByteA()
internal inline fun String.toUShortBytes() = this.toShortBytes()
internal inline fun String.toUIntBytes() = this.toIntBytes()
internal inline fun String.toULongBytes() = this.toLongBytes()

internal inline fun Byte.posInt() = this.toUByte().toInt()