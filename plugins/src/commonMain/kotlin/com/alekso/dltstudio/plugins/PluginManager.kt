package com.alekso.dltstudio.plugins

import com.alekso.dltstudio.model.contract.Formatter

class PluginManager(
    private val formatter: Formatter,
    private val messagesProvider: MessagesProvider,
    private val onProgressUpdate: (Float) -> Unit
) {
    private val predefinedPlugins = mutableListOf<DLTStudioPlugin>()
    private val plugins = mutableListOf<DLTStudioPlugin>()


    fun registerPredefinedPlugin(plugin: DLTStudioPlugin) {
        if (!predefinedPlugins.contains(plugin)) {
            predefinedPlugins.add(plugin)
        }
    }

    suspend fun loadPlugins() {
        predefinedPlugins.forEach { plugin ->
            plugin.init(
                logs = messagesProvider.getMessages(),
                onProgressUpdate = onProgressUpdate,
            )
            if (plugin is FormatterConsumer) {
                plugin.initFormatter(formatter)
            }
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