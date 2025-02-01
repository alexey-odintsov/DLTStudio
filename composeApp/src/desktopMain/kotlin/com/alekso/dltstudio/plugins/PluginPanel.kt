package com.alekso.dltstudio.plugins

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


interface PluginPanel {
    fun getPanelName(): String

    @Composable
    fun renderPanel(modifier: Modifier)
}