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
    println("renderSeries(seriesCount: $seriesCount; padding: $verticalPadding)")
    (0..<seriesCount).forEach { i ->
        val y = calculateY(i.toFloat(), (seriesCount - 1).toFloat(), seriesCount, size.height, verticalPadding)
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y))
    }
}

internal fun DrawScope.renderSeries(
    seriesCount: Int,
    lineColor: Color,
    verticalPadding: Float,
) {
    println("renderSeries(seriesCount: $seriesCount; padding: $verticalPadding)")
    (0..<seriesCount).forEach { i ->
        val y = calculateY(i.toFloat(), (seriesCount - 1).toFloat(), seriesCount, size.height, verticalPadding)
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
        val maxValueResult = textMeasurer.measure(
            text = "$label$labelsPostfix", style = labelsTextStyle
        )
        maxHeight = maxValueResult.size.height
        if (maxValueResult.size.width > maxWidth) {
            maxWidth = maxValueResult.size.width
        }
    }

    labels.forEachIndexed { i, label ->
        val y = calculateY(i.toFloat(), (labels.size - 1).toFloat(), labels.size, size.height, verticalPadding)
        drawText(
            textMeasurer = textMeasurer,
            text = "$label$labelsPostfix",
            style = labelsTextStyle.copy(textAlign = TextAlign.Start),
            topLeft = Offset(3.dp.toPx(), y - maxHeight / 2f),
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = 1,
            size = Size(maxWidth.toFloat(), maxHeight.toFloat()),
        )
    }
}

internal fun DrawScope.renderEvents(
    entriesMap: EventsChartData,
    labelsSize: Int,
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.event)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
                labelIndex.toFloat(),
                (labels.size - 1).toFloat(),
                labels.size,
                size.height,
                verticalPadding
            )

            drawCircle(
                color = getColor(labelIndex),
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
        val lineColor = if (isHighlighted) style.highlightColor else getColor(keyIndex)
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
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
                val prevY = calculateY(
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
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor = if (isHighlighted) style.highlightColor else getColor(keyIndex)
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.newState)
            val oldLabelIndex = labels.indexOf(entry.oldState)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
                labelIndex.toFloat(),
                (labels.size - 1).toFloat(),
                labelsSize,
                size.height,
                verticalPaddingPx,
            )
            val prevY = calculateY(
                oldLabelIndex.toFloat(),
                (labels.size - 1).toFloat(),
                labelsSize,
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
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor = if (isHighlighted) style.highlightColor else getColor(keyIndex)
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        var oldLabelIndex = -1
        entries.forEachIndexed entriesIteration@{ i, entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.state)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
                labelIndex.toFloat(),
                (labels.size - 1).toFloat(),
                labelsSize,
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
                    oldLabelIndex.toFloat(),
                    (labels.size - 1).toFloat(),
                    labelsSize,
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
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor = if (isHighlighted) style.highlightColor else getColor(keyIndex)
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(key.key)
            val x1 = calculateX(entry, timeFrame, size.width)
            val x2 = calculateX(entry.copy(timestamp = entry.timestampEnd), timeFrame, size.width)
            val y = calculateY(
                labelIndex.toFloat(),
                (labels.size - 1).toFloat(),
                labelsSize,
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
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        val entries = entriesMap.getEntries(key)
        val isHighlighted = highlightedKey != null && highlightedKey == key
        val lineColor = if (isHighlighted) style.highlightColor else getColor(keyIndex)
        val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        entries.forEachIndexed entriesIteration@{ i, entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateY(
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
                val prevY = calculateY(
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


val colors = listOf(
    Color.Blue,
    Color.Red,
//    Color.Green, // is used as highlight color
    Color.Yellow,
    Color.White,
    Color.Cyan,
    Color.Magenta,
    Color.LightGray,
    Color.DarkGray,
    Color.Gray,
    Color.Black,
)

private fun getColor(index: Int): Color {
    // TODO: generate color or use colorPalette
    val i = if (index >= colors.size) colors.size % index else index
    return colors[i]
}
