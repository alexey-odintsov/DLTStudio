package com.alekso.dltstudio.charts.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartStyle
import com.alekso.dltstudio.charts.ui.ChartType
import kotlinx.datetime.Clock

@Preview
@Composable
fun PreviewEventsChart() {
    val now = Clock.System.now().toEpochMilliseconds() * 1000L
    val app1 = StringKey("app1")
    val app2 = StringKey("app2")
    val service1 = StringKey("service1")

    val chartData = EventsChartData<String>()
    chartData.addEntry(app1, EventEntry(now + 500_000L, "Crash", ""))
    chartData.addEntry(app2, EventEntry(now + 1_000_000L, "ANR", ""))
    chartData.addEntry(app2, EventEntry(now + 1_500_000L, "Crash", ""))
    chartData.addEntry(service1, EventEntry(now + 2_000_000L, "WTF", ""))

    val chartData2 = EventsChartData<String>()
    chartData2.addEntry(app1, EventEntry(now + 500_000L, "Crash", ""))

    val chartData3 = EventsChartData<String>()
    chartData3.addEntry(app1, EventEntry(now + 500_000L, "Crash", ""))
    chartData3.addEntry(app2, EventEntry(now + 1_500_000L, "WTF", ""))

    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Default,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            type = ChartType.Events,
            highlightedKey = null,
        )
        Spacer(Modifier.size(4.dp))
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Dark,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData3,
            onDragged = {},
            type = ChartType.Events,
            highlightedKey = null,
        )
        Spacer(Modifier.size(4.dp))
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Dark,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData2,
            onDragged = {},
            type = ChartType.Events,
            highlightedKey = null,
        )
    }
}