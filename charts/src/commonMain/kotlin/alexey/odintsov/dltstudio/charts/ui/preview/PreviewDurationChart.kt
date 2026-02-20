package alexey.odintsov.dltstudio.charts.ui.preview

import alexey.odintsov.dltstudio.charts.model.DurationChartData
import alexey.odintsov.dltstudio.charts.model.DurationEntry
import alexey.odintsov.dltstudio.charts.model.StringKey
import alexey.odintsov.dltstudio.charts.model.TimeFrame
import alexey.odintsov.dltstudio.charts.ui.Chart
import alexey.odintsov.dltstudio.charts.ui.ChartStyle
import alexey.odintsov.dltstudio.charts.ui.ChartType
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun PreviewDurationChart() {
    val now = System.currentTimeMillis() * 1000L
    val app1 = StringKey("app1")
    val app2 = StringKey("app2")
    val app3 = StringKey("app3")

    val chartData = DurationChartData<String>()
    chartData.addEntry(app1, DurationEntry(now + 500_000L, "start", end = null, ""))
    chartData.addEntry(app1, DurationEntry(now + 900_000L, null, end = "stop", ""))
    chartData.addEntry(app1, DurationEntry(now + 1_000_000L, null, end = "stop", ""))
    chartData.addEntry(app2, DurationEntry(now + 1_200_000L, begin = "start", end = null, ""))
    chartData.addEntry(app3, DurationEntry(now + 900_000L, begin = null, end = "stop", ""))

    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        Chart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            style = ChartStyle.Default,
            totalTime = TimeFrame(now, now + 2_000_000L),
            timeFrame = TimeFrame(now, now + 2_000_000L),
            entries = chartData,
            onDragged = {},
            labelsCount = 4,
            type = ChartType.Duration,
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
            type = ChartType.Duration,
            highlightedKey = app2,
        )
    }
}