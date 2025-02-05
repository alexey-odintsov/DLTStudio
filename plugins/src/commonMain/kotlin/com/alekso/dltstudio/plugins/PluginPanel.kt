package com.alekso.dltstudio.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

/**
 * Interface for plugin panel that is shown in main window tabs.
 */
@Stable
interface PluginPanel {
    /**
     * Panel name that will be shown in tabs. Please keep it short.
     */
    fun getPanelName(): String

    /**
     * Main panel composable function that will be rendered when tab is active
     */
    @Composable
    fun renderPanel(modifier: Modifier)
}