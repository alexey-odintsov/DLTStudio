package com.alekso.dltstudio.device.analyse

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.plugins.PanelState
import com.alekso.dltstudio.plugins.PluginPanel

class DeviceAnalyzePlugin(
    private val viewModel: DeviceAnalyzeViewModel,
    private val state: PanelState,
) : PluginPanel {
    override fun getPanelName(): String = "Device Analyse"
    override fun getPanelState(): PanelState = state

    @Composable
    override fun renderPanel(modifier: Modifier, state: PanelState) {
        DeviceAnalysePanel(modifier = modifier, deviceAnalyzeViewModel = viewModel)
    }
}