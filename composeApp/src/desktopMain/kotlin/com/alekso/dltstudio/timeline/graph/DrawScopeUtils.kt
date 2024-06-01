package com.alekso.dltstudio.timeline.graph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.timeline.TimeFrame
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.TimeLineViewStyle


fun DrawScope.renderLines(
    viewStyle: TimeLineViewStyle,
    items: MutableList<TimeLineEntry<Float>>?,
    splitTimeSec: Float,
    timeFrame: TimeFrame,
    secSizePx: Float,
    height: Float,
    verticalPaddingPx: Float,
    maxValue: Float,
    color: Color,
    highlightedKey: String?,
    key: String
) {
    items?.forEachIndexed entriesIteration@{ i, entry ->
        if (i == 0) return@entriesIteration

        val prev = items[i - 1]
        val prevDiffSec = (entry.timestamp - prev.timestamp) / 1000000f
        // split lines if difference is too big
        if (prevDiffSec > splitTimeSec) {
            return@entriesIteration
        }
        val prevX = (prev.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx
        val prevY = verticalPaddingPx + height - height * prev.value / maxValue

        val curX = ((entry.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)
        val curY = verticalPaddingPx + height - height * entry.value / maxValue

        drawLine(
            color,
            Offset(timeFrame.offsetSeconds * secSizePx + prevX, prevY),
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curY),
            strokeWidth = if (highlightedKey != null && highlightedKey == key)
                viewStyle.highlightedLineWidth.toPx() else viewStyle.lineWidth.toPx()
        )
    }
}

fun DrawScope.renderStateLines(
    states: List<String>,
    items: MutableList<TimeLineStateEntry>?,
    splitTimeSec: Float,
    timeFrame: TimeFrame,
    secSizePx: Float,
    verticalPaddingPx: Float,
    color: Color,
    highlightedKey: String?,
    key: String,
    seriesCount: Int,
    availableHeight: Float,
) {
    val regularStroke = 2.dp.toPx()
    val highlightedStroke = 3.dp.toPx()

    items?.forEachIndexed entriesIteration@{ i, entry ->

        val prev = if (i > 0) items[i - 1] else null

        val prevX = if (prev != null) {
            ((prev.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)
        } else {
            0f
        }
        val curX = ((entry.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)

        val curOldY = calculateY(seriesCount, states.indexOf(entry.value.first), availableHeight)
        val curY = calculateY(seriesCount, states.indexOf(entry.value.second), availableHeight)

        // horizontal line
        if (prev != null) {
            val prevY = calculateY(seriesCount, states.indexOf(prev.value.second), availableHeight)
            drawLine(
                color,
                Offset(timeFrame.offsetSeconds * secSizePx + prevX, prevY + verticalPaddingPx),
                Offset(timeFrame.offsetSeconds * secSizePx + curX, curOldY+ verticalPaddingPx),
                strokeWidth = if (highlightedKey != null && highlightedKey == key) highlightedStroke else regularStroke
            )
        }
        // vertical line
        drawLine(
            color,
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curOldY+ verticalPaddingPx),
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curY+ verticalPaddingPx),
            strokeWidth = if (highlightedKey != null && highlightedKey == key) highlightedStroke else regularStroke
        )
    }
}

fun DrawScope.renderEvents(
    states: List<String>,
    items: MutableList<TimeLineEventEntry>?,
    timeFrame: TimeFrame,
    secSizePx: Float,
    verticalPaddingPx: Float,
    color: Color,
    highlightedKey: String?,
    key: String,
    seriesCount: Int,
    availableHeight: Float,
) {
    items?.forEachIndexed entriesIteration@{ i, entry ->
        val x = ((entry.timestamp - timeFrame.timestampStart) / 1_000_000f * secSizePx)
        val y = calculateY(seriesCount, states.indexOf(entry.value.event), availableHeight)

        drawCircle(
            color = color,
            radius = if (highlightedKey != null && highlightedKey == key) 4.dp.toPx() else 3.dp.toPx(),
            center = Offset(timeFrame.offsetSeconds * secSizePx + x, y + verticalPaddingPx),
        )
    }
}

fun DrawScope.renderSecondsVerticalLines(
    timeFrame: TimeFrame,
    secSizePx: Float,
    height: Float,
) {
    for (i in 0..timeFrame.getTotalSeconds()) {
        val x = timeFrame.offsetSeconds * secSizePx + i * secSizePx
        drawLine(Color.LightGray, Offset(x, 0f), Offset(x, height), alpha = 0.2f)
    }
}

fun DrawScope.renderVerticalSeries(
    seriesCount: Int,
    availableHeight: Float,
    verticalPaddingPx: Float,
    width: Float,
) {
    for (i in 0..<seriesCount) {
        val y = calculateY(seriesCount, i, availableHeight)
        drawLine(
            Color.LightGray,
            Offset(0f, y + verticalPaddingPx),
            Offset(width, y + verticalPaddingPx),
            alpha = 0.5f
        )
    }
}

fun DrawScope.renderLabels(
    minValue: Float,
    maxValue: Float,
    seriesCount: Int,
    availableHeight: Float,
    verticalPaddingPx: Float,
    textMeasurer: TextMeasurer,
    seriesPostfix: String,
    seriesTextStyle: TextStyle
) {
    val step = (maxValue - minValue) / (seriesCount - 1)
    val textHeight = seriesTextStyle.fontSize.toPx() + 2.dp.toPx()

    for (i in 0..<seriesCount) {
        val y = calculateY(seriesCount, i, size.height - verticalPaddingPx * 2)
        drawText(
            textMeasurer = textMeasurer,
            size = Size(200.dp.toPx(), textHeight),
            text = "${"%.0f".format(maxValue - (i * step))}$seriesPostfix",
            topLeft = Offset(
                3.dp.toPx(),
                y + textHeight / 2f
            ),
            style = seriesTextStyle,
        )
    }
}

fun DrawScope.renderStateLabels(
    states: List<String>,
    seriesCount: Int,
    verticalPaddingPx: Float,
    textMeasurer: TextMeasurer,
    seriesTextStyle: TextStyle,
    availableHeight: Float,
) {
    for (i in 0..<seriesCount) {
        val y = calculateY(seriesCount, i, availableHeight)
        drawText(
            textMeasurer,
            size = Size(LABEL_WIDTH.toPx(), LABEL_HEIGHT.toPx()),
            text = states[i],
            topLeft = Offset(3.dp.toPx(), y - LABEL_HALF_HEIGHT.toPx() + verticalPaddingPx),
            style = seriesTextStyle
        )
    }
}

private var LABEL_WIDTH = 300.dp
private var LABEL_HEIGHT = 12.dp
private var LABEL_HALF_HEIGHT = LABEL_HEIGHT / 2f
private var MAX_ITEM_HEIGHT_RATIO = 0.3f

fun calculateY(seriesCount: Int, i: Int, availableHeight: Float): Float {
    var itemHeight = availableHeight / (seriesCount - 1)
    var additionalPadding = 0f

    if (itemHeight > availableHeight * MAX_ITEM_HEIGHT_RATIO) {
        itemHeight = availableHeight * MAX_ITEM_HEIGHT_RATIO
        additionalPadding = (availableHeight - ((seriesCount - 1) * itemHeight)) / 2f
    }
    return if (seriesCount == 1) availableHeight / 2f else itemHeight * i + additionalPadding
}