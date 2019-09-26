package com.buttongames.butterflycore.xml.kbinxml

import com.buttongames.butterflycore.xml.kbinxml.ControlTypes.*
import com.buttongames.butterflycore.xml.kbinxml.Types.Companion.binStub
import com.buttongames.butterflycore.xml.kbinxml.Types.Companion.strStub
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.nio.charset.Charset
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.experimental.inv


internal class KbinReader(data: ByteArray) {

    init {
        if (data[0] != 0xA0.toByte()) {
            throw KbinException("First byte must be 0xA0")
        }
    }

    val compressed = when (data[1].toInt()) {
        0x42 -> true
        0x45 -> false
        else -> throw KbinException("Second byte of data must be 0x42 or 0x45")
    }

    val encoding = when (
        val tmp = data[2].posInt() shr 5) {
        in 0..5 -> Charset.forName(Constants.encodings[tmp])
        else -> throw KbinException("Third byte does not match any encoding")
    }

    init {
        if (data[2] != data[3].inv()) throw KbinException("Fourth byte must be inverse of third")
    }

    private val nodeBuffer: KbinNodeBuffer
    private val dataBuffer: KbinDataBuffer

    init {
        val nodeLength = data.slice(4 until 8).toByteArray().toUInt().toInt()
        nodeBuffer = KbinNodeBuffer(data.slice(8 until (8 + nodeLength)).toByteArray(), compressed, encoding)
        val dataStart = 12 + nodeLength
        val dataLength = data.slice(dataStart - 4 until dataStart).toByteArray().toUInt().toInt()
        dataBuffer = KbinDataBuffer(data.slice(dataStart until (dataStart + dataLength)).toByteArray(), encoding)
    }

    fun getXml(): Document {
        nodeBuffer.reset(); dataBuffer.reset()
        var currentNode: Element? = null
        val nodeStack = ArrayDeque<Element>()
        var end = false

        val dbf = DocumentBuilderFactory.newInstance()
        val builder = dbf.newDocumentBuilder()
        val doc = builder.newDocument()
        doc.xmlStandalone = true

        while (!end) {
            val current = nodeBuffer.readU8().toInt()
            val actual = current and (1 shl 6).inv()
            val controlType = ControlTypeMap[actual]
            val valueType = kbinTypeMap[actual]

            if (controlType != null)
                when (controlType) {
                    NodeStart -> {
                        val name = nodeBuffer.readString()

                        val newNode = doc.createElement(name)
                        if (currentNode != null) {
                            currentNode.appendChild(newNode)
                            nodeStack.push(currentNode)
                        }
                        currentNode = newNode
                    }
                    NodeEnd -> {
                        if (nodeStack.size > 0) {
                            //println("Popping Node ${nodeStack.peek().localName}")
                            //currentNode?.sortAttributes()
                            currentNode = nodeStack.pop()
                        }
                    }
                    Attribute -> {
                        val name = nodeBuffer.readString()

                        val valueLength = dataBuffer.readU32().toInt()
                        val value = dataBuffer.readString(valueLength)

                        //println("Got attribute $name with value $value")

                        currentNode!!.setAttribute(name, value)
                    }
                    FileEnd -> {
                        if (nodeStack.size == 0) {
                            end = true
                        } else throw KbinException("Byte indicates end of file, but parsing is not done")
                    }
                }
            else if (valueType != null) {
                val valueName = valueType.name
                val isArray = (((current shr 6) and 1) == 1) or (valueName in listOf("bin", "str"))
                val nodeName = nodeBuffer.readString()
                val arraySize = if (isArray) dataBuffer.readU32().toInt() else valueType.size

                nodeStack.push(currentNode!!)
                val newNode = doc.createElement(nodeName)
                newNode.setAttribute("__type", valueName)
                currentNode.appendChild(newNode)
                currentNode = newNode

                val numElements = arraySize / valueType.size

                when (valueType) {
                    binStub -> {
                        currentNode.setAttribute("__size", arraySize.toString())
                        val bytes = dataBuffer.readFrom4Byte(arraySize)
                        currentNode.text = ByteConv.binToString(bytes)
                    }
                    strStub -> {
                        currentNode.text = dataBuffer.readString(arraySize)
                    }
                    else -> {
                        if (isArray) currentNode.setAttribute("__count", numElements.toString())

                        val byteList = dataBuffer.readBytes(arraySize)
                        val stringList = mutableListOf<String>()
                        for (i in 0 until numElements) {
                            val bytes = byteList.sliceArray(i * valueType.size until (i + 1) * valueType.size)
                            stringList.add(valueType.toString(bytes))
                        }
                        currentNode.text = stringList.joinToString(separator = " ")
                    }

                }
            } else if (current != 1) {
                throw KbinException("Unsupported node type with ID $actual")
            }
        }
        doc.appendChild(currentNode)
        return doc
    }
}