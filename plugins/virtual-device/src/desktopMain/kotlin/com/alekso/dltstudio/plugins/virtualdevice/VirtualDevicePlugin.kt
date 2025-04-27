package com.alekso.dltstudio.plugins.virtualdevice

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginLogPreview
import com.alekso.dltstudio.plugins.virtualdevice.db.VirtualDeviceRepository
import com.alekso.dltstudio.plugins.virtualdevice.db.VirtualDeviceRepositoryImpl
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
    override fun getPanelName(): String = "Virtual device"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectory: String,
    ) {
        virtualDeviceRepository = VirtualDeviceRepositoryImpl(
            database = DBFactory().createDatabase("${pluginDirectory}/virtual_device.db"),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
        viewModel = VirtualDeviceViewModel(virtualDeviceRepository)
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPreview(modifier: Modifier, logMessage: LogMessage?, messageIndex: Int) {
        if (viewModel.devicePreviewDialogState) {
            VirtualDevicesDialog(
                visible = viewModel.devicePreviewDialogState,
                onDialogClosed = { viewModel.devicePreviewDialogState = false },
                virtualDevices = viewModel.virtualDevices,
                onVirtualDeviceUpdate = { device -> viewModel.onVirtualDeviceUpdate(device) },
                onVirtualDeviceDelete = { device -> viewModel.onVirtualDeviceDelete(device) },
            )
        }
        DevicePreviewView(
            modifier = modifier,
            currentDeviceIndex = viewModel.currentDeviceIndex,
            virtualDevices = viewModel.virtualDevices,
            logMessage = logMessage,
            messageIndex = messageIndex,
            onShowVirtualDeviceClicked = {
                viewModel.devicePreviewDialogState = true
            },
            onDeviceSelected = viewModel::onDeviceSelected
        )
    }

}