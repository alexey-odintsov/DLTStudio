package com.alekso.dltstudio.device.analyse

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.plugins.PluginPanel

class DeviceAnalyzePlugin(
    private val viewModel: DeviceAnalyzeViewModel,
) : PluginPanel {
    override fun getPanelName(): String = "Device Analyse"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        DeviceAnalysePanel(
            modifier = modifier,
            responseState = viewModel.analyzeState,
            onExecuteButtonClicked = viewModel::executeCommand
        )
    }
}