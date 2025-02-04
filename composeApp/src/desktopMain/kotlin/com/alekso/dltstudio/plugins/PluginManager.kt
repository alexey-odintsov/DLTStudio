package com.alekso.dltstudio.com.alekso.dltstudio.plugins

import com.alekso.dltstudio.plugins.deviceplugin.DeviceAnalyzePlugin
import com.alekso.dltstudio.plugins.deviceplugin.DeviceAnalyzeViewModel
import com.alekso.dltstudio.plugins.filesextraction.FilesPlugin
import com.alekso.dltstudio.plugins.filesextraction.FilesViewModel
import com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.PluginPanel

class PluginManager {
    private val plugins = mutableListOf<DLTStudioPlugin>()

    suspend fun loadPlugins() {
        val deviceAnalyzePlugin = DeviceAnalyzePlugin(
            DeviceAnalyzeViewModel(DependencyManager.onProgressUpdate)
        )
        plugins.add(deviceAnalyzePlugin)

        val filesPlugin = FilesPlugin(
            viewModel = FilesViewModel(DependencyManager.onProgressUpdate),
            logMessages = DependencyManager.getMessageHolder().getMessages(),
        )
        plugins.add(filesPlugin)
    }

    fun getPluginPanels(): List<PluginPanel> {
        return plugins.filter { it is PluginPanel }.map { it as PluginPanel }
    }

}