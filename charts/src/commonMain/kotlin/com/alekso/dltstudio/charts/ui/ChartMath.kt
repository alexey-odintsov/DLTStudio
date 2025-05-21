package com.alekso.dltstudio.charts.ui

import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.TimeFrame

internal fun calculateX(
    entry: ChartEntry,
    timeFrame: TimeFrame,
    width: Float,
): Float {
    return ((entry.timestamp - timeFrame.timeStart) / timeFrame.duration.toFloat()) * width
}

internal fun calculateYForLabel(
    labels: List<String>,
    labelIndex: Int,
    height: Float,
    verticalPadding: Float
): Float {
    val itemHeight = (height - 2 * verticalPadding) / (labels.size - 1)
    return if (labels.size == 1) height / 2f else verticalPadding + itemHeight * labelIndex
}

internal fun calculateYForValue(
    value: Float,
    maxValue: Float,
    height: Float,
    verticalPadding: Float
): Float {
    val itemHeight = (height - 2 * verticalPadding)
    return verticalPadding + itemHeight - itemHeight * value / maxValue
}