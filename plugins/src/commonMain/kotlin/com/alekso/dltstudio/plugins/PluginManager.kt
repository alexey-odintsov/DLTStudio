package com.alekso.dltstudio.plugins

class PluginManager(
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