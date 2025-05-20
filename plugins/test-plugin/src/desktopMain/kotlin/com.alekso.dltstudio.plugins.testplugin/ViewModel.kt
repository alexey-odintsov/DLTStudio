package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.FloatChartData
import com.alekso.dltstudio.charts.model.NumericalEntry
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.SingleStateEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.ChartType
import kotlinx.datetime.Clock

data class Message(
    val timestamp: Long,
    val message: String,
)

data class ChartParameters(
    val order: Int,
    val key: String,
    val chartType: ChartType,
    val labelsPostfix: String = "",
    val labelsCount: Int = 10,
)


class ViewModel {
    // State
    val entriesMap = mutableStateMapOf<ChartParameters, ChartData>()
    val totalTime by mutableStateOf(
        TimeFrame(
            Clock.System.now().toEpochMilliseconds() * 1000L,
            Clock.System.now().toEpochMilliseconds() * 1000L + 2_000_000L
        )
    )
    var timeFrame by mutableStateOf(TimeFrame(totalTime.timeStart, totalTime.timeEnd))

    // Public Interface
    fun onAnaliseClicked() {
        extractEvents()
    }
    fun onDragged(dx: Float) {
        timeFrame = timeFrame.move(dx.toLong())
    }

    fun onZoom(zoomIn: Boolean) {
        timeFrame = timeFrame.zoom(zoomIn)
    }

    fun onFit() {
        timeFrame = totalTime.copy()
    }

    // Business logic
    private fun extractEvents() {

        val cpuEntries = FloatChartData()
        cpuEntries.addEntry(
            StringKey("app1"),
                NumericalEntry(
                    timestamp = timeFrame.timeStart + 1_000_000L,
                    data = Message(timeFrame.timeStart + 1_000_000L, "cpu0: 40%"),
                    value = 40f
                )
        )
        cpuEntries.addEntry(
            StringKey("app2"),
                NumericalEntry(
                    timestamp = timeFrame.timeStart + 1_050_000L,
                    data = Message(timeFrame.timeStart + 1_050_000L, "cpu1: 25%"),
                    value = 25f
                )
        )
        val service1 = StringKey("service1")
        cpuEntries.addEntry(
            service1,
                NumericalEntry(
                    timestamp = timeFrame.timeStart + 900_000L,
                    data = Message(timeFrame.timeStart + 900_000L, "cpu2: 14%"),
                    value = 14f
                )
        )
        cpuEntries.addEntry(
            service1,
            NumericalEntry(
                    timestamp = timeFrame.timeStart + 1_200_000L,
                    data = Message(timeFrame.timeStart + 1_200_000L, "cpu2: 8%"),
                    value = 8f
                ),
        )


        val crashes = EventsChartData()
        val app1Key = StringKey("app1")
        val service1Key = StringKey("service1")
        crashes.addEntry(
            app1Key,
            "Crash",
            EventEntry(timestamp = timeFrame.timeStart + 500_000L, event = "Crash", data = "Crash1")
        )
        crashes.addEntry(
            app1Key,
            "WTF",
            EventEntry(
                timestamp = timeFrame.timeStart + 1_250_000L,
                event = "WTF",
                data = "Crash12"
            )
        )
        crashes.addEntry(
            service1Key, "ANR",
            EventEntry(timestamp = timeFrame.timeStart + 1_120_000L, event = "ANR", data = "WTF1")
        )
        val userState = SingleStateChartData()
        val u0Key = StringKey("u0")
        userState.addEntry(
            u0Key, "RUNNING",
            SingleStateEntry(
                timestamp = timeFrame.timeStart + 1_000_000L,
                state = "RUNNING",
                data = ""
            )
        )
        userState.addEntry(
            u0Key, "CLOSED",
            SingleStateEntry(
                timestamp = timeFrame.timeStart + 1_000_000L,
                state = "CLOSED",
                data = ""
            ),
        )

        entriesMap[ChartParameters(0, "crashes", ChartType.Events)] = crashes
        entriesMap[ChartParameters(1, "cpuc", ChartType.Percentage, labelsPostfix = "%")] = cpuEntries
        entriesMap[ChartParameters(2, "userState", ChartType.SingleState)] = userState
    }

}