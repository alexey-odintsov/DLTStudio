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
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.MinMaxEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartStyle
import com.alekso.dltstudio.charts.ui.ChartType
import kotlinx.datetime.Clock

@Preview
@Composable
fun PreviewMinMaxChart() {
    val now = Clock.System.now().toEpochMilliseconds() * 1000L
    val app1 = StringKey("app1")
    val app2 = StringKey("app2")
    val service1 = StringKey("service1")

    val chartData = MinMaxChartData<String>()
    chartData.addEntry(app1, MinMaxEntry(now + 100_000L,0f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 200_000L,100f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 300_000L,200f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 400_000L,300f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 500_000L,400f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 600_000L, 340f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 700_000L,600f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 800_000L, 300f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 900_000L,800f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 1_000_000L, 400f, ""))
    chartData.addEntry(app1, MinMaxEntry(now + 1_100_000L, 200f, ""))
    chartData.addEntry(app2, MinMaxEntry(now + 1_500_000L, 500f, ""))
    chartData.addEntry(app2, MinMaxEntry(now + 1_750_000L, 1000f, ""))
    chartData.addEntry(service1, MinMaxEntry(now + 800_000L, 200f, ""))

    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Default,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            labelsCount = 4,
            labelsPostfix = " Mb",
            type = ChartType.MinMax,
            highlightedKey = app2,
        )
        Spacer(Modifier.size(4.dp))
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Dark,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            labelsPostfix = " Mb",
            type = ChartType.MinMax,
            highlightedKey = app2,
        )
    }
}