package alexey.odintsov.dltstudio.plugins.virtualdevice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.contract.DLTStudioPlugin
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
import alexey.odintsov.dltstudio.plugins.contract.PluginLogPreview
import alexey.odintsov.dltstudio.plugins.virtualdevice.db.VirtualDeviceRepository
import alexey.odintsov.dltstudio.plugins.virtualdevice.db.VirtualDeviceRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class VirtualDevicePlugin : DLTStudioPlugin, PluginLogPreview {
    private lateinit var viewModel: VirtualDeviceViewModel
    private lateinit var virtualDeviceRepository: VirtualDeviceRepository

    override fun pluginName(): String = "Virtual Device"
    override fun pluginDirectoryName(): String = "virtual-device"
    override fun pluginVersion(): String = "0.0.1"
    override fun pluginClassName(): String = VirtualDevicePlugin::class.simpleName.toString()
    override fun author(): String = "Alexey Odintsov"
    override fun pluginLink(): String? = null
    override fun description(): String = "Allows creating virtual screens and preview elements from selected logs. To show previews selected log message should contains Rect information."
    override fun getPanelName(): String = "Virtual device"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String,
    ) {
        virtualDeviceRepository = VirtualDeviceRepositoryImpl(
            database = DBFactory().createDatabase("${pluginFilesPath}/virtual_device.db"),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
        viewModel = VirtualDeviceViewModel(virtualDeviceRepository)
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?) {
        val virtualDevices = viewModel.virtualDevices.collectAsState()

        if (viewModel.devicePreviewDialogState) {
            VirtualDevicesDialog(
                visible = viewModel.devicePreviewDialogState,
                onDialogClosed = { viewModel.devicePreviewDialogState = false },
                virtualDevices = virtualDevices.value,
                onVirtualDeviceUpdate = { device -> viewModel.onVirtualDeviceUpdate(device) },
                onVirtualDeviceDelete = { device -> viewModel.onVirtualDeviceDelete(device) },
            )
        }
        DevicePreviewView(
            modifier = modifier,
            currentDeviceIndex = viewModel.currentDeviceIndex,
            virtualDevices = virtualDevices.value,
            logMessage = logMessage,
            onShowVirtualDeviceClicked = {
                viewModel.devicePreviewDialogState = true
            },
            onDeviceSelected = viewModel::onDeviceSelected,
        )
    }

}