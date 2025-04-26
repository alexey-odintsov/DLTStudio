package com.alekso.dltstudio.plugins.deviceplugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.PluginPanel

class DeviceAnalyzePlugin : DLTStudioPlugin, PluginPanel {
    private lateinit var viewModel: DeviceAnalyzeViewModel

    override fun pluginName(): String = "Device Analyze Plugin"
    override fun pluginDirectoryName(): String = "device-analyze"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = DeviceAnalyzePlugin::class.simpleName.toString()
    override fun getPanelName(): String = "Device Analyse"

    override fun init(
        logs: SnapshotStateList<LogMessage>,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectory: String,
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