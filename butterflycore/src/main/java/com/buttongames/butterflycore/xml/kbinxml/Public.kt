package com.buttongames.butterflycore.xml.kbinxml

import org.w3c.dom.Document

fun kbinEncode(d: Document, charset: String = "UTF-8") = KbinWriter(d, charset).getKbin()
fun kbinEncode(s: String, charset: String = "UTF-8") = kbinEncode(s.toXml(), charset)

fun kbinDecode(b: ByteArray) = KbinReader(b).getXml()

fun kbinDecodeToString(b: ByteArray) = kbinDecode(b).prettyString()
