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
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.SingleStateEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartStyle
import com.alekso.dltstudio.charts.ui.ChartType
import kotlinx.datetime.Clock

@Preview
@Composable
fun PreviewSingleStateChart() {
    val now = Clock.System.now().toEpochMilliseconds() * 1000L
    val app1 = StringKey("app1")
    val app2 = StringKey("app2")
    val service1 = StringKey("service1")

    val chartData = SingleStateChartData<String>()
    chartData.addEntry(app1, SingleStateEntry(now + 500_000L, "ON_CREATE", ""))
    chartData.addEntry(app1, SingleStateEntry(now + 700_000L, "ON_START", ""))
    chartData.addEntry(app2, SingleStateEntry(now + 900_000L, "ON_CREATE", ""))
    chartData.addEntry(app1, SingleStateEntry(now + 1_200_000L, "ON_RESUME", ""))
    chartData.addEntry(app2, SingleStateEntry(now + 1_400_000L, "ON_START", ""))
    chartData.addEntry(app1, SingleStateEntry(now + 1_600_000L, "ON_PAUSE", ""))
    chartData.addEntry(service1, SingleStateEntry(now + 1_800_000L, "ON_CREATE", ""))

    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Default,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            labelsCount = 4,
            type = ChartType.SingleState,
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
            type = ChartType.SingleState,
            highlightedKey = app2,
        )
    }
}