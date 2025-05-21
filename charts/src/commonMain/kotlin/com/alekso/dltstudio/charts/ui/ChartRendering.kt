package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.TimeFrame

enum class RenderType {
    ByValue,
    BySeries,
}

internal fun DrawScope.renderSeriesByValue(
    seriesCount: Int,
    lineColor: Color,
    verticalPadding: Float,
) {
    println("renderSeries(seriesCount: $seriesCount; padding: $verticalPadding)")
    (0..<seriesCount).forEach { i ->
        val y = calculateYByValue(i.toFloat(), (seriesCount - 1).toFloat(), size.height, verticalPadding)
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
        val y = calculateYBySeries(i.toFloat(), (seriesCount - 1).toFloat(), seriesCount, size.height, verticalPadding)
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y))
    }
}

internal fun DrawScope.renderLabels(
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
    labelsPostfix: String,
    verticalPadding: Float,
    renderType: RenderType,
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
        val y = when(renderType) {
            RenderType.ByValue -> calculateYByValue(i.toFloat(), (labels.size - 1).toFloat(), size.height, verticalPadding)
            RenderType.BySeries -> calculateYBySeries(i.toFloat(), (labels.size - 1).toFloat(), labels.size, size.height, verticalPadding)
        }
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
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.event)
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYBySeries(
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
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYByValue(entry.value, entriesMap.getMaxValue(), size.height, verticalPadding)

            drawCircle(
                color = getColor(i),
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

internal fun DrawScope.renderPercentageLines(
    entriesMap: PercentageChartData,
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val x = calculateX(entry, timeFrame, size.width)
            val y = calculateYByValue(entry.value, entriesMap.getMaxValue(), size.height, verticalPadding)

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
