package com.alekso.dltstudio.plugins.diagramtimeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.DurationEntry
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.MinMaxEntry
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.PercentageEntry
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.SingleStateEntry
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.StateEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartType
import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType

private val timeFrame = TimeFrame(0L, 5_000_000L)

object TimelinePreviewFactory {
    @Composable
    fun getPreview(diagramType: DiagramType, modifier: Modifier) {
        var chartData: ChartData?
        var chartType: ChartType?
        val key1 = StringKey("app1")
        val key2 = StringKey("app2")
        val key3 = StringKey("app3")

        when (diagramType) {
            DiagramType.Percentage -> {
                chartType = ChartType.Percentage
                chartData = PercentageChartData()
                chartData.addEntry(key1, PercentageEntry(1_000_000L, 20f))
                chartData.addEntry(key1, PercentageEntry(2_000_000L, 42f))
                chartData.addEntry(key1, PercentageEntry(3_000_000L, 25f))
                chartData.addEntry(key1, PercentageEntry(4_000_000L, 79f))
                chartData.addEntry(key1, PercentageEntry(5_000_000L, 59f))
                chartData.addEntry(key2, PercentageEntry(1_500_000L, 75f))
                chartData.addEntry(key2, PercentageEntry(2_500_000L, 70f))
                chartData.addEntry(key2, PercentageEntry(4_500_000L, 41f))
            }

            DiagramType.MinMaxValue -> {
                chartType = ChartType.MinMax
                chartData = MinMaxChartData()
                chartData.addEntry(key1, MinMaxEntry(1_000_000L, 480f))
                chartData.addEntry(key1, MinMaxEntry(2_000_000L, 680f))
                chartData.addEntry(key1, MinMaxEntry(3_000_000L, 550f))
                chartData.addEntry(key1, MinMaxEntry(4_000_000L, 920f))
                chartData.addEntry(key1, MinMaxEntry(5_000_000L, 960f))
                chartData.addEntry(key2, MinMaxEntry(1_000_000L, 150f))
                chartData.addEntry(key2, MinMaxEntry(2_000_000L, 200f))
                chartData.addEntry(key2, MinMaxEntry(3_000_000L, 450f))
                chartData.addEntry(key2, MinMaxEntry(4_000_000L, 550f))
            }

            DiagramType.State -> {
                chartType = ChartType.State
                chartData = StateChartData()
                chartData.addEntry(key1, StateEntry(1_000_000L, "a", "b"))
                chartData.addEntry(key1, StateEntry(2_000_000L, "b", "c"))
                chartData.addEntry(key1, StateEntry(3_000_000L, "c", "a"))
                chartData.addEntry(key1, StateEntry(4_000_000L, "a", "b"))
            }

            DiagramType.SingleState -> {
                chartType = ChartType.SingleState
                chartData = SingleStateChartData()
                chartData.addEntry(key1, SingleStateEntry(1_000_000L, "DRIVING"))
                chartData.addEntry(key1, SingleStateEntry(2_000_000L, "STOPPED"))
                chartData.addEntry(key1, SingleStateEntry(3_000_000L, "PARKED"))
                chartData.addEntry(key1, SingleStateEntry(4_000_000L, "OFF"))
            }

            DiagramType.Duration -> {
                chartType = ChartType.Duration
                chartData = DurationChartData()
                chartData.addEntry(key1, DurationEntry(1_000_000L, begin = "begin", end = null))
                chartData.addEntry(key1, DurationEntry(2_000_000L, begin = null, end = "end"))
                chartData.addEntry(key2, DurationEntry(3_000_000L, begin = "begin", end = null))
                chartData.addEntry(key2, DurationEntry(4_000_000L, begin = null, end = "end"))
            }

            DiagramType.Events -> {
                chartType = ChartType.Events
                chartData = EventsChartData()
                chartData.addEntry(key1, EventEntry(1_000_000L, "CRASH"))
                chartData.addEntry(key2, EventEntry(2_000_000L, "CRASH"))
                chartData.addEntry(key1, EventEntry(3_000_000L, "ANR"))
                chartData.addEntry(key3, EventEntry(4_000_000L, "WTF"))
            }
        }
        Chart(
            modifier = modifier,
            entries = chartData,
            totalTime = timeFrame,
            timeFrame = timeFrame,
            type = chartType,
        )
    }
}

@Preview
@Composable
fun PreviewDiagramPreviews() {
    Box {
        TimelinePreviewFactory.getPreview(
            DiagramType.MinMaxValue,
            Modifier.width(200.dp).height(200.dp)
        )
    }
}