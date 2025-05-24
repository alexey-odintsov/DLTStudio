package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.TimeFrame


internal fun DrawScope.renderSeriesByValue(
    seriesCount: Int,
    lineColor: Color,
    verticalPadding: Float,
) {
    (0..<seriesCount).forEach { i ->
        val y = calculateYForValue(
            i.toFloat(),
            (seriesCount - 1).toFloat(),
            seriesCount,
            size.height,
            verticalPadding
        )
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y))
    }
}

internal fun DrawScope.renderSeries(
    seriesCount: Int,
    lineColor: Color,
    verticalPadding: Float,
) {
    (0..<seriesCount).forEach { i ->
        val y = calculateY(i, seriesCount, size.height, verticalPadding)
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y))
    }
}

internal fun DrawScope.renderLabels(
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
    labelsPostfix: String,
    verticalPadding: Float,
) {
    var maxWidth = 0
    var maxHeight = 0
    labels.forEachIndexed { i, label ->
        val maxValueResult =
            textMeasurer.measure(text = "$label$labelsPostfix", style = labelsTextStyle)
        maxHeight = maxValueResult.size.height
        if (maxValueResult.size.width > maxWidth) {
            maxWidth = maxValueResult.size.width
        }
    }

    val labelSize = Size(maxWidth.toFloat(), maxHeight.toFloat())

    labels.forEachIndexed { i, label ->
        val y = calculateY(i, labels.size, size.height, verticalPadding)
        drawText(
            textMeasurer = textMeasurer,
            text = "$label$labelsPostfix",
            style = labelsTextStyle,
            topLeft = Offset(3.dp.toPx(), y - maxHeight / 2f),
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = 1,
            size = labelSize,
        )
    }
}

internal fun DrawScope.renderLabelsForValue(
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
    labelsPostfix: String,
    verticalPadding: Float,
) {
    val style = labelsTextStyle.copy(textAlign = TextAlign.End)

    var maxWidth = 0
    var maxHeight = 0
    labels.forEachIndexed { i, label ->
        val maxValueResult =
            textMeasurer.measure(text = "$label$labelsPostfix", style = labelsTextStyle)
        maxHeight = maxValueResult.size.height
        if (maxValueResult.size.width > maxWidth) {
            maxWidth = maxValueResult.size.width
        }
    }

    val labelSize = Size(maxWidth.toFloat(), maxHeight.toFloat())

    labels.forEachIndexed { i, label ->
        val y = calculateYForValue(
            i.toFloat(),
            (labels.size - 1).toFloat(),
            labels.size,
            size.height,
            verticalPadding
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "$label$labelsPostfix",
            style = style,
            topLeft = Offset(3.dp.toPx(), y - maxHeight / 2f),
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = 1,
            size = labelSize,
        )
    }
}

internal fun DrawScope.renderEvents(
    entriesMap: EventsChartData,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    val verticalPadding = style.verticalPadding.toPx()

    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val labels = entriesMap.getLabels()
            val isHighlighted = highlightedKey != null && highlightedKey == key
            val labelIndex = labels.indexOf(entry.event)
            val color = if (isHighlighted) style.highlightColor else ChartPalette.getColor(
                    keyIndex,
                    style.isDark
                )
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
                labelIndex,
                labels.size,
                size.height,
                verticalPadding
            )

            drawCircle(
                color = color,
                radius = 10f,
                center = Offset(x, y)
            )
        }
    }
}

internal fun DrawScope.renderMinMaxLines(
    entriesMap: MinMaxChartData,
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor =
            if (isHighlighted) style.highlightColor else ChartPalette.getColor(
                keyIndex,
                style.isDark
            )
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYForValue(
                entry.value,
                entriesMap.getMaxValue(),
                labelsSize,
                size.height,
                verticalPaddingPx,
            )

            if (i == 0 || entries.size == 1) {
                drawCircle(
                    color = lineColor,
                    radius = lineWidthPx,
                    center = Offset(x, y)
                )
                return@entriesIteration
            } else {
                val prev = entries[i - 1]
                val prevX = calculateX(prev, timeFrame, size.width)
                val prevY = calculateYForValue(
                    prev.value,
                    entriesMap.getMaxValue(),
                    labelsSize,
                    size.height,
                    verticalPaddingPx,
                )

                drawLine(
                    lineColor,
                    Offset(prevX, prevY),
                    Offset(x, y),
                    strokeWidth = lineWidthPx,
                )
            }
        }
    }
}

val dashPath = PathEffect.dashPathEffect(floatArrayOf(3f, 3f))

internal fun DrawScope.renderStateLines(
    entriesMap: StateChartData,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor =
            if (isHighlighted) style.highlightColor else ChartPalette.getColor(
                keyIndex,
                style.isDark
            )
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.newState)
            val oldLabelIndex = labels.indexOf(entry.oldState)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
                labelIndex,
                labels.size,
                size.height,
                verticalPaddingPx,
            )
            val prevY = calculateY(
                oldLabelIndex,
                labels.size,
                size.height,
                verticalPaddingPx,
            )

            if (i == 0 || entries.size == 1) {
                drawLine(
                    lineColor,
                    Offset(x, prevY),
                    Offset(x, y),
                    strokeWidth = lineWidthPx,
                    pathEffect = dashPath,
                )
                return@entriesIteration
            } else {
                val prev = entries[i - 1]
                val prevX = calculateX(prev, timeFrame, size.width)

                drawLine(
                    lineColor,
                    Offset(prevX, prevY),
                    Offset(x, prevY),
                    strokeWidth = lineWidthPx,
                )
                drawLine(
                    lineColor,
                    Offset(x, prevY),
                    Offset(x, y),
                    strokeWidth = lineWidthPx,
                    pathEffect = dashPath,
                )
            }
        }
    }
}

internal fun DrawScope.renderSingleStateLines(
    entriesMap: SingleStateChartData,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor =
            if (isHighlighted) style.highlightColor else ChartPalette.getColor(
                keyIndex,
                style.isDark
            )
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        var oldLabelIndex = -1
        entries.forEachIndexed entriesIteration@{ i, entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.state)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
                labelIndex,
                labels.size,
                size.height,
                verticalPaddingPx,
            )

            if (i == 0) {
                drawCircle(
                    color = lineColor,
                    radius = lineWidthPx,
                    center = Offset(x, y)
                )
            } else {
                val prev = entries[i - 1]
                val prevX = calculateX(prev, timeFrame, size.width)
                val prevY = calculateY(
                    oldLabelIndex,
                    labels.size,
                    size.height,
                    verticalPaddingPx,
                )

                drawLine(
                    lineColor,
                    Offset(prevX, prevY),
                    Offset(x, prevY),
                    strokeWidth = lineWidthPx,
                )
                drawLine(
                    lineColor,
                    Offset(x, prevY),
                    Offset(x, y),
                    strokeWidth = lineWidthPx,
                    pathEffect = dashPath,
                )
            }
            oldLabelIndex = labelIndex
        }
    }
}

internal fun DrawScope.renderDurationLines(
    entriesMap: DurationChartData,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor =
            if (isHighlighted) style.highlightColor else ChartPalette.getColor(
                keyIndex,
                style.isDark
            )
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(key.key)
            val x1 = calculateX(entry, timeFrame, size.width)
            val x2 = calculateX(entry.copy(timestamp = entry.timestampEnd), timeFrame, size.width)
            val y = calculateY(
                labelIndex,
                labels.size,
                size.height,
                verticalPaddingPx,
            )

            drawCircle(
                color = lineColor,
                radius = lineWidthPx,
                center = Offset(x1, y)
            )

            drawCircle(
                color = lineColor,
                radius = lineWidthPx,
                center = Offset(x2, y)
            )

            drawLine(
                lineColor,
                Offset(x1, y),
                Offset(x2, y),
                strokeWidth = lineWidthPx,
            )
        }
    }
}

internal fun DrawScope.renderPercentageLines(
    entriesMap: PercentageChartData,
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    val verticalPaddingPx = style.verticalPadding.toPx()

    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor =
            if (isHighlighted) style.highlightColor else ChartPalette.getColor(
                keyIndex,
                style.isDark
            )
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYForValue(
                entry.value,
                entriesMap.getMaxValue(),
                labelsSize,
                size.height,
                verticalPaddingPx,
            )

            if (i == 0 || entries.size == 1) {
                drawCircle(
                    color = lineColor,
                    radius = lineWidthPx,
                    center = Offset(x, y)
                )
                return@entriesIteration
            } else {
                val prev = entries[i - 1]
                val prevX = calculateX(prev, timeFrame, size.width)
                val prevY = calculateYForValue(
                    prev.value,
                    entriesMap.getMaxValue(),
                    labelsSize,
                    size.height,
                    verticalPaddingPx,
                )

                drawLine(
                    lineColor,
                    Offset(prevX, prevY),
                    Offset(x, y),
                    strokeWidth = lineWidthPx,
                )
            }
        }
    }
}

