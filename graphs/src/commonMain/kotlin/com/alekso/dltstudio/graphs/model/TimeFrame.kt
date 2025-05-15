package com.alekso.dltstudio.graphs.model

data class TimeFrame(
    val timeStart: Long,
    val timeEnd: Long,
) {
    val duration
        get() = timeEnd - timeStart

    fun move(dx: Long): TimeFrame {
        return copy(timeStart = timeStart + dx, timeEnd = timeEnd + dx)
    }
}
