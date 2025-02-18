package com.alekso.dltstudio.model

data class VirtualDevice(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
) {
    companion object {
        val Empty = VirtualDevice(-1, "", 0, 0)
    }
}