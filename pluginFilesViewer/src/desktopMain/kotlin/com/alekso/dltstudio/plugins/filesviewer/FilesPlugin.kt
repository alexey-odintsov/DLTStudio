package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.FormatterConsumer
import com.alekso.dltstudio.plugins.PluginPanel

val LocalFormatter = staticCompositionLocalOf<Formatter> { Formatter.STUB }

class FilesPlugin : DLTStudioPlugin, PluginPanel, FormatterConsumer {
    private lateinit var logMessages: SnapshotStateList<LogMessage>
    private lateinit var viewModel: FilesViewModel
    private lateinit var formatter: Formatter

    override fun pluginName(): String = "Files Viewer Plugin"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = "com.alekso.dltstudio.plugins.filesviewer.FilesPlugin"

    override fun init(logs: SnapshotStateList<LogMessage>, onProgressUpdate: (Float) -> Unit) {
        logMessages = logs
        viewModel = FilesViewModel(onProgressUpdate)
    }

    override fun initFormatter(formatter: Formatter) {
        this.formatter = formatter
    }

    override fun onLogsChanged() {
        viewModel.clearState()
    }

    override fun getPanelName(): String = "Files"


    @Composable
    override fun renderPanel(modifier: Modifier) {
        println("Recompose FilesPlugin.renderPanel")

        CompositionLocalProvider(LocalFormatter provides formatter) {
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

}