package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltstudio.charts.model.ChartData
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
        entriesMap.putAll(EntriesParser().provideMockEntries(timeFrame))
    }

}