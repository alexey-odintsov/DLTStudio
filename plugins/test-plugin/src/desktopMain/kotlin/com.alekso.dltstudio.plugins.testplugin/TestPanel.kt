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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.graphs.model.ChartData
import com.alekso.dltstudio.graphs.model.TimeFrame
import com.alekso.dltstudio.graphs.ui.Chart

@Composable
fun TestPanel(
    modifier: Modifier,
    entries: SnapshotStateMap<ChartParameters, ChartData>,
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
            val listState = rememberLazyListState()
            LazyColumn(
                Modifier.fillMaxSize(),
                state = listState
            ) {
                itemsIndexed(
                    items = entries.keys.toList(),
                    key = { _, key -> key },
                    contentType = { _, _ -> ChartParameters::class }) { i, diagram ->
                    Chart(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        backgroundColor = Color.White,
                        totalTime = totalFrame,
                        timeFrame = timeFrame,
                        entries = entries[diagram],
                        onDragged = onDragged,
                        type = diagram.chartType,
                        labelsPostfix = diagram.labelsPostfix,
                        labelsCount = diagram.labelsCount,
                    )
                    Spacer(Modifier.size(4.dp))
                }
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