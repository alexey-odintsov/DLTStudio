package com.alekso.dltstudio.charts.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.FloatChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import kotlinx.datetime.Clock

@Composable
fun Chart(
    modifier: Modifier,
    style: ChartStyle,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData?,
    onDragged: (Float) -> Unit,
    type: ChartType,
    labelsCount: Int = 10,
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
                    renderSeries(if (labelsSize > 0) labelsSize else labelsCount)

                    // draw entries
                    when (type) {
                        ChartType.Events -> renderEvents(entries as EventsChartData, timeFrame)
                        ChartType.Percentage, ChartType.MinMax -> renderLines(
                            entries as FloatChartData,
                            timeFrame
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
) {
    val seriesDistance = size.height / seriesCount
    (0..<seriesCount).forEach { i ->
        val y = seriesDistance * i
        drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y))
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
    val seriesDistance = size.height / seriesCount
    val step = (maxValue - minValue) / seriesCount
    repeat(seriesCount) { i ->
        val text = "${"%,.0f".format((seriesCount - i) * step)}$labelsPostfix"
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
) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val labels = entriesMap.getLabels()
            val labelIndex = labels.indexOf(entry.event)
            val x = calculateX(entry, timeFrame)
            val y = calculateYForLabels(labels, labelIndex, size.height)

            drawCircle(
                color = getColor(labelIndex),
                radius = 15f,
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.renderLines(entriesMap: FloatChartData, timeFrame: TimeFrame) {
    entriesMap.getKeys().forEachIndexed { i, key ->
        val entries = entriesMap.getEntries(key)
        entries.forEach { entry ->
            val x = calculateX(entry, timeFrame)
            val y = calculateYForValue(entry.value, entriesMap.getMaxValue(), size.height)

            drawCircle(
                color = getColor(i),
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

private fun calculateYForLabels(labels: List<String>, labelIndex: Int, height: Float): Float {
    val itemHeight = height / (labels.size - 1)
    return if (labels.size == 1) height / 2f else itemHeight * labelIndex
}

private fun calculateYForValue(value: Float, maxValue: Float, height: Float): Float {
    return height - height * value / maxValue
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

@Preview
@Composable
fun PreviewEventsGraph() {
    val now = Clock.System.now().toEpochMilliseconds() * 1000L
    val app1 = StringKey("app1")
    val app2 = StringKey("app2")
    val service1 = StringKey("service1")

    val chartData = EventsChartData()
    chartData.addEntry(app1, EventEntry(now + 500_000L, "Crash", ""))
    chartData.addEntry(app2, EventEntry(now + 1_500_000L, "ANR", ""))
    chartData.addEntry(app2, EventEntry(now + 1_750_000L, "Crash", ""))
    chartData.addEntry(service1, EventEntry(now + 800_000L, "WTF", ""))

    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Default,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            type = ChartType.Events,
        )
        Spacer(Modifier.size(4.dp))
    }
}