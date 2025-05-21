package com.alekso.dltstudio.charts.ui

import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.TimeFrame

internal fun getSteps(minValue: Float, maxValue: Float, seriesCount: Int): List<String> {
    val result = mutableListOf<String>()

    val step = (maxValue - minValue) / (seriesCount - 1)
    repeat(seriesCount) { i ->
        result.add("%,.0f".format(i * step))
    }

    return result
}

internal fun calculateX(
    entry: ChartEntry,
    timeFrame: TimeFrame,
    width: Float,
): Float {
    return ((entry.timestamp - timeFrame.timeStart) / timeFrame.duration.toFloat()) * width
}

internal fun calculateY(
    value: Float,
    maxValue: Float,
    height: Float,
    verticalPadding: Float
): Float {
    if (maxValue == 0f) return height / 2f
    val availableHeight = height - 2 * verticalPadding
    val y = height - (value / maxValue) * availableHeight - verticalPadding

    println("calculateY(value: $value; maxValue: $maxValue; height: $height; verticalPadding: $verticalPadding) = avail: $availableHeight; y: $y")
    return y
}
