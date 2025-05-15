package com.alekso.dltstudio.graphs.model.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.graphs.model.Entry
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.TimeFrame

@Composable
fun LineGraph(
    modifier: Modifier,
    backgroundColor: Color,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: SnapshotStateMap<Key<*>, Entry<*>>
) {
    Spacer(modifier = modifier.fillMaxSize().background(backgroundColor).clipToBounds().drawWithCache {
        onDrawBehind {
            entries.keys.forEachIndexed { i, key ->
                val entry = entries[key]
                if (entry != null) {
                    renderEvents(entry, timeFrame)
                }
            }
        }
    })
}

fun DrawScope.renderEvents(entry: Entry<*>, timeFrame: TimeFrame) {
    val x = ((entry.timestamp - timeFrame.timeStart) / timeFrame.duration.toFloat()) * size.width
    val y = 100f

    drawCircle(
        color = entry.value as Color,
        radius = 25f,
        center = Offset(x, y)
    )
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
        LineGraph(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            backgroundColor = Color.White,
            totalTime = TimeFrame(0L, 480L),
            timeFrame = TimeFrame(140L, 300L),
            entries = entries,
        )
    }
}