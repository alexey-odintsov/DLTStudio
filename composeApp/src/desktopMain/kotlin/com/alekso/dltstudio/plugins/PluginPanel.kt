package com.alekso.dltstudio.plugins

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

open class PanelState {

}

interface PluginPanel {
    fun getPanelName(): String

    fun getPanelState(): PanelState

    @Composable
    fun renderPanel(modifier: Modifier, state: PanelState)
}