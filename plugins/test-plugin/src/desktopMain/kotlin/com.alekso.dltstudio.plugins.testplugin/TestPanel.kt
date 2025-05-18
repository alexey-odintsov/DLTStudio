package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.TimeFrame
import com.alekso.dltstudio.graphs.model.Value
import com.alekso.dltstudio.graphs.ui.Graph
import com.alekso.dltstudio.graphs.ui.GraphType

@Composable
fun TestPanel(
    modifier: Modifier,
    entries: SnapshotStateMap<Diagram, SnapshotStateMap<out Key, List<out Value>>>,
    onAnaliseClicked: () -> Unit,
    onDragged: (Float) -> Unit,
    timeFrame: TimeFrame,
    totalFrame: TimeFrame,
    onZoom: (Boolean) -> Unit,
    onFit: () -> Unit,
) {
    Column(modifier = modifier.padding(4.dp)) {
        Text("Test plugin")
        Button(onClick = onAnaliseClicked) {
            Text("Get events")
        }

        Row {
            Button(onClick = { onZoom(true) }) {
                Text("+")
            }
            Button(onClick = { onZoom(false) }) {
                Text("-")
            }
            Button(onClick = { onFit() }) {
                Text("Fit")
            }
        }

        val formatter = LocalFormatter.current
        Text(
            "${formatter.formatDateTime(timeFrame.timeStart)} .. ${
                formatter.formatDateTime(
                    timeFrame.timeEnd
                )
            }"
        )
        Column(Modifier.fillMaxSize().background(Color.LightGray)) {
            entries.keys.forEach { diagram ->
                Graph(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    backgroundColor = Color.White,
                    totalTime = totalFrame,
                    timeFrame = timeFrame,
                    entries = entries[diagram],
                    onDragged = onDragged,
                    type = diagram.graphType,
                )
                Spacer(Modifier.size(4.dp))
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewTestPanel() {
//    TestPanel(
//        modifier = Modifier.fillMaxSize(),
//        entries = mutableStateMapOf(
//            Key("a") to Entry(200L, Color.Green),
//            Key("b") to Entry(160L, Color.Red),
//            Key("b") to Entry(280L, Color.Blue),
//        ),
//        onAnaliseClicked = {},
//        onDragged = {},
//        timeFrame = TimeFrame(0L, 480L),
//        totalFrame = TimeFrame(140L, 300L),
//        onZoom = {},
//        onFit = {},
//    )
//}