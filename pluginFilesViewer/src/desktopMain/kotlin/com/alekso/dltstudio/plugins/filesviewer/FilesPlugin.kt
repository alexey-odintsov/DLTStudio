package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.PluginPanel

class FilesPlugin : DLTStudioPlugin, PluginPanel {
    private lateinit var logMessages: SnapshotStateList<LogMessage>
    private lateinit var viewModel: FilesViewModel

    override fun pluginName(): String = "Files Viewer Plugin"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = "com.alekso.dltstudio.plugins.filesviewer.FilesPlugin"

    override fun init(logs: SnapshotStateList<LogMessage>, onProgressUpdate: (Float) -> Unit) {
        logMessages = logs
        viewModel = FilesViewModel(onProgressUpdate)
    }

    override fun onLogsChanged() {
        viewModel.clearState()
    }

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