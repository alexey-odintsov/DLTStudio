package com.alekso.dltstudio.plugins.testplugin

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
import com.alekso.dltstudio.charts.ui.ChartType

class EntriesParser {
    fun provideMockEntries(timeFrame: TimeFrame): Map<ChartParameters, ChartData> {
        val key1 = StringKey("app1")
        val key2 = StringKey("app2")
        val key3 = StringKey("service1")

        val entriesMap = mutableMapOf<ChartParameters, ChartData>()

        val percentage = PercentageChartData()
        percentage.addEntry(key1, PercentageEntry(timestamp = timeFrame.timeStart + 1_000_000L, data = Message(timeFrame.timeStart + 1_000_000L, "cpu0: 40%"), value = 0f))
        percentage.addEntry(key2, PercentageEntry(timestamp = timeFrame.timeStart + 1_050_000L, data = Message(timeFrame.timeStart + 1_050_000L, "cpu1: 25%"), value = 25f))
        percentage.addEntry(key3, PercentageEntry(timestamp = timeFrame.timeStart + 900_000L, data = Message(timeFrame.timeStart + 900_000L, "cpu2: 14%"), value = 50f))
        percentage.addEntry(key3, PercentageEntry(timestamp = timeFrame.timeStart + 1_200_000L, data = Message(timeFrame.timeStart + 1_200_000L, "cpu2: 8%"), value = 75f),)
        percentage.addEntry(key3, PercentageEntry(timestamp = timeFrame.timeStart + 1_800_000L, data = Message(timeFrame.timeStart + 1_200_000L, "cpu2: 8%"), value = 100f),)

        val event = EventsChartData()
        event.addEntry(key1, EventEntry(timestamp = timeFrame.timeStart + 500_000L, event = "CRASH", data = "Crash1"))
        event.addEntry(key2, EventEntry(timestamp = timeFrame.timeStart + 1_250_000L, event = "WTF", data = "Crash12"))
//        event.addEntry(key3, EventEntry(timestamp = timeFrame.timeStart + 1_120_000L, event = "ANR", data = "WTF1"))

        val singleState = SingleStateChartData()
        singleState.addEntry(key3, SingleStateEntry(timestamp = timeFrame.timeStart + 500_000L, state = "STARTING", data = ""))
        singleState.addEntry(key3, SingleStateEntry(timestamp = timeFrame.timeStart + 1_000_000L, state = "STARTED", data = ""))
        singleState.addEntry(key3, SingleStateEntry(timestamp = timeFrame.timeStart + 1_500_000L, state = "CLOSING", data = ""),)

        val state = StateChartData()
        state.addEntry(key3, StateEntry(timestamp = timeFrame.timeStart + 500_000L, oldState = "STARTING", newState = "STARTED", data = ""))
        state.addEntry(key3, StateEntry(timestamp = timeFrame.timeStart + 1_000_000L, oldState = "STARTED", newState = "CLOSING", data = ""))
        state.addEntry(key3, StateEntry(timestamp = timeFrame.timeStart + 1_500_000L, oldState = "CLOSING", newState = "CLOSED", data = ""),)

        val minmax = MinMaxChartData()
        minmax.addEntry(key1, MinMaxEntry(timestamp = timeFrame.timeStart + 1_000_000L, value = 836034f, data = ""))
        minmax.addEntry(key1, MinMaxEntry(timestamp = timeFrame.timeStart + 1_230_000L, value = 1023945f, data = ""))
        minmax.addEntry(key1, MinMaxEntry(timestamp = timeFrame.timeStart + 1_450_000L, value = 304345f, data = ""))
        minmax.addEntry(key2, MinMaxEntry(timestamp = timeFrame.timeStart + 850_000L, value = 123564f, data = ""))
        minmax.addEntry(key2, MinMaxEntry(timestamp = timeFrame.timeStart + 1_500_000L, value = 86304f, data = ""))
        minmax.addEntry(key3, MinMaxEntry(timestamp = timeFrame.timeStart + 1_900_000L, value = 34573f, data = ""))

        val duration = DurationChartData()
        duration.addEntry(key1, DurationEntry(timestamp = timeFrame.timeStart + 1_000_000L, begin = "start", end = null, data = ""))
        duration.addEntry(key1, DurationEntry(timestamp = timeFrame.timeStart + 1_400_000L, begin = null, end = "stop", data = ""))

        entriesMap[ChartParameters(0, "crashes", ChartType.Events)] = event
        entriesMap[ChartParameters(1, "cpuc", ChartType.Percentage)] = percentage
        entriesMap[ChartParameters(2, "userState", ChartType.SingleState)] = singleState
        entriesMap[ChartParameters(3, "state", ChartType.State)] = state
        entriesMap[ChartParameters(4, "memory", ChartType.MinMax)] = minmax
        entriesMap[ChartParameters(5, "duration", ChartType.Duration)] = duration

        return entriesMap
    }
}