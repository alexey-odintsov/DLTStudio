package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltstudio.graphs.model.ChartData
import com.alekso.dltstudio.graphs.model.EventValue
import com.alekso.dltstudio.graphs.model.EventsChartData
import com.alekso.dltstudio.graphs.model.FloatChartData
import com.alekso.dltstudio.graphs.model.NumericalValue
import com.alekso.dltstudio.graphs.model.SingleStateChartData
import com.alekso.dltstudio.graphs.model.SingleStateValue
import com.alekso.dltstudio.graphs.model.StringKey
import com.alekso.dltstudio.graphs.model.TimeFrame
import com.alekso.dltstudio.graphs.ui.GraphType
import kotlinx.datetime.Clock

data class Message(
    val timestamp: Long,
    val message: String,
)

data class Diagram(
    val key: String,
    val graphType: GraphType,
)


class ViewModel {
    // State
    val entriesMap = mutableStateMapOf<Diagram, ChartData>()
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

        val cpuEntries = FloatChartData(mutableStateMapOf())
        cpuEntries.addEntry(
            StringKey("app1"),
                NumericalValue(
                    timestamp = timeFrame.timeStart + 1_000_000L,
                    data = Message(timeFrame.timeStart + 1_000_000L, "cpu0: 40%"),
                    value = 40f
                )
        )
        cpuEntries.addEntry(
            StringKey("app2"),
                NumericalValue(
                    timestamp = timeFrame.timeStart + 1_050_000L,
                    data = Message(timeFrame.timeStart + 1_050_000L, "cpu1: 25%"),
                    value = 25f
                )
        )
        val service1 = StringKey("service1")
        cpuEntries.addEntry(
            service1,
                NumericalValue(
                    timestamp = timeFrame.timeStart + 900_000L,
                    data = Message(timeFrame.timeStart + 900_000L, "cpu2: 14%"),
                    value = 14f
                )
        )
        cpuEntries.addEntry(
            service1,
            NumericalValue(
                    timestamp = timeFrame.timeStart + 1_200_000L,
                    data = Message(timeFrame.timeStart + 1_200_000L, "cpu2: 8%"),
                    value = 8f
                ),
        )


        val crashes = EventsChartData(mutableStateMapOf())
        val app1Key = StringKey("app1")
        val service1Key = StringKey("service1")
        crashes.addEntry(
            app1Key,
            "Crash",
            EventValue(timestamp = timeFrame.timeStart + 500_000L, event = "Crash", data = "Crash1")
        )
        crashes.addEntry(
            app1Key,
            "WTF",
            EventValue(
                timestamp = timeFrame.timeStart + 1_250_000L,
                event = "WTF",
                data = "Crash12"
            )
        )
        crashes.addEntry(
            service1Key, "ANR",
            EventValue(timestamp = timeFrame.timeStart + 1_120_000L, event = "ANR", data = "WTF1")
        )
        val userState = SingleStateChartData(mutableStateMapOf())
        val u0Key = StringKey("u0")
        userState.addEntry(
            u0Key, "RUNNING",
            SingleStateValue(
                timestamp = timeFrame.timeStart + 1_000_000L,
                state = "RUNNING",
                data = ""
            )
        )
        userState.addEntry(
            u0Key, "CLOSED",
            SingleStateValue(
                timestamp = timeFrame.timeStart + 1_000_000L,
                state = "CLOSED",
                data = ""
            ),
        )

        entriesMap[Diagram("crashes", GraphType.Events)] = crashes
        entriesMap[Diagram("cpuc", GraphType.Percentage)] = cpuEntries
        entriesMap[Diagram("userState", GraphType.SingleState)] = userState
    }

}