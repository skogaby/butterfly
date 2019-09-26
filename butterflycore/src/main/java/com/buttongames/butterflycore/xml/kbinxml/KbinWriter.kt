package com.buttongames.butterflycore.xml.kbinxml

import com.buttongames.butterflycore.xml.kbinxml.Types.Companion.binStub
import com.buttongames.butterflycore.xml.kbinxml.Types.Companion.strStub
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import kotlin.experimental.inv


internal class KbinWriter(val xml: Document, val encoding: String = "UTF-8", val compressed: Boolean = true) {
    private val header = ByteArray(4)

    val charset = Charset.forName(encoding)

    fun getKbin(): ByteArray {
        if (reverseKbinTypeMap.isEmpty()) {
            for (entry in kbinTypeMap.entries) {
                for (name in entry.value.names) {
                    reverseKbinTypeMap[name] = entry.key
                }
            }
        }
        val dataBuffer = KbinDataBuffer(charset)
        val nodeBuffer = KbinNodeBuffer(compressed, charset)

        header[0] = 0xA0u.toByte()
        header[1] = (if (compressed) 0x42u else 0x45u).toByte()
        header[2] = (Constants.encodingsReverse[encoding]!! shl 5).toByte()
        header[3] = header[2].inv()

        nodeRecurse(xml.documentElement, dataBuffer, nodeBuffer)

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
        var typeName = e.getAttribute("__type")
        val text = e.text
        if (typeName == "" && text.trim() != "") {
            typeName = "str"
        }
        val typeId = reverseKbinTypeMap[typeName]
        if (typeName != "" && typeId == null)
            throw KbinException("Type $typeName is not supported")
        val type = kbinTypeMap[typeId]

        var count = run {
            val count_attr = e.getAttribute("__count")
            val size_attr = e.getAttribute("__size")
            when {
                count_attr != "" -> count_attr
                size_attr != "" -> size_attr
                else -> null
            }?.toInt()
        }

        val isArray = (count != null) && type?.name !in strStub.names + binStub.names

        if (typeId == null) {
            nodeBuffer.writeU8(1u)
        } else {
            var toWrite = typeId.toUByte()
            if (isArray) {
                toWrite = toWrite or ((1 shl 6).toUByte())
            }
            nodeBuffer.writeU8(toWrite)
        }
        nodeBuffer.writeString(e.nodeName)

        if (type != null) {
            if (type == binStub) {
                count = text.length / 2
                dataBuffer.writeU32(count.toUInt())
                val toWrite = ByteConv.stringToBin(text)
                dataBuffer.writeTo4Byte(toWrite)
            } else if (type == strStub) {
                dataBuffer.writeString(text)
            } else {
                if (count != null) {
                    dataBuffer.writeU32((count * type.size).toUInt())
                }
                val split = text.splitAndJoin(type.count)
                //val toWrite = split.flatMap { type.fromString(it).asIterable() }.toByteArray()
                val toWrite = split.flatMap { type.fromString(it).asIterable() }.toByteArray()
                dataBuffer.writeBytes(toWrite)
            }
        }

        val attributes = e.attributeList.filter { it.nodeName !in listOf("__type", "__count", "__size") }
            .sortedBy { it.nodeName }

        for (a in attributes) {
            a as Attr
            nodeBuffer.writeU8(46u)
            nodeBuffer.writeString(a.nodeName)
            dataBuffer.writeString(a.value)
        }

        for (thing in e.childNodes(Node.ELEMENT_NODE)) {
            nodeRecurse(thing as Element, dataBuffer, nodeBuffer)
        }
        nodeBuffer.writeU8(254u)
    }
}