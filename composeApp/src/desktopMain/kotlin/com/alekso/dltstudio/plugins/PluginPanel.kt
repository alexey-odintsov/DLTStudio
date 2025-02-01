package com.alekso.dltstudio.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier


@Stable
interface PluginPanel {
    fun getPanelName(): String

    @Composable
    fun renderPanel(modifier: Modifier)
}