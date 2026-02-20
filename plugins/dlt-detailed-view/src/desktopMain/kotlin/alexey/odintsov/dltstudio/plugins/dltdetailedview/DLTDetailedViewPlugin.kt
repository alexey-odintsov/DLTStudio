package alexey.odintsov.dltstudio.plugins.dltdetailedview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.contract.DLTStudioPlugin
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
import alexey.odintsov.dltstudio.plugins.contract.PluginLogPreview

class DLTDetailedViewPlugin : DLTStudioPlugin, PluginLogPreview {
    override fun pluginName(): String = "DLT detailed view"
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