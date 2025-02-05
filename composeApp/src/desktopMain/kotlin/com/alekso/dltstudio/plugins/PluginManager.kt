package com.alekso.dltstudio.com.alekso.dltstudio.plugins

import com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.PluginPanel
import com.alekso.dltstudio.plugins.deviceplugin.DeviceAnalyzePlugin
import com.alekso.dltstudio.plugins.filesviewer.FilesPlugin

class PluginManager {
    private val predefinedPlugins = listOf<DLTStudioPlugin>(
        DeviceAnalyzePlugin(),
        FilesPlugin(),
    )
    private val plugins = mutableListOf<DLTStudioPlugin>()

    suspend fun loadPlugins() {
        predefinedPlugins.forEach { plugin ->
            plugin.init(
                logs = DependencyManager.getMessageHolder().getMessages(),
                onProgressUpdate = DependencyManager.onProgressUpdate,
            )
            plugins.add(plugin)
        }
    }

    fun getPluginPanels(): List<PluginPanel> {
        return plugins.filter { it is PluginPanel }.map { it as PluginPanel }
    }

    fun notifyLogsChanged() {
        plugins.forEach { plugin ->
            plugin.onLogsChanged()
        }
    }

}