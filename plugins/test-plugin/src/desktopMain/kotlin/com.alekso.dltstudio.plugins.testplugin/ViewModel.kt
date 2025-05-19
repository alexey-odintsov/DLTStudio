package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.alekso.dltstudio.graphs.model.EventValue
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.NumericalValue
import com.alekso.dltstudio.graphs.model.SingleEventValue
import com.alekso.dltstudio.graphs.model.StringKey
import com.alekso.dltstudio.graphs.model.TimeFrame
import com.alekso.dltstudio.graphs.model.Value
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
    val entriesMap = mutableStateMapOf<Diagram, SnapshotStateMap<out Key, List<out Value>>>()
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
        val cpuEntries = mutableStateMapOf<Key, List<out Value>>(
            StringKey("app1") to listOf(
                NumericalValue(
                    timestamp = timeFrame.timeStart + 1_000_000L,
                    data = Message(timeFrame.timeStart + 1_000_000L, "cpu0: 40%"),
                    value = 40f
                )
            ),
            StringKey("app2") to listOf(
                NumericalValue(
                    timestamp = timeFrame.timeStart + 1_050_000L,
                    data = Message(timeFrame.timeStart + 1_050_000L, "cpu1: 25%"),
                    value = 25f
                )
            ),
            StringKey("service1") to listOf(
                NumericalValue(
                    timestamp = timeFrame.timeStart + 900_000L,
                    data = Message(timeFrame.timeStart + 900_000L, "cpu2: 14%"),
                    value = 14f
                ),
                NumericalValue(
                    timestamp = timeFrame.timeStart + 1_200_000L,
                    data = Message(timeFrame.timeStart + 1_200_000L, "cpu2: 8%"),
                    value = 8f
                ),
            ),
        )


        val crashes = mutableStateMapOf<Key, List<out Value>>(
            StringKey("app1") to listOf(
                EventValue(timestamp = timeFrame.timeStart + 500_000L, event = "Crash", data = "Crash1"),
                EventValue(timestamp = timeFrame.timeStart + 1_250_000L, event = "WTF", data = "Crash12"),
            ),
            StringKey("service1") to listOf(
                EventValue(timestamp = timeFrame.timeStart + 1_120_000L, event = "ANR", data = "WTF1"),
            ),
        )
        val userState = mutableStateMapOf<Key, List<out Value>>(
            StringKey("u0") to listOf(
                SingleEventValue(timestamp = timeFrame.timeStart + 1_000_000L, state = "RUNNING", data = ""),
                SingleEventValue(timestamp = timeFrame.timeStart + 1_000_000L, state = "CLOSED", data = ""),
            ),
        )
        entriesMap[Diagram("crashes", GraphType.Events)] = crashes
        entriesMap[Diagram("cpuc", GraphType.Percentage)] = cpuEntries
        entriesMap[Diagram("userState", GraphType.SingleState)] = userState
    }

}