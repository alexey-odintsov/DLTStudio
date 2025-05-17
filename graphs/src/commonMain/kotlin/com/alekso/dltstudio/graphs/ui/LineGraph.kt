package com.alekso.dltstudio.graphs.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.graphs.model.Entry
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.TimeFrame

enum class GraphType {
    Events,
    Lines,
    State,
    Duration,
}

@Composable
fun Graph(
    modifier: Modifier,
    backgroundColor: Color,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: SnapshotStateMap<Key<*>, Entry<*>>,
    onDragged: (Float) -> Unit,
    type: GraphType,
) {
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
                        GraphType.Lines -> renderLines(entries, timeFrame)
                        else -> {}
                    }
                }
            })
}

private fun DrawScope.calculateX(entry: Entry<*>, timeFrame: TimeFrame): Float {
    return ((entry.timestamp - timeFrame.timeStart) / timeFrame.duration.toFloat()) * size.width
}

fun DrawScope.renderEvents(entries: Map<Key<*>, Entry<*>>, timeFrame: TimeFrame) {
    entries.keys.forEachIndexed { i, key ->
        val entry = entries[key]
        if (entry != null) {
            val x = calculateX(entry, timeFrame)
            val y = 100f

            drawCircle(
                color = entry.value as Color,
                radius = 15f,
                center = Offset(x, y)
            )
        }
    }
}

fun DrawScope.renderLines(entries: Map<Key<*>, Entry<*>>, timeFrame: TimeFrame) {
    entries.keys.forEachIndexed { i, key ->
        val entry = entries[key]
        if (entry != null) {
            val x = calculateX(entry, timeFrame)
            val y = 100f

            drawCircle(
                color = entry.value as Color,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

@Preview
@Composable
fun PreviewLineGraph() {
    val entries = mutableStateMapOf<Key<*>, Entry<*>>(
        Key("a") to Entry(200L, Color.Green),
        Key("b") to Entry(160L, Color.Red),
        Key("b") to Entry(280L, Color.Blue),
    )
    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Graph(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            backgroundColor = Color.White,
            totalTime = TimeFrame(0L, 480L),
            timeFrame = TimeFrame(140L, 300L),
            entries = entries,
            onDragged = {},
            type = GraphType.Events,
        )
        Spacer(Modifier.size(4.dp))
        Graph(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            backgroundColor = Color.Gray,
            totalTime = TimeFrame(0L, 480L),
            timeFrame = TimeFrame(140L, 300L),
            entries = entries,
            onDragged = {},
            type = GraphType.Lines,
        )
    }
}