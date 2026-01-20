package com.alekso.dltstudio.plugins.virtualdevice

import androidx.compose.ui.geometry.Rect

interface DeviceView

data class PointerView(
    val x: Float,
    val y: Float,
) : DeviceView

data class RectView(
    val rect: Rect,
    val id: String? = null,
) : DeviceView
