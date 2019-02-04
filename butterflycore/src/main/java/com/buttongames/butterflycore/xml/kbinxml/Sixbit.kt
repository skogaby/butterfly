package com.buttongames.butterflycore.xml.kbinxml

import kotlin.experimental.or
import kotlin.math.roundToInt

internal class Sixbit {
    companion object {
        private const val characters = "0123456789:ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"
        private val charToByte: Map<Char, Byte> = mutableMapOf()

        init {
            charToByte as MutableMap<Char, Byte>
            for (i in 0 until characters.length) {
                charToByte[characters[i]] = i.toByte()
            }
        }

        private fun pack(bytes: ByteArray): ByteArray {
            val output = ByteArray(Math.ceil(bytes.size * 6.0 / 8).roundToInt())
            for (i in 0 until bytes.size * 6) {
                output[i / 8] = output[i / 8] or
                        ((bytes[i / 6].toInt() shr (5 - (i % 6)) and 1)
                                shl (7 - (i % 8))).toByte()
            }
            return output
        }

        fun decode(input: ByteArray, length: Int): String {
            val charBytes = ByteArray(length)
            for (i in 0 until length * 6) {
                charBytes[i / 6] = charBytes[i / 6] or
                        (((input[i / 8].toInt() shr (7 - (i % 8))) and 1)
                                shl (5 - (i % 6))).toByte()
            }
            return charBytes.map { characters[it.toInt()] }.joinToString("")
        }

        fun encode(input: String): ByteArray {
            val bytes = ByteArray(input.length)
            for (i in 0 until input.length) {
                bytes[i] = charToByte[input[i]]!!
            }
            return pack(bytes)
        }
    }
}