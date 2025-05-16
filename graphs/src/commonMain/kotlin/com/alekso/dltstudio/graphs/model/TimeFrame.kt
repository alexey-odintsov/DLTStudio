package com.alekso.dltstudio.graphs.model

data class TimeFrame(
    val timeStart: Long,
    val timeEnd: Long,
) {
    val duration
        get() = timeEnd - timeStart

    val durationSec = ((timeEnd - timeStart) / 1_000_000L).toInt()

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
