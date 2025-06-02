package com.alekso.dltstudio.plugins.dltdetailedview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginLogPreview

class DLTDetailedViewPlugin : DLTStudioPlugin, PluginLogPreview {
    override fun pluginName(): String = "DLT detailed vide"
    override fun pluginDirectoryName(): String = "dlt-detailed-view"
    override fun pluginVersion(): String = "0.0.1"
    override fun pluginClassName(): String = DLTDetailedViewPlugin::class.simpleName.toString()
    override fun author(): String = "Alexey Odintsov"
    override fun pluginLink(): String? = null
    override fun description(): String = "Shows detailed information about selected DLT message."
    override fun getPanelName(): String = "DLT Details"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String,
    ) {
        // do nothing
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?) {
        DLTDetailedInfoView(modifier, logMessage)
    }

}