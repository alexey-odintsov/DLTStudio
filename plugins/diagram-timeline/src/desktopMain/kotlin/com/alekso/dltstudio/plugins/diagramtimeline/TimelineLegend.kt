package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.MinMaxEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.ui.ChartPalette
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.uicomponents.Tooltip
import kotlinx.datetime.Clock

@Composable
fun TimelineLegend(
    modifier: Modifier,
    title: String,
    entries: ChartData<LogMessage>? = null,
    updateHighlightedKey: (ChartKey?) -> Unit,
    highlightedKey: ChartKey? = null,
) {
    val state = rememberLazyListState()
    val keys = entries?.getKeys()
    val keysCount = keys?.size ?: 0

    Box(modifier = modifier.padding(start = 4.dp, end = 4.dp)) {
        Column(Modifier.fillMaxSize()) {
            Text(
                title,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(bottom = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (keysCount > 0) {
                LazyColumn(Modifier, state) {

                    items(keysCount) { i ->
                        val key = keys?.get(i)
                        Row(
                            Modifier.selectable(
                                selected = highlightedKey == key,
                                onClick = { updateHighlightedKey(if (highlightedKey != key) key else null) })
                        ) {
                            Box(
                                modifier = Modifier.width(30.dp).height(6.dp).padding(end = 4.dp)
                                    .align(Alignment.CenterVertically)
                                    .background(ChartPalette.getColor(i))
                            )
                            Tooltip(text = key?.key ?: "") {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = key?.key ?: "",
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    fontWeight = FontWeight(if (highlightedKey == key) 600 else 400)
                                )
                            }
                        }
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Preview
@Composable
fun PreviewTimeLineLegend() {
    val ts = Clock.System.now().toEpochMilliseconds() * 1000L

    val entries = EventsChartData<LogMessage>()
    entries.addEntry(StringKey("app1"), EventEntry(ts, "ANR", null))
    entries.addEntry(StringKey("app2"), EventEntry(ts, "ANR", null))
    entries.addEntry(StringKey("app3"), EventEntry(ts, "ANR", null))
    entries.addEntry(StringKey("mysuperservice1: 12345"), EventEntry(ts, "ANR", null))
    entries.addEntry(StringKey("mysuperservice2: 12345"), EventEntry(ts, "ANR", null))

    val minMax = MinMaxChartData<LogMessage>()
    minMax.addEntry(
        StringKey("app1"),
        MinMaxEntry(ts, 100f, null)
    )
    minMax.addEntry(
        StringKey("app2"),
        MinMaxEntry(ts, 100f, null)
    )
    minMax.addEntry(
        StringKey("mysuperservice: 12345"),
        MinMaxEntry(ts, 100f, null)
    )

    Column(Modifier.fillMaxWidth()) {
        TimelineLegend(
            modifier = Modifier.size(200.dp, 100.dp).background(Color.Gray),
            title = "Crashes",
            entries = entries,
            updateHighlightedKey = {},
            highlightedKey = null
        )
        Spacer(Modifier.height(4.dp))

        TimelineLegend(
            modifier = Modifier.size(250.dp, 200.dp).background(Color.LightGray),
            title = "Memory usage",
            entries = minMax,
            updateHighlightedKey = {},
            highlightedKey = null
        )
    }
}