package com.alekso.dltstudio.charts.ui

import com.alekso.dltstudio.charts.model.TimeFrame

internal fun getSteps(minValue: Float, maxValue: Float, seriesCount: Int): List<String> {
    val result = mutableListOf<String>()

    val step = (maxValue - minValue) / (seriesCount - 1)
    repeat(seriesCount) { i ->
        result.add("%,.0f".format(i * step))
    }

    return result
}

fun calculateX(
    timestamp: Long,
    timeFrame: TimeFrame,
    width: Float,
): Float {
    return ((timestamp - timeFrame.timeStart) / timeFrame.duration.toFloat()) * width
}

fun calculateTimestamp(
    x: Float,
    timeFrame: TimeFrame,
    width: Float,
): Long {
    return (x / width * timeFrame.duration).toLong() + timeFrame.timeStart
}

internal fun calculateYForValue(
    value: Float,
    maxValue: Float,
    seriesCount: Int,
    height: Float,
    verticalPadding: Float
): Float {
    if (seriesCount == 1) return height / 2f
    val availableHeight = height - 2 * verticalPadding
    return height - (value / maxValue) * availableHeight - verticalPadding
}

internal fun calculateY(
    index: Int,
    seriesCount: Int,
    height: Float,
    verticalPadding: Float
): Float {
    if (seriesCount == 1) return height / 2f
    val itemHeight = height / (seriesCount + 1).toFloat()
    return height - itemHeight * (index + 1)
}