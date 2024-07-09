package com.alekso.dltstudio.logs.infopanel

import androidx.compose.ui.geometry.Size

data class VirtualDevice(
    val id: Int,
    val name: String,
    val size: Size,
) {
    companion object {
        val Empty = VirtualDevice(-1, "", Size.Zero)
    }
}