package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.FormatterConsumer
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.dltstudio.uicomponents.dialogs.FileDialog

val LocalFormatter = staticCompositionLocalOf<Formatter> { Formatter.STUB }

class FilesPlugin : DLTStudioPlugin, PluginPanel, FormatterConsumer {
    private lateinit var messagesRepository: MessagesRepository
    private lateinit var viewModel: FilesViewModel
    private lateinit var formatter: Formatter

    override fun pluginName(): String = "Files Viewer Plugin"
    override fun pluginDirectoryName(): String = "files-plugin"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = FilesPlugin::class.simpleName.toString()
    override fun getPanelName(): String = "Files"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectory: String,
    ) {
        this.messagesRepository = messagesRepository
        viewModel = FilesViewModel(onProgressUpdate)
    }

    override fun initFormatter(formatter: Formatter) {
        this.formatter = formatter
    }

    override fun onLogsChanged() {
        viewModel.clearState()
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        FileDialog(viewModel.fileDialogState)

        CompositionLocalProvider(LocalFormatter provides formatter) {
            FilesPanel(
                analyzeState = viewModel.analyzeState.value,
                files = viewModel.filesEntries,
                previewState = viewModel.previewState,
                onPreviewDialogClosed = viewModel::closePreviewDialog,
                onSearchButtonClicked = {
                    viewModel.startFilesSearch(messagesRepository.getMessages())
                },
                onFileEntryClicked = viewModel::onFileClicked
            )
        }
    }
}