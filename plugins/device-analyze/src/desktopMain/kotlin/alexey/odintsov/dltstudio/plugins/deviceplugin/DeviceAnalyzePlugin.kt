package alexey.odintsov.dltstudio.plugins.deviceplugin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import alexey.odintsov.dltstudio.plugins.contract.DLTStudioPlugin
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
import alexey.odintsov.dltstudio.plugins.contract.PluginPanel

class DeviceAnalyzePlugin : DLTStudioPlugin, PluginPanel {
    private lateinit var viewModel: DeviceAnalyzeViewModel

    override fun pluginName(): String = "Device Analyze Plugin"
    override fun pluginDirectoryName(): String = "device-analyze"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = DeviceAnalyzePlugin::class.simpleName.toString()
    override fun author(): String = "Alexey Odintsov"
    override fun pluginLink(): String? = null
    override fun description(): String = "Executes predefined adb commands"
    override fun getPanelName(): String = "Device Analyse"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String,
    ) {
        viewModel = DeviceAnalyzeViewModel(onProgressUpdate)
    }

    override fun onLogsChanged() {
        // ignore
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        DeviceAnalysePanel(
            modifier = modifier,
            responseState = viewModel.analyzeState,
            onExecuteButtonClicked = viewModel::executeCommand
        )
    }
}