package com.alekso.dltstudio.plugins.virtualdevice.model

data class VirtualDevice(
    val id: Int,
    val name: String,
    val width: Int,
    val height: Int,
) {
    companion object {
        val Empty = VirtualDevice(-1, "", 0, 0)
    }
}