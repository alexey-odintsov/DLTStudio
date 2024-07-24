package com.alekso.dltstudio.model

import androidx.compose.ui.geometry.Size

data class VirtualDevice(
    val id: Long,
    val name: String,
    val size: Size,
) {
    companion object {
        val Empty = VirtualDevice(-1, "", Size.Zero)
    }
}