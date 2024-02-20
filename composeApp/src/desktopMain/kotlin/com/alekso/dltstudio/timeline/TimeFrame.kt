package com.alekso.dltstudio.timeline

data class TimeFrame(
    val timestampStart: Long,
    val timestampEnd: Long,
    val offsetSeconds: Float,
    val scale: Float,
) {
    fun getTotalSeconds(): Int = ((timestampEnd - timestampStart) / 1000000).toInt()

    fun calculateSecSizePx(widthPx: Float) = (widthPx / getTotalSeconds()) * scale
}