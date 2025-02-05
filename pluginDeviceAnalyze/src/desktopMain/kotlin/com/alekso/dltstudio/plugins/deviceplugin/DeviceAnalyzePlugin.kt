package com.alekso.dltstudio.plugins.deviceplugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.PluginPanel

class DeviceAnalyzePlugin : DLTStudioPlugin, PluginPanel {
    private lateinit var viewModel: DeviceAnalyzeViewModel

    override fun identify(): String = "Device Analyze Plugin"

    override fun init(logs: SnapshotStateList<LogMessage>, onProgressUpdate: (Float) -> Unit) {
        viewModel = DeviceAnalyzeViewModel(onProgressUpdate)
    }

    override fun getPanelName(): String = "Device Analyse"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        println("Recompose DeviceAnalyzePlugin.renderPanel")

        DeviceAnalysePanel(
            modifier = modifier,
            responseState = viewModel.analyzeState,
            onExecuteButtonClicked = viewModel::executeCommand
        )
    }
}