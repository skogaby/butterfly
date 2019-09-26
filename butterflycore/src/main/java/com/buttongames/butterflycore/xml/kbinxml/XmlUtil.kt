package com.buttongames.butterflycore.xml.kbinxml

import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

var Element.text: String
    get() = this.childNodes(Node.TEXT_NODE).firstOrNull()?.nodeValue ?: ""
    set(value) {
        val children = this.childNodes
        for (i in 0 until children.length) {
            val c = children.item(i)
            if (c.nodeType == Node.TEXT_NODE) {
                this.removeChild(c)
            }
        }
        val e = this.ownerDocument.createTextNode(value)
        this.appendChild(e)
    }

fun Element.childNodes(type: Short): Sequence<Node> {
    val children = this.childNodes
    return sequence {
        for (i in 0 until children.length) {
            val c = children.item(i)
            if (c.nodeType == type) {
                yield(c)
            }
        }
    }
}


fun Element.firstChild(name: String): Element? {
    var child: Node? = this.firstChild
    while (child != null) {
        if (child is Element && name == child.nodeName) return child
        child = child.nextSibling
    }
    return null
}

val Element.childElements: Sequence<Element>
    get() {
        return this.childNodes(Node.ELEMENT_NODE) as Sequence<Element>
    }

val Element.attributeList: List<Attr>
    get() {
        val result = mutableListOf<Attr>()
        val attrs = this.attributes!!
        for (i in 0 until attrs.length) {
            result.add(attrs.item(i) as Attr)
        }
        return result
    }

fun Document.prettyString(indent: Int = 4): String {
    this.xmlStandalone = true
    val stringWriter = StringWriter()
    val xmlOutput = StreamResult(stringWriter)
    val transformerFactory = TransformerFactory.newInstance()
    transformerFactory.setAttribute("indent-number", indent)
    val transformer = transformerFactory.newTransformer()

    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
    transformer.transform(DOMSource(this), xmlOutput)
    return xmlOutput.writer.toString()
}

fun String.toXml(): Document {
    val db = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val input = InputSource()
    input.characterStream = (StringReader(this))
    return db.parse(input)
}

fun File.toXml(): Document {
    val db = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    return db.parse(this)
}