package com.alekso.dltparser


fun Byte.isBitSet(position: Int): Boolean {
    return ((toInt() shl (position.inv() and 31)) < 0)
}

fun Int.isBitSet(position: Int): Boolean {
    return ((this shl (position.inv() and 31)) < 0)
}

fun ByteArray.toInt16l(): Int = (get(1).toInt() shl 8) or (get(0).toInt())
fun ByteArray.toInt16b(): Int = (get(0).toInt() shl 8) or (get(1).toInt())

fun Byte.toHex(): String ="%02x ".format(this)

fun ByteArray.toInt32l(): Int =
    (get(3).toInt() shl 24) or
            (get(2).toInt() and 0xff shl 16) or
            (get(1).toInt() and 0xff shl 8) or
            (get(0).toInt() and 0xff)

fun ByteArray.toInt32b(): Int =
    (get(0).toInt() shl 24) or
            (get(1).toInt() and 0xff shl 16) or
            (get(2).toInt() and 0xff shl 8) or
            (get(3).toInt() and 0xff)

fun ByteArray.toHex(): String =
    joinToString(separator = "", limit = 256) { eachByte -> "%02x ".format(eachByte) }