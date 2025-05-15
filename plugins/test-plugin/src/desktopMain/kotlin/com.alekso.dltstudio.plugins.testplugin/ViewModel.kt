package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import com.alekso.dltstudio.graphs.model.Entry
import com.alekso.dltstudio.graphs.model.Key

class ViewModel {
    // State
    val entries = mutableStateMapOf<Key<*>, Entry<*>>()

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

}