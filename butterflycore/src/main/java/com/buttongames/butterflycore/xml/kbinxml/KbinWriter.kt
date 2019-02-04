package com.buttongames.butterflycore.xml.kbinxml

import nu.xom.Document
import nu.xom.Element
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import kotlin.experimental.inv

internal class KbinWriter(val xml: Document, val encoding: String = "SHIFT_JIS", val compressed: Boolean = true) {
    private val header = ByteArray(4)

    val charset = Charset.forName(encoding)

    fun getKbin(): ByteArray {
        val dataBuffer = KbinDataBuffer(charset)
        val nodeBuffer = KbinNodeBuffer(compressed, charset)

        header[0] = 0xa0u.toByte()
        header[1] = (if (compressed) 0x42u else 0x45u).toByte()
        header[2] = (Constants.encodingsReverse[encoding]!! shl 5).toByte()
        header[3] = header[2].inv()

        nodeRecurse(xml.rootElement, dataBuffer, nodeBuffer)

        nodeBuffer.writeU8(255u)
        nodeBuffer.pad()
        dataBuffer.pad()
        val output = ByteArrayOutputStream()
        output.write(header)
        output.write(nodeBuffer.size.toByteArray())
        output.write(nodeBuffer.getContent())
        output.write(dataBuffer.size.toByteArray())
        output.write(dataBuffer.getContent())
        return output.toByteArray()

    }

    private fun nodeRecurse(e: Element, dataBuffer: KbinDataBuffer, nodeBuffer: KbinNodeBuffer) {
        val typeName = e.getAttribute("__type")?.value
        val typeId = reverseKbinTypeMap[typeName]
        if (typeName != null && typeId == null) throw KbinException("Type $typeName is not supported")
        val type = kbinTypeMap[typeId]
        val count = (e.getAttribute("__count")?.value ?: e.getAttribute("__size")?.value)?.toInt()

        val isArray = (count != null) && type?.name !in listOf("bin", "str")

        if (typeId == null) {
            nodeBuffer.writeU8(1u)
        } else {
            var toWrite = typeId.toUByte()
            if (isArray) {
                toWrite = toWrite or ((1 shl 6).toUByte())
            }
            nodeBuffer.writeU8(toWrite)
        }
        nodeBuffer.writeString(e.localName)
        if (type != null) {
            if (type.name == "bin") {
                if (count != null) {
                    dataBuffer.writeU32(count.toUInt())
                }
                val toWrite = ByteConv.stringToBin(e.text)
                dataBuffer.writeTo4Byte(toWrite)
            } else if (type.name == "str") {
                dataBuffer.writeString(e.text)
            } else {
                if (count != null) {
                    dataBuffer.writeU32((count * type.size).toUInt())
                }
                val split = e.text.splitAndJoin(type.count)
                val toWrite = split.flatMap { type.fromString(it).asIterable() }.toByteArray()
                dataBuffer.writeBytes(toWrite)
            }
        }
        /*for (a in e) {
            val name = a.localName
            if (name in arrayOf("__count", "__size", "__type"))
                continue
            val value = a.value
            nodeBuffer.writeU8(46u)
            nodeBuffer.writeString(name)
            dataBuffer.writeString(value)
        }*/
        val attributes = e.iterator().asSequence()
                .filter { it.localName !in listOf("__type", "__count", "__size") }
                .sortedBy { it.localName }

        for (a in attributes) {
            nodeBuffer.writeU8(46u)
            nodeBuffer.writeString(a.localName)
            dataBuffer.writeString(a.value)
        }
        for (c in e.childElements) {
            nodeRecurse(c, dataBuffer, nodeBuffer)
        }
        nodeBuffer.writeU8(254u)
    }
}