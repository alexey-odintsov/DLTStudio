package com.alekso.dltstudio.plugins.dltdetailedview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.PluginLogPreview

class DLTDetailedViewPlugin: DLTStudioPlugin, PluginLogPreview {
    override fun pluginName(): String = "DLT detailed vide"

    override fun pluginDirectoryName(): String = "dlt-detailed-view"

    override fun pluginVersion(): String = "0.0.1"

    override fun pluginClassName(): String = DLTDetailedViewPlugin::class.simpleName.toString()

    override fun init(
        logs: SnapshotStateList<LogMessage>,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectoryPath: String
    ) {

    }

    override fun onLogsChanged() {

    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?, messageIndex: Int) {
        DLTDetailedInfoView(modifier, logMessage, messageIndex)
    }

    override fun getPanelName(): String = "DLT Details"
}