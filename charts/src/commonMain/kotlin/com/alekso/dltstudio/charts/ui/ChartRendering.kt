package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.FloatChartData
import com.alekso.dltstudio.charts.model.TimeFrame


internal fun DrawScope.renderSeries(
    seriesCount: Int,
    lineColor: Color,
    verticalPadding: Float,
) {
    val seriesDistance = (size.height - 2 * verticalPadding) / (seriesCount - 1)
    (0..<seriesCount).forEach { i ->
        val y = verticalPadding + seriesDistance * i
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y))
    }
}

internal fun DrawScope.renderLabels(
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
    verticalPadding: Float,
) {
    labels.forEachIndexed { i, label ->
        val y = calculateYForLabel(labels, i, size.height, verticalPadding)
        val maxValueResult = textMeasurer.measure(
            text = label, style = labelsTextStyle
        )
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = labelsTextStyle,
            topLeft = Offset(0f, y - maxValueResult.size.height / 2f),
        )
    }
}

internal fun DrawScope.renderValuesLabels(
    minValue: Float,
    maxValue: Float,
    seriesCount: Int,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
    labelsPostfix: String,
    verticalPadding: Float,
) {
    val step = (maxValue - minValue) / (seriesCount - 1)
    repeat(seriesCount + 1) { i ->
        val text = "${"%,.0f".format((seriesCount - i) * step)}$labelsPostfix"
        val y = calculateYForValue(step * (seriesCount - i), maxValue, size.height, verticalPadding)
        val maxValueResult = textMeasurer.measure(
            text = text, style = labelsTextStyle
        )
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = labelsTextStyle,
            topLeft = Offset(0f, y - maxValueResult.size.height / 2f),
        )
    }
}


internal fun DrawScope.renderEvents(
    entriesMap: EventsChartData,
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.event)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYForLabel(labels, labelIndex, size.height, verticalPadding)

            drawCircle(
                color = getColor(labelIndex),
                radius = 15f,
                center = Offset(x, y)
            )
        }
    }
}

internal fun DrawScope.renderLines(
    entriesMap: FloatChartData,
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYForValue(entry.value, entriesMap.getMaxValue(), size.height, verticalPadding)

            drawCircle(
                color = getColor(i),
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}


val colors = listOf(
    Color.Blue,
    Color.Red,
    Color.Green,
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
