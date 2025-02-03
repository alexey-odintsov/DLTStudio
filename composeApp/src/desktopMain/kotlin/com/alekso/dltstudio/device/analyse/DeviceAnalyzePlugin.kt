package com.alekso.dltstudio.device.analyse

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.PluginPanel

class DeviceAnalyzePlugin(
    private val viewModel: DeviceAnalyzeViewModel,
) : DLTStudioPlugin, PluginPanel {

    override fun identify(): String = "Device Analyze Plugin"

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