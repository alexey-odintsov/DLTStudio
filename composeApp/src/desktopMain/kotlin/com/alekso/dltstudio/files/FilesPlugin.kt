package com.alekso.dltstudio.files

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.plugins.PluginPanel

class FilesPlugin(
    private val viewModel: FilesViewModel,
    private val logMessages: SnapshotStateList<LogMessage>,
) : DLTStudioPlugin, PluginPanel {

    override fun identify(): String = "Files Extraction Plugin"
    
    override fun getPanelName(): String = "Files"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        println("Recompose FilesPlugin.renderPanel")

        FilesPanel(
            analyzeState = viewModel.analyzeState.value,
            files = viewModel.filesEntries,
            previewState = viewModel.previewState,
            onPreviewDialogClosed = viewModel::closePreviewDialog,
            onSearchButtonClicked = {
                viewModel.startFilesSearch(logMessages)
            },
            onSaveFileClicked = viewModel::saveFile,
            onFileEntryClicked = viewModel::onFileClicked
        )
    }

}