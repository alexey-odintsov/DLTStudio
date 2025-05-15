package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.graphs.model.Entry
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.TimeFrame
import com.alekso.dltstudio.graphs.model.ui.LineGraph

@Composable
fun TestPanel(
    modifier: Modifier,
    entries: SnapshotStateMap<Key<*>, Entry<*>>,
    onAnaliseClicked: () -> Unit,
) {
    Column(modifier = modifier.padding(4.dp)) {
        Text("Test plugin")
        Button(onClick = onAnaliseClicked) {
            Text("Get events")
        }

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
}

@Preview
@Composable
fun PreviewTestPanel() {
    TestPanel(
        modifier = Modifier.fillMaxSize(),
        entries = mutableStateMapOf(
            Key("a") to Entry(200L, Color.Green),
            Key("b") to Entry(160L, Color.Red),
            Key("b") to Entry(280L, Color.Blue),
        ),
        onAnaliseClicked = {}
    )
}