package com.alekso.dltstudio.plugins.loginfoview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginLogPreview

val LocalFormatter = staticCompositionLocalOf<Formatter> { Formatter.STUB }


class LogInfoViewPlugin : DLTStudioPlugin, PluginLogPreview {
    private lateinit var viewModel: LogInfoViewViewModel

    override fun pluginName(): String = "Log info view"
    override fun pluginDirectoryName(): String = "log-info-view"
    override fun pluginVersion(): String = "0.0.1"
    override fun pluginClassName(): String = LogInfoViewPlugin::class.simpleName.toString()
    override fun getPanelName(): String = "Info view"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String,
    ) {
        // do nothing
        viewModel = LogInfoViewViewModel(messagesRepository)
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?, messageIndex: Int) {
        LogInfoView(modifier, logMessage, messageIndex, onCommentUpdated = viewModel::onCommentUpdated)
    }
}