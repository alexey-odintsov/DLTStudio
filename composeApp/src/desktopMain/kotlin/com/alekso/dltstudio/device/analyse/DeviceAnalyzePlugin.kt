package com.alekso.dltstudio.com.alekso.dltstudio.device.analyse

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.PluginPanel
import com.alekso.dltstudio.device.analyse.DeviceAnalysePanel
import com.alekso.dltstudio.device.analyse.DeviceAnalyzeViewModel

class DeviceAnalyzePlugin(private val viewModel: DeviceAnalyzeViewModel) : PluginPanel {
    override fun getPanelName(): String = "Device Analyse"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        DeviceAnalysePanel(modifier = modifier, deviceAnalyzeViewModel = viewModel)
    }
}