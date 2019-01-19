package com.buttongames.butterfly.xml.kbinxml

import nu.xom.*
import java.io.ByteArrayOutputStream
import java.io.File

fun deepCompare(a: Document, b: Document): Boolean {
    return deepCompare(a.copy().rootElement, b.copy().rootElement)
}

private fun deepCompare(a: Element, b: Element): Boolean {
    a.sortAttributes(); b.sortAttributes()
    val nameA = a.localName
    val nameB = b.localName
    check(nameA == nameB) { "Element names are different: $nameA != $nameB" }
    val attrItA = a.iterator()
    val attrItB = b.iterator()
    while (attrItA.hasNext()) {
        val attrA = attrItA.next()
        val attrB = attrItB.next()
        check(attrA.localName == attrB.localName) { "Attribute names are different: ${attrA.localName} != ${attrB.localName}" }
        if (attrB.value == "bib")
            println("break")
        check(attrA.value == attrB.value) { "Attribute values are different for \"${attrA.localName}\": ${attrA.value} != ${attrB.value}" }
    }
    if (a.text != "") {
        check(a.text == b.text) { "Text inside of tags does not match: ${a.value} != ${b.value}" }
    }
    val elemItA = a.childElements.iterator()
    val elemItB = b.childElements.iterator()
    while (elemItA.hasNext()) {
        deepCompare(elemItA.next(), elemItB.next())
    }
    return true
}

fun Document.prettyString(): String {
    val o = ByteArrayOutputStream()
    val serializer = Serializer(o, "UTF-8")
    serializer.setIndent(4)
    serializer.write(this)
    return o.toString("UTF-8")
}

var Element.text: String
    get() {
        for (i in 0 until childCount) {
            val e = getChild(i)
            if (e is Text) {
                return e.value
            }
        }
        return ""
    }
    set(value: String) {
        this.appendChild(value)
    }


internal fun Element.sortAttributesRec(): Element {
    this.sortAttributes()
    for (e in this.childElements) {
        sortAttributesRecE(e)
    }
    return this
}

private fun sortAttributesRecE(e: Element) {
    e.sortAttributes()
    for (b in e.childElements) {
        sortAttributesRecE(b)
    }
}

fun String.toXml() = Builder().build(this, null)!!

fun File.toXml() = Builder().build(this)