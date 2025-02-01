package com.alekso.dltstudio.files

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.plugins.PanelState
import com.alekso.dltstudio.plugins.PluginPanel

class FilesPlugin(
    private val viewModel: FilesViewModel,
    private val logMessages: SnapshotStateList<LogMessage>,
    private val state: PanelState,
) : PluginPanel {
    override fun getPanelName(): String = "Files"
    override fun getPanelState(): PanelState = state

    @Composable
    override fun renderPanel(modifier: Modifier, state: PanelState) {
        FilesPanel(
            viewModel = viewModel,
            logMessages = logMessages,
            analyzeState = viewModel.analyzeState.value,
            files = viewModel.filesEntries
        )
    }

}