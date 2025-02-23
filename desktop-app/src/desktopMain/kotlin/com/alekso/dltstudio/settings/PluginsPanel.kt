package com.alekso.dltstudio.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.plugins.DependencyManager

@Composable
fun PluginsPanel(callbacks: SettingsDialogCallbacks) {
    val pluginManager = remember { DependencyManager.providePluginsManager() }
    Text(
        text = "Plugins",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp)
    )
    Text("Plugins path:")
    Text(pluginManager.pluginsPath)

    Spacer(Modifier.height(6.dp))

    Text("Registered plugins:")
    Column {
        DependencyManager.providePluginsManager().plugins.forEach { plugin ->
            Text("${plugin.pluginName()} ${plugin.pluginVersion()}")
        }
    }

}