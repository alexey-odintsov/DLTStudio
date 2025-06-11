package com.alekso.dltstudio.model

data class SettingsPlugins(
    val selectedPlugin: String? = null,
    val pluginsState: List<PluginState>,
) {
    fun isEnabled(pluginClassName: String): Boolean {
        return pluginsState.find { it.key == pluginClassName }?.enabled == true
    }

    companion object {
        val Initial = SettingsPlugins(
            pluginsState = emptyList()
        )
    }
}