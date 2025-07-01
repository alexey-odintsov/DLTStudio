package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.DurationEntry
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

internal fun <T> DrawScope.renderEvents(
    entriesMap: EventsChartData<T>,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
    selectedEntry: ChartEntry<T>?,
) {
    val verticalPadding = style.verticalPadding.toPx()

    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        renderEventsEntries(entriesMap, key, false, style, keyIndex, timeFrame, verticalPadding, selectedEntry)
    }
    if (highlightedKey != null) {
        val keyIndex = entriesMap.getKeys().indexOfFirst { it.key == highlightedKey.key }
        renderEventsEntries(
            entriesMap,
            highlightedKey,
            true,
            style,
            keyIndex,
            timeFrame,
            verticalPadding,
            selectedEntry
        )
    }
}

private fun <T> DrawScope.renderEventsEntries(
    entriesMap: EventsChartData<T>,
    key: ChartKey,
    isHighlighted: Boolean,
    style: ChartStyle,
    keyIndex: Int,
    timeFrame: TimeFrame,
    verticalPadding: Float,
    selectedEntry: ChartEntry<T>?
) {
    val entries = entriesMap.getEntries(key)
    entries.forEach { entry ->
        val labels = entriesMap.getLabels()
        val labelIndex = labels.indexOf(entry.event)
        val color = if (isHighlighted) style.highlightColor else ChartPalette.getColor(
            keyIndex,
            style.isDark
        )
        val x = calculateX(entry.timestamp, timeFrame, size.width)
        val y = calculateY(
            labelIndex,
            labels.size,
            size.height,
            verticalPadding
        )

        drawCircle(
            color = color,
            radius = 2.dp.toPx(),
            center = Offset(x, y)
        )
        if (entry == selectedEntry) {
            renderSelection(Offset(x, y))
        }
    }
}

internal fun <T> DrawScope.renderMinMaxLines(
    entriesMap: MinMaxChartData<T>,
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        renderMinMaxEntries(entriesMap, key, false, style, keyIndex, timeFrame, labelsSize)
    }
    if (highlightedKey != null) {
        val keyIndex = entriesMap.getKeys().indexOfFirst { it.key == highlightedKey.key }
        renderMinMaxEntries(entriesMap, highlightedKey, true, style, keyIndex, timeFrame, labelsSize)
    }
}

private fun <T> DrawScope.renderMinMaxEntries(
    entriesMap: MinMaxChartData<T>,
    key: ChartKey,
    isHighlighted: Boolean,
    style: ChartStyle,
    keyIndex: Int,
    timeFrame: TimeFrame,
    labelsSize: Int
) {
    val entries = entriesMap.getEntries(key)
    val lineColor =
        if (isHighlighted) style.highlightColor else ChartPalette.getColor(
            keyIndex,
            style.isDark
        )
    val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
    val verticalPaddingPx = style.verticalPadding.toPx()

    entries.forEachIndexed entriesIteration@{ i, entry ->
        val x = calculateX(entry.timestamp, timeFrame, size.width)
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
            val prevX = calculateX(prev.timestamp, timeFrame, size.width)
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

val dashPath = PathEffect.dashPathEffect(floatArrayOf(3f, 3f))

internal fun <T> DrawScope.renderStateLines(
    entriesMap: StateChartData<T>,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        renderStateEntries(entriesMap, key, false, style, keyIndex, timeFrame)
    }
    if (highlightedKey != null) {
        val keyIndex = entriesMap.getKeys().indexOfFirst { it.key == highlightedKey.key }
        renderStateEntries(entriesMap, highlightedKey, true, style, keyIndex, timeFrame)
    }
}

private fun <T> DrawScope.renderStateEntries(
    entriesMap: StateChartData<T>,
    key: ChartKey,
    isHighlighted: Boolean,
    style: ChartStyle,
    keyIndex: Int,
    timeFrame: TimeFrame
) {
    val entries = entriesMap.getEntries(key)
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
        val x = calculateX(entry.timestamp, timeFrame, size.width)
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
            val prevX = calculateX(prev.timestamp, timeFrame, size.width)

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

internal fun <T> DrawScope.renderSingleStateLines(
    entriesMap: SingleStateChartData<T>,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        renderSingleStateEntries(entriesMap, key, false, style, keyIndex, timeFrame)
    }
    if (highlightedKey != null) {
        val keyIndex = entriesMap.getKeys().indexOfFirst { it.key == highlightedKey.key }
        renderSingleStateEntries(entriesMap, highlightedKey, true, style, keyIndex, timeFrame)
    }
}

private fun <T> DrawScope.renderSingleStateEntries(
    entriesMap: SingleStateChartData<T>,
    key: ChartKey,
    isHighlighted: Boolean,
    style: ChartStyle,
    keyIndex: Int,
    timeFrame: TimeFrame
) {
    val entries = entriesMap.getEntries(key)
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
        val x = calculateX(entry.timestamp, timeFrame, size.width)
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
            val prevX = calculateX(prev.timestamp, timeFrame, size.width)
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

internal fun <T> DrawScope.renderDurationLines(
    entriesMap: DurationChartData<T>,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
) {
    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        renderDurationEntries(entriesMap, key, false, style, keyIndex, timeFrame)
    }
    if (highlightedKey != null) {
        val keyIndex = entriesMap.getKeys().indexOfFirst { it.key == highlightedKey.key }
        renderDurationEntries(entriesMap, highlightedKey, true, style, keyIndex, timeFrame)
    }
}

private fun <T> DrawScope.renderDurationEntries(
    entriesMap: DurationChartData<T>,
    key: ChartKey,
    isHighlighted: Boolean,
    style: ChartStyle,
    keyIndex: Int,
    timeFrame: TimeFrame
) {
    val entries = entriesMap.getEntries(key)
    val lineColor =
        if (isHighlighted) style.highlightColor else ChartPalette.getColor(
            keyIndex,
            style.isDark
        )
    val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 1f else style.lineWidth.toPx()
    val verticalPaddingPx = style.verticalPadding.toPx()

    var prev: DurationEntry<T>? = null
    entries.forEachIndexed entriesIteration@{ i, entry ->

        val labels = entriesMap.getLabels()
        val labelIndex = labels.indexOf(key.key)
        val x1 = calculateX(entry.timestamp, timeFrame, size.width)
        val x2 = if (prev != null) calculateX(prev!!.timestamp, timeFrame, size.width) else null
        val y = calculateY(
            labelIndex,
            labels.size,
            size.height,
            verticalPaddingPx,
        )

        if (entry.begin != null) {
            drawLine(
                lineColor,
                Offset(x1 - 3.dp.toPx(), y - 3.dp.toPx()),
                Offset(x1, y),
                strokeWidth = lineWidthPx,
            )
            drawLine(
                lineColor,
                Offset(x1, y),
                Offset(x1 - 3.dp.toPx(), y + 3.dp.toPx()),
                strokeWidth = lineWidthPx,
            )
        } else if (entry.end != null) {
            drawLine(
                lineColor,
                Offset(x1, y - 3.dp.toPx()),
                Offset(x1, y + 3.dp.toPx()),
                strokeWidth = lineWidthPx,
            )
        }

        if (x2 != null) {
            drawLine(
                lineColor,
                Offset(x1, y),
                Offset(x2, y),
                strokeWidth = lineWidthPx,
            )
        }
        prev = entry
    }
}

internal fun <T> DrawScope.renderPercentageLines(
    entriesMap: PercentageChartData<T>,
    labelsSize: Int,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
    selectedEntry: ChartEntry<T>?,
) {
    val verticalPaddingPx = style.verticalPadding.toPx()

    entriesMap.getKeys().forEachIndexed { keyIndex, key ->
        renderPercentageEntries(
            entriesMap,
            key,
            false,
            style,
            keyIndex,
            timeFrame,
            labelsSize,
            verticalPaddingPx,
            selectedEntry
        )
    }
    if (highlightedKey != null) {
        val keyIndex = entriesMap.getKeys().indexOfFirst { it.key == highlightedKey.key }
        renderPercentageEntries(
            entriesMap,
            highlightedKey,
            true,
            style,
            keyIndex,
            timeFrame,
            labelsSize,
            verticalPaddingPx,
            selectedEntry
        )
    }
}

private fun <T> DrawScope.renderPercentageEntries(
    entriesMap: PercentageChartData<T>,
    key: ChartKey,
    isHighlighted: Boolean,
    style: ChartStyle,
    keyIndex: Int,
    timeFrame: TimeFrame,
    labelsSize: Int,
    verticalPaddingPx: Float,
    selectedEntry: ChartEntry<T>?
) {
    val entries = entriesMap.getEntries(key)
    val lineColor =
        if (isHighlighted) style.highlightColor else ChartPalette.getColor(
            keyIndex,
            style.isDark
        )
    val lineWidthPx = if (isHighlighted) style.lineWidth.toPx() + 2f else style.lineWidth.toPx()

    entries.forEachIndexed entriesIteration@{ i, entry ->
        val x = calculateX(entry.timestamp, timeFrame, size.width)
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
            val prevX = calculateX(prev.timestamp, timeFrame, size.width)
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
        if (entry == selectedEntry) {
            renderSelection(Offset(x, y))
        }
    }
}

internal fun DrawScope.renderEmptyMessage(
    textMeasurer: TextMeasurer,
    style: ChartStyle
) {
    val messageLayout =
        textMeasurer.measure("No entries found", style = style.messageTextStyle)
    val textSize = messageLayout.size
    drawText(
        messageLayout,
        topLeft = Offset(
            x = size.width / 2 - textSize.width / 2,
            y = size.height / 2 - textSize.height / 2
        )
    )
}

internal fun DrawScope.renderSelection(offset: Offset) {
    drawCircle(
        color = Color.Green,
        radius = 4.dp.toPx(),
        center = offset,
        style = Stroke(width = 1.5.dp.toPx())
    )
}