package alexey.odintsov.dltstudio.plugins.loginsights

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.contract.DLTStudioPlugin
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
import alexey.odintsov.dltstudio.plugins.contract.PluginLogPreview

class LogInsightsPlugin : DLTStudioPlugin, PluginLogPreview {
    private lateinit var viewModel: LogInsightsViewModel

    override fun pluginName(): String = "Logs insights"
    override fun pluginDirectoryName(): String = "log-insights"
    override fun pluginVersion(): String = "0.0.1"
    override fun pluginClassName(): String = LogInsightsPlugin::class.simpleName.toString()
    override fun author(): String = "Alexey Odintsov"
    override fun pluginLink(): String? = null
    override fun description(): String = "Translates selected message to a readable format based on transformation database."
    override fun getPanelName(): String = "Insights"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String
    ) {
        viewModel = LogInsightsViewModel(InsightsRepository())
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?) {
        viewModel.loadInsights(logMessage)
        LogInsightsView(modifier, logMessage, viewModel.logInsights)
    }
}