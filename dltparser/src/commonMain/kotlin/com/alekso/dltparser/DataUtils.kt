package com.alekso.dltparser

enum class Endian {
    BIG,
    LITTLE
}

fun Byte.isBitSet(position: Int): Boolean {
    return ((toInt() shl (position.inv() and 31)) < 0)
}

fun Int.isBitSet(position: Int): Boolean {
    return ((this shl (position.inv() and 31)) < 0)
}

fun Byte.toHex(): String = "%02x".format(this)

fun ByteArray.readString(start: Int, bytes: Int) = decodeToString(start, start + bytes)

/**
 * Reads 2 bytes data signed Short
 */
fun ByteArray.readShort(start: Int, endian: Endian): Short {
    return if (endian == Endian.LITTLE) {
        ((get(start + 1).toInt() shl 8) or (get(start + 0).toInt())).toShort()
    } else {
        ((get(start + 0).toInt() shl 8) or (get(start + 1).toInt())).toShort()
    }
}

/**
 * Reads 4 bytes signed Int
 */
fun ByteArray.readInt(start: Int, endian: Endian): Int {
    return if (endian == Endian.LITTLE) {
        (get(start + 3).toInt() shl 24) or
                (get(start + 2).toInt() and 0xff shl 16) or
                (get(start + 1).toInt() and 0xff shl 8) or
                (get(start + 0).toInt() and 0xff)
    } else {
        (get(start + 0).toInt() shl 24) or
                (get(start + 1).toInt() and 0xff shl 16) or
                (get(start + 2).toInt() and 0xff shl 8) or
                (get(start + 3).toInt() and 0xff)
    }
}

fun ByteArray.toHex(): String =
    joinToString(separator = " ", limit = 256) { eachByte -> "%02x".format(eachByte) }