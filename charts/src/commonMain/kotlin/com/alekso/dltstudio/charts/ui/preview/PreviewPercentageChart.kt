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
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.PercentageEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartStyle
import com.alekso.dltstudio.charts.ui.ChartType
import kotlinx.datetime.Clock

@Preview
@Composable
fun PreviewPercentageChart() {
    val now = Clock.System.now().toEpochMilliseconds() * 1000L
    val app1 = StringKey("app1")
    val app2 = StringKey("app2")
    val service1 = StringKey("service1")

    val chartData = PercentageChartData()
    chartData.addEntry(app1, PercentageEntry(now + 100_000L, 0f, ""))
    chartData.addEntry(app1, PercentageEntry(now + 200_000L, 20f, ""))
    chartData.addEntry(app1, PercentageEntry(now + 300_000L, 30f, ""))
    chartData.addEntry(app1, PercentageEntry(now + 400_000L, 4f, ""))
    chartData.addEntry(app1, PercentageEntry(now + 500_000L, 40f, ""))
    chartData.addEntry(app1, PercentageEntry(now + 600_000L, 100f, ""))
    chartData.addEntry(app2, PercentageEntry(now + 1_500_000L, 50f, ""))
    chartData.addEntry(app2, PercentageEntry(now + 1_750_000L, 100f, ""))
    chartData.addEntry(service1, PercentageEntry(now + 800_000L, 20f, ""))

    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Default,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            type = ChartType.Percentage,
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
            type = ChartType.Percentage,
            highlightedKey = app2,
        )
    }
}