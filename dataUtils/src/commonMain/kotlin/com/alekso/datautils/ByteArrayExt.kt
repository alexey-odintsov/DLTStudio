package com.alekso.datautils

fun ByteArray.toHex(): String =
    joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }

fun ByteArray.readString(start: Int, bytes: Int) = decodeToString(start, start + bytes)

/**
 * Reads 2 bytes data signed Short
 */
fun ByteArray.readShort(start: Int, endian: Endian): Short {
    if (size < 2) {
        throw IllegalStateException("Trying to read 2 bytes from $size byteArray")
    }

    return if (endian == Endian.LITTLE) {
        ((get(start + 1).toInt() shl 8) or (get(start + 0).toInt())).toShort()
    } else {
        ((get(start + 0).toInt() shl 8) or (get(start + 1).toInt())).toShort()
    }
}

/**
 * Reads 2 bytes data signed Short
 */
fun ByteArray.readUShort(start: Int, endian: Endian): UShort {
    if (size < 2) {
        throw IllegalStateException("Trying to read 2 bytes from $size byteArray")
    }

    return if (endian == Endian.LITTLE) {
        (((get(start + 1).toInt() and 255) shl 8) or (get(start).toInt() and 255)).toUShort()
    } else {
        (((get(start).toInt() and 255) shl 8) or (get(start + 1).toInt() and 255)).toUShort()
    }
}

/**
 * Reads 4 bytes signed Int
 */
fun ByteArray.readInt(start: Int, endian: Endian): Int {
    if (size < 4) {
        throw IllegalStateException("Trying to read 4 bytes from $size byteArray")
    }

    return if (endian == Endian.LITTLE) {
        (get(start + 3).toInt() shl 24) or (get(start + 2).toInt() and 0xff shl 16) or (get(start + 1).toInt() and 0xff shl 8) or (get(
            start + 0
        ).toInt() and 0xff)
    } else {
        (get(start + 0).toInt() shl 24) or (get(start + 1).toInt() and 0xff shl 16) or (get(start + 2).toInt() and 0xff shl 8) or (get(
            start + 3
        ).toInt() and 0xff)
    }
}