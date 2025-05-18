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
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.NumericalValue
import com.alekso.dltstudio.graphs.model.PercentageValue
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
    entries: SnapshotStateMap<out Key, List<out Value>>?,
    onDragged: (Float) -> Unit,
    type: GraphType,
) {
    if (entries.isNullOrEmpty()) {
        Text("No entries found")
        return
    }
    var usSize by remember { mutableStateOf(1f) }
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

                    // center line
                    drawLine(
                        Color.LightGray,
                        Offset(size.center.x, 0f),
                        Offset(size.center.x, size.height),
                        alpha = 0.5f
                    )

                    // draw entries
                    when (type) {
                        GraphType.Events -> renderEvents(entries, timeFrame)
                        GraphType.Percentage, GraphType.MinMax -> renderLines(entries, timeFrame)
                        else -> {}
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

private fun DrawScope.renderEvents(
    entriesMap: Map<out Key, List<out Value>>,
    timeFrame: TimeFrame
) {
    entriesMap.keys.forEachIndexed { i, key ->
        val entries = entriesMap[key]
        entries?.forEach { entry ->
            val x = calculateX(entry, timeFrame)
            val y = when (entry) {
                is PercentageValue -> entry.value
                is NumericalValue -> entry.value.toFloat()
                else -> 1f
            }

            drawCircle(
                color = Color.Red,
                radius = 15f,
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.renderLines(entriesMap: Map<out Key, List<out Value>>, timeFrame: TimeFrame) {
    entriesMap.keys.forEachIndexed { i, key ->
        val entries = entriesMap[key]
        entries?.forEach { entry ->
            val x = calculateX(entry, timeFrame)
            val y = 100f

            drawCircle(
                color = Color.Green,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
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