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

    fun zoom(zoomIn: Boolean, factor: Double = 0.1): TimeFrame {
        val center = (timeStart + timeEnd) / 2
        val scale = if (zoomIn) 1 - factor else 1 + factor
        val halfNewDuration = (duration * scale / 2).toLong()
        return if (zoomIn && halfNewDuration <= 500) {
            TimeFrame(
                timeStart = center - 500,
                timeEnd = center + 500
            )
        } else TimeFrame(
            timeStart = center - halfNewDuration,
            timeEnd = center + halfNewDuration
        )
    }

}
