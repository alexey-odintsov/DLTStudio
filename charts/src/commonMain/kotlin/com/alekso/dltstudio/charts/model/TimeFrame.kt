package com.alekso.dltstudio.charts.model

import kotlin.math.roundToInt

data class TimeFrame(
    val timeStart: Long,
    val timeEnd: Long,
) {
    val duration
        get() = timeEnd - timeStart

    val durationSec = ((timeEnd - timeStart).toFloat() / 1_000_000f).roundToInt()

    fun move(dx: Long): TimeFrame {
        return copy(timeStart = timeStart + dx, timeEnd = timeEnd + dx)
    }

    fun zoom(zoomIn: Boolean): TimeFrame {
        val factor = 10
        return if (zoomIn) {
            copy(timeStart = timeStart + duration / factor, timeEnd = timeEnd - duration / factor)
        } else {
            copy(timeStart = timeStart - duration / factor, timeEnd = timeEnd + duration / factor)
        }
    }
}
