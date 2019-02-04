package com.buttongames.butterflycore.xml.kbinxml

internal class Converters {
    companion object {
        val s8 = KbinConverter({ it.toByteA() }) { it.toByte().toString() }
        val u8 = KbinConverter({ it.toUByteA() }) { it.toUByte().toString() }
        val s16 = KbinConverter({ it.toShortBytes() }) { it.toShort().toString() }
        val u16 = KbinConverter({ it.toUShortBytes() }) { it.toUShort().toString() }
        val s32 = KbinConverter({ it.toIntBytes() }) { it.toInt().toString() }
        val u32 = KbinConverter({ it.toUIntBytes() }) { it.toUInt().toString() }
        val s64 = KbinConverter({ it.toLongBytes() }) { it.toLong().toString() }
        val u64 = KbinConverter({ it.toULongBytes() }) { it.toULong().toString() }

        // val bin = KbinConverter(ByteConv.E::stringToBin, ByteConv.E::binToString)
        val stub = KbinConverter({ "STUB".toByteArray() }, { "STUB" }) // scary

        val ip4 = KbinConverter(ByteConv.E::stringToIp, ByteConv.E::ipToString)
        val float = KbinConverter(ByteConv.E::stringToFloat, ByteConv.E::floatToString)
        val double = KbinConverter(ByteConv.E::stringToDouble, ByteConv.E::doubleToString)

        val bool = KbinConverter(ByteConv.E::stringToBool, ByteConv.E::boolToString)
    }
}