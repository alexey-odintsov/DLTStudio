package com.alekso.dltstudio.timeline

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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

fun DrawScope.renderSecondsVerticalLines(
    timeFrame: TimeFrame,
    secSizePx: Float,
    availableHeight: Float
) {
    for (i in 0..timeFrame.getTotalSeconds()) {
        val x = timeFrame.offsetSeconds * secSizePx + i * secSizePx
        drawLine(Color.LightGray, Offset(x, 0f), Offset(x, availableHeight), alpha = 0.2f)
    }
}

fun DrawScope.renderVerticalSeries(
    seriesCount: Int,
    availableHeight: Float,
    verticalPaddingDp: Dp,
    width: Float,
) {
    // Render labels
    for (i in 0..seriesCount) {
        val y = availableHeight * i / seriesCount + verticalPaddingDp.toPx()
        drawLine(Color.LightGray, Offset(0f, y), Offset(width, y), alpha = 0.5f)
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
    val step = (maxValue - minValue) / seriesCount
    // Render labels
    for (i in 0..seriesCount) {
        val y = availableHeight * i / seriesCount + verticalPaddingPx
        drawText(
            textMeasurer = textMeasurer,
            size = Size(100.dp.toPx(), 12.dp.toPx()),
            text = "${"%.0f".format(maxValue - (i * step))}$seriesPostfix",
            topLeft = Offset(
                3.dp.toPx(),
                y - 6.sp.toPx()
            ),
            style = seriesTextStyle,
        )
    }
}