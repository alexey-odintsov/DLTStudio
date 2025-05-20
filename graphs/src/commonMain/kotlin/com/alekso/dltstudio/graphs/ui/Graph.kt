package com.alekso.dltstudio.graphs.ui

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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.graphs.model.ChartData
import com.alekso.dltstudio.graphs.model.EventsChartData
import com.alekso.dltstudio.graphs.model.FloatChartData
import com.alekso.dltstudio.graphs.model.TimeFrame
import com.alekso.dltstudio.graphs.model.Value

enum class GraphType {
    Percentage,
    MinMax,
    Events,
    State,
    SingleState,
    Duration,
}

@Composable
fun Graph(
    modifier: Modifier,
    backgroundColor: Color,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData?,
    onDragged: (Float) -> Unit,
    type: GraphType,
    labelsCount: Int = 10,
    labelsPostfix: String = "",
) {
    if (entries == null || entries.isEmpty()) {
        Text("No entries found")
        return
    }
    var usSize by remember { mutableStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()
    val labelsTextStyle = remember {
        TextStyle(
            color = Color.Black,
            fontSize = 10.sp,
            textAlign = TextAlign.End,
            lineHeightStyle = LineHeightStyle(
                LineHeightStyle.Alignment.Center,
                LineHeightStyle.Trim.None
            )
        )
    }


    Spacer(
        modifier = modifier.fillMaxSize().background(backgroundColor).clipToBounds()
            .onSizeChanged { size ->
                usSize = size.width.toFloat() / timeFrame.duration
            }
            .pointerInput("graph-dragging") {
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
                        GraphType.Events -> renderEvents(entries as EventsChartData, timeFrame)
                        GraphType.Percentage, GraphType.MinMax -> renderLines(
                            entries as FloatChartData,
                            timeFrame
                        )
                        else -> {}
                    }

                    when (type) {
                        GraphType.Percentage -> renderValuesLabels(
                            0f,
                            100f,
                            labelsCount,
                            textMeasurer,
                            labelsTextStyle,
                            labelsPostfix,
                        )

                        GraphType.MinMax -> renderValuesLabels(
                            (entries as FloatChartData).getMinValue(),
                            (entries as FloatChartData).getMaxValue(),
                            labelsCount,
                            textMeasurer,
                            labelsTextStyle,
                            labelsPostfix,
                        )

                        GraphType.Events, GraphType.State, GraphType.SingleState, GraphType.Duration -> renderLabels(
                            entries.getLabels(),
                            textMeasurer,
                            labelsTextStyle
                        )
                    }
                }
            })
}

private fun DrawScope.calculateX(
    entry: Value,
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
            val x = calculateX(entry, timeFrame)
            val y = calculateYForLabels(
                entriesMap.getLabels(),
                entriesMap.getLabels().indexOf(entry.event),
                size.height
            )

            drawCircle(
                color = Color.Red,
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
                color = Color.Green,
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

//@Preview
//@Composable
//fun PreviewLineGraph() {
//    val now = Clock.System.now().toEpochMilliseconds() * 1000L
//    val key1 = CPUKey("cpu0")
//    val key2 = CPUKey("cpu1")
//    val key3 = CPUKey("cpu2")
//    val entries = mutableStateMapOf(
//        key1 to listOf(CPUEvent(Message(now, "cpu0: 40%"), 40f)),
//        key2 to listOf(CPUEvent(Message(now + 1_000_000L, "cpu1: 12%"), 12f)),
//        key3 to listOf(CPUEvent(Message(now + 2_000_000L, "cpu2: 43%"), 43f)),
//    )
//    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
//        Graph(
//            modifier = Modifier.fillMaxWidth().height(200.dp),
//            backgroundColor = Color.White,
//            totalTime = TimeFrame(now, now + 2_000_000L),
//            timeFrame = TimeFrame(now, now + 2_000_000L),
//            entries = entries,
//            onDragged = {},
//            type = GraphType.Events,
//        )
//        Spacer(Modifier.size(4.dp))
//        Graph(
//            modifier = Modifier.fillMaxWidth().height(200.dp),
//            backgroundColor = Color.Gray,
//            totalTime = TimeFrame(0L, 480L),
//            timeFrame = TimeFrame(140L, 300L),
//            entries = entries,
//            onDragged = {},
//            type = GraphType.Lines,
//        )
//    }
//}