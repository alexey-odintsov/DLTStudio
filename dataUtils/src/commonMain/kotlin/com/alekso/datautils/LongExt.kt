package com.alekso.datautils

fun Long.isBitSet(position: Int): Boolean {
    val mask = 1L shl position
    val result = this and mask
    return (this and result) == mask
}

fun Long.toBinary(bits: Int): String {
    return toULong().toString(2).padStart(bits, '0')
}

fun Long.toHex(bytes: Int): String {
    return toULong().toString(16).padStart(bytes * 2, '0')
}
