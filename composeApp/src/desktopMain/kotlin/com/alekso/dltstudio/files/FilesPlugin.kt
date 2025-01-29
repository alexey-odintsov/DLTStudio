package com.alekso.dltstudio.com.alekso.dltstudio.files

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.PluginPanel
import com.alekso.dltstudio.files.FilesPanel
import com.alekso.dltstudio.files.FilesViewModel
import com.alekso.dltstudio.model.LogMessage

class FilesPlugin(
    private val viewModel: FilesViewModel,
    private val logMessages: SnapshotStateList<LogMessage>,
) : PluginPanel {
    override fun getPanelName(): String = "Files"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        FilesPanel(
            viewModel = viewModel,
            logMessages = logMessages,
            analyzeState = viewModel.analyzeState.value,
            files = viewModel.filesEntries
        )
    }

}