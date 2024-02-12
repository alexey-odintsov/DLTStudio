package com.alekso.dltstudio.timeline

data class TimeFrame(
    val timestampStart: Long,
    val timestampEnd: Long,
    val offsetSeconds: Float,
    val scale: Float,
) {
    fun getTotalSeconds(): Int = (timestampEnd - timestampStart).toInt() / 1000

    fun calculateSecSizePx(widthPx: Float) = (widthPx / getTotalSeconds()) * scale
}