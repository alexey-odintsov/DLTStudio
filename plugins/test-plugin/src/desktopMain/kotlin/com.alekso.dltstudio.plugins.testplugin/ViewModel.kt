package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.alekso.dltstudio.graphs.model.Entry
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.TimeFrame
import kotlinx.datetime.Clock

class ViewModel {
    // State
    val entries = mutableStateMapOf<Key<*>, Entry<*>>()
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

    // Business logic
    private fun extractEvents() {
        entries.putAll(
            mapOf(
                Key("a") to Entry(timeFrame.timeStart, Color.Green),
                Key("b") to Entry(timeFrame.timeStart + 1000000, Color.Red),
                Key("b") to Entry(timeFrame.timeStart + 2000000, Color.Blue),
            )
        )
    }

    fun onDragged(dx: Float) {
        timeFrame = timeFrame.move(dx.toLong())
    }

    fun onZoom(zoomIn: Boolean) {
        timeFrame = timeFrame.zoom(zoomIn)
    }

}