package com.alekso.dltstudio.plugins.loginsights

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.LogSelectionObserver
import com.alekso.dltstudio.plugins.contract.PluginLogPreview

class LogInsightsPlugin : DLTStudioPlugin, PluginLogPreview, LogSelectionObserver {
    private lateinit var viewModel: LogInsightsViewModel

    override fun pluginName(): String = "Logs insights"
    override fun pluginDirectoryName(): String = "log-insights"
    override fun pluginVersion(): String = "0.0.1"
    override fun pluginClassName(): String = LogInsightsPlugin::class.simpleName.toString()
    override fun getPanelName(): String = "Insights"

    override fun init(
        logs: SnapshotStateList<LogMessage>,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectory: String
    ) {
        viewModel = LogInsightsViewModel(InsightsRepository())
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?, messageIndex: Int) {
        LogInsightsView(modifier, logMessage, messageIndex, viewModel.logInsights)
    }

    override fun onMessageSelected(logMessage: LogMessage) {
        viewModel.onLogSelected(logMessage)
    }

}