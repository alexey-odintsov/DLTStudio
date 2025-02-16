package com.alekso.datautils

fun Int.isBitSet(position: Int): Boolean {
    val mask = 1 shl position
    val result = this and mask
    return (this and result) == mask
}

fun Int.toBinary(bits: Int): String {
    return toUInt().toString(2).padStart(bits, '0')
}

fun Int.toHex(bytes: Int): String {
    return toUInt().toString(16).padStart(bytes * 2, '0')
}
