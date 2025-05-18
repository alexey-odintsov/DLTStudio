package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.PercentageValue
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

data class CPUEvent(
    val message: Message,
    override val value: Float,
    override val timestamp: Long = message.timestamp,
) : PercentageValue

data class CPUKey(
    val cpuName: String,
    override val key: String = cpuName,
) : Key

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
        val key1 = CPUKey("cpu0")
        val key2 = CPUKey("cpu1")
        val key3 = CPUKey("cpu2")
        val cpuEntries = mutableStateMapOf<Key, List<out Value>>(
            key1 to listOf(
                CPUEvent(
                    Message(timeFrame.timeStart + 1_000_000L, "cpu0: 40%"),
                    40f
                )
            ),
            key2 to listOf(
                CPUEvent(
                    Message(timeFrame.timeStart + 1_200_000L, "cpu1: 25%"),
                    25f
                )
            ),
            key3 to listOf(
                CPUEvent(Message(timeFrame.timeStart + 600_000L, "cpu3: 5%"), 5f),
                CPUEvent(Message(timeFrame.timeStart + 1_500_000L, "cpu3: 8%"), 8f),
            ),
        )
        entriesMap[Diagram("crashes", GraphType.Events)] = mutableStateMapOf()
        entriesMap[Diagram("cpuc", GraphType.MinMax)] = cpuEntries
    }

}