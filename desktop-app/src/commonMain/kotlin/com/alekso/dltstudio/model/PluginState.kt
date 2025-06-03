package com.alekso.dltstudio.model

import com.alekso.dltstudio.db.settings.PluginStateEntity

data class PluginState(
    val key: String,
    val enabled: Boolean,
)

fun PluginStateEntity.toPluginState(): PluginState =
    PluginState(this.pluginClass, this.enabled)

fun PluginState.toPluginStateEntity(): PluginStateEntity =
    PluginStateEntity(this.key, this.enabled)
