package com.buttongames.butterflycore.xml.kbinxml

import nu.xom.Builder
import nu.xom.Document

fun kbinEncode(d: Document) = KbinWriter(d).getKbin()
fun kbinEncode(s: String) = kbinEncode(Builder().build(s, null))

fun kbinDecode(b: ByteArray) = KbinReader(b).getXml()

fun kbinDecodeToString(b: ByteArray) = kbinDecode(b).prettyString()
