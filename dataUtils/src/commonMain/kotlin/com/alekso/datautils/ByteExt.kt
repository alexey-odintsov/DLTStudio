package com.alekso.datautils

fun Byte.isBitSet(position: Int): Boolean {
    val mask = 1 shl position
    val result = this.toInt() and mask
    return (this.toInt() and result) == mask
}

fun Byte.toBinary(bits: Int): String {
    return toUByte().toString(2).padStart(bits, '0')
}

fun Byte.toHex(): String = "%02x".format(this)

