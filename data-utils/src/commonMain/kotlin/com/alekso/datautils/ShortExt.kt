package com.alekso.datautils

fun Short.isBitSet(position: Int): Boolean {
    val mask = 1 shl position
    val result = this.toInt() and mask
    return (this.toInt() and result) == mask
}

fun Short.toBinary(bits: Int): String {
    return toUShort().toString(2).padStart(bits, '0')
}

fun Short.toHex(bytes: Int): String {
    return toUShort().toString(16).padStart(bytes * 2, '0')
}

