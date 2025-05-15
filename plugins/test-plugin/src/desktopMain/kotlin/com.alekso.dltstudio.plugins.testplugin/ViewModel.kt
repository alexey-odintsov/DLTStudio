package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.alekso.dltstudio.graphs.model.Entry
import com.alekso.dltstudio.graphs.model.Key
import com.alekso.dltstudio.graphs.model.TimeFrame

class ViewModel {
    // State
    val entries = mutableStateMapOf<Key<*>, Entry<*>>()
    val totalTime by mutableStateOf(TimeFrame(0L, 480L))
    var timeFrame by mutableStateOf(TimeFrame(140L, 300L))

    // Public Interface
    fun onAnaliseClicked() {
        extractEvents()
    }

    // Business logic
    private fun extractEvents() {
        entries.putAll(
            mapOf(
                Key("a") to Entry(200L, Color.Green),
                Key("b") to Entry(160L, Color.Red),
                Key("b") to Entry(280L, Color.Blue),
            )
        )
    }

    fun onDragged(dx: Float) {
        timeFrame = timeFrame.move(dx.toLong())
    }

}