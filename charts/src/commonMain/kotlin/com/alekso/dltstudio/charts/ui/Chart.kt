package com.alekso.dltstudio.charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.FloatChartData
import com.alekso.dltstudio.charts.model.TimeFrame

@Composable
fun Chart(
    modifier: Modifier,
    style: ChartStyle,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData?,
    onDragged: (Float) -> Unit,
    type: ChartType,
    labelsCount: Int = 11,
    labelsPostfix: String = "",
) {
    if (entries == null || entries.isEmpty()) {
        Text("No entries found")
        return
    }
    var usSize by remember { mutableStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = modifier.fillMaxSize().background(style.backgroundColor).clipToBounds()
            .onSizeChanged { size ->
                usSize = size.width.toFloat() / timeFrame.duration
            }
            .pointerInput("chart-dragging") {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dragUs = -dragAmount.x / usSize
                    onDragged(dragUs)
                }
            }
            .drawWithCache {
                onDrawBehind {

                    // center line – zoom marker
                    drawLine(
                        Color.LightGray,
                        Offset(size.center.x, 0f),
                        Offset(size.center.x, size.height),
                        alpha = 0.5f
                    )

                    val labelsSize = entries.getLabels().size
                    renderSeries(
                        if (labelsSize > 0) labelsSize else labelsCount,
                        style.seriesColor,
                        style.verticalPadding.toPx()
                    )

                    // draw entries
                    when (type) {
                        ChartType.Events -> renderEvents(
                            entries as EventsChartData,
                            timeFrame,
                            style.verticalPadding.toPx()
                        )
                        ChartType.Percentage, ChartType.MinMax -> renderLines(
                            entries as FloatChartData,
                            timeFrame,
                            style.verticalPadding.toPx()
                        )

                        else -> {}
                    }

                    when (type) {
                        ChartType.Percentage -> renderValuesLabels(
                            0f,
                            100f,
                            labelsCount,
                            textMeasurer,
                            style.labelTextStyle,
                            labelsPostfix,
                        )

                        ChartType.MinMax -> renderValuesLabels(
                            (entries as FloatChartData).getMinValue(),
                            (entries as FloatChartData).getMaxValue(),
                            labelsCount,
                            textMeasurer,
                            style.labelTextStyle,
                            labelsPostfix,
                        )

                        ChartType.Events, ChartType.State, ChartType.SingleState, ChartType.Duration -> renderLabels(
                            entries.getLabels(),
                            textMeasurer,
                            style.labelTextStyle
                        )
                    }
                }
            })
}

private fun DrawScope.calculateX(
    entry: ChartEntry,
    timeFrame: TimeFrame
): Float {
    return ((entry.timestamp - timeFrame.timeStart) / timeFrame.duration.toFloat()) * size.width
}

private fun DrawScope.renderSeries(
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

private fun DrawScope.renderLabels(
    labels: List<String>,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
) {
    val seriesDistance = size.height / labels.size
    labels.forEachIndexed { i, label ->
        val y = seriesDistance * i
        val maxValueResult = textMeasurer.measure(
            text = label, style = labelsTextStyle
        )
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(0f, y),
        )
    }
}

private fun DrawScope.renderValuesLabels(
    minValue: Float,
    maxValue: Float,
    seriesCount: Int,
    textMeasurer: TextMeasurer,
    labelsTextStyle: TextStyle,
    labelsPostfix: String,
) {
    val seriesDistance = size.height / (seriesCount )
    val step = (maxValue - minValue) / (seriesCount - 1)
    repeat(seriesCount + 1) { i ->
        val text = "${"%,.0f".format((seriesCount - 1 - i) * step)}$labelsPostfix"
        val y = seriesDistance * i
        val maxValueResult = textMeasurer.measure(
            text = text, style = labelsTextStyle
        )
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            topLeft = Offset(0f, y),
        )
    }
}


private fun DrawScope.renderEvents(
    entriesMap: EventsChartData,
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.event)
            val x = calculateX(entry, timeFrame)
            val y = calculateYForLabels(labels, labelIndex, size.height, verticalPadding)

            drawCircle(
                color = getColor(labelIndex),
                radius = 15f,
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.renderLines(
    entriesMap: FloatChartData,
    timeFrame: TimeFrame,
    verticalPadding: Float,
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val x = calculateX(entry, timeFrame)
            val y = calculateYForValue(entry.value, entriesMap.getMaxValue(), size.height, verticalPadding)

            drawCircle(
                color = getColor(i),
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

private fun calculateYForLabels(
    labels: List<String>,
    labelIndex: Int,
    height: Float,
    verticalPadding: Float
): Float {
    val itemHeight = (height - 2 * verticalPadding) / (labels.size - 1)
    return if (labels.size == 1) height / 2f else verticalPadding + itemHeight * labelIndex
}

private fun calculateYForValue(value: Float, maxValue: Float, height: Float, verticalPadding: Float): Float {
    val itemHeight = (height - 2 * verticalPadding)
    return verticalPadding + itemHeight - itemHeight * value / maxValue
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
