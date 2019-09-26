package com.buttongames.butterflycore.xml.kbinxml

internal class Constants {
    companion object {
        val encodings = arrayOf("SHIFT_JIS", "ASCII", "ISO-8859-1", "EUC-JP", "SHIFT_JIS", "UTF-8")
        val encodingsReverse: Map<String, Int>

        init {
            val m1 = mutableMapOf<String, Int>()
            for (i in 1 until encodings.size) {
                m1[encodings[i]] = i
            }
            encodingsReverse = m1
        }
    }
}

internal enum class ControlTypes {
    NodeStart,
    Attribute,
    NodeEnd,
    FileEnd
}

internal val ControlTypeMap = mapOf(
    1 to ControlTypes.NodeStart,
    46 to ControlTypes.Attribute,
    190 to ControlTypes.NodeEnd,
    191 to ControlTypes.FileEnd
    //254 to ControlTypes.NodeEnd,
    //255 to ControlTypes.FileEnd
)

internal class KbinConverter(val fromString: (String) -> ByteArray, val toString: (ByteArray) -> String)

internal class KbinType(
    var names: List<String>,
    val size: Int,
    private val handler: KbinConverter,
    val count: Int = 1
) {

    constructor(name: String, size: Int, handler: KbinConverter, count: Int = 1) : this(
        mutableListOf(name),
        size,
        handler,
        count
    )

    val name: String
        get() = names[0]

    fun alias(alias: String): KbinType {
        (names as MutableList<String>) += alias
        return this
    }

    fun rename(name: String): KbinType {
        val mutable = (names as MutableList<String>)
        mutable.clear()
        mutable.add(name)
        return this
    }

    fun fromString(string: String): ByteArray = if (string.isEmpty()) byteArrayOf() else handler.fromString(string)
    fun toString(bytes: ByteArray): String = if (bytes.isEmpty()) "" else handler.toString(bytes)
}

internal operator fun Int.times(t: KbinType): KbinType {
    val newNames = t.names.map { this.toString() + it }
    val newSize = t.size * this
    fun newToString(input: ByteArray) =
        input.asIterable().chunked(t.size).joinToString(" ") { t.toString(it.toByteArray()) }

    fun newFromString(input: String) =
        input.split(" ").flatMap { t.fromString(it).asIterable() }.toByteArray()

    return KbinType(newNames, newSize, KbinConverter(::newFromString, ::newToString), this * t.count)
}

internal class Types {
    companion object {
        val s8 = KbinType("s8", 1, Converters.s8)
        val u8 = KbinType("u8", 1, Converters.u8)
        val s16 = KbinType("s16", 2, Converters.s16)
        val u16 = KbinType("u16", 2, Converters.u16)
        val s32 = KbinType("s32", 4, Converters.s32)
        val u32 = KbinType("u32", 4, Converters.u32)
        val s64 = KbinType("s64", 8, Converters.s64)
        val u64 = KbinType("u64", 8, Converters.u64)

        val binStub = KbinType(listOf("bin", "binary"), 1, Converters.stub)
        val strStub = KbinType(listOf("str", "string"), 1, Converters.stub)

        val ip4 = KbinType("ip4", 4, Converters.ip4)
        val time = KbinType("time", 4, Converters.u32)
        val float = KbinType(listOf("float", "f"), 4, Converters.float)
        val double = KbinType(listOf("double", "d"), 8, Converters.double)
        val bool = KbinType(listOf("bool", "b"), 1, Converters.bool)
    }
}

internal val kbinTypeMap = with(Types) {
    mapOf(
        2 to s8,
        3 to u8,
        4 to s16,
        5 to u16,
        6 to s32,
        7 to u32,
        8 to s64,
        9 to u64,
        10 to binStub,
        11 to strStub,
        12 to ip4,
        13 to time,
        14 to float,
        15 to double,
        16 to 2 * s8,
        17 to 2 * u8,
        18 to 2 * s16,
        19 to 2 * u16,
        20 to 2 * s32,
        21 to 2 * u32,
        22 to (2 * s64).alias("vs64"),
        23 to (2 * u64).alias("vu64"),
        24 to (2 * float).rename("2f"),
        25 to (2 * double).rename("2d").alias("vd"),
        26 to 3 * s8,
        27 to 3 * u8,
        28 to 3 * s16,
        29 to 3 * u16,
        30 to 3 * s32,
        31 to 3 * u32,
        32 to 3 * s64,
        33 to 3 * u64,
        34 to (3 * float).rename("3f"),
        35 to (3 * double).rename("3d"),
        36 to 4 * s8,
        37 to 4 * u8,
        38 to 4 * s16,
        39 to 4 * u16,
        40 to (4 * s32).alias("vs32"),
        41 to (4 * u32).alias("vu32"),
        42 to 4 * s64,
        43 to 4 * u64,
        44 to (4 * float).rename("4f").alias("vf"),
        45 to (4 * double).rename("4d"),
        48 to (16 * s8).rename("vs8"),
        49 to (16 * u8).rename("vu8"),
        50 to (8 * s16).rename("vs16"),
        51 to (8 * u16).rename("vu16"),
        52 to bool,
        53 to (2 * bool).rename("2b"),
        54 to (3 * bool).rename("3b"),
        55 to (4 * bool).rename("4b"),
        56 to (16 * bool).rename("vb")
    )
}

// internal var reverseKbinTypeMap: Map<String, Int> = kbinTypeMap.entries.associateBy({ it.value.name }) { it.key }
internal var reverseKbinTypeMap = mutableMapOf<String, Int>()