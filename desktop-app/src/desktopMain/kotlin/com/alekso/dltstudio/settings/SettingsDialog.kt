package com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltstudio.model.PluginState
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.model.SettingsPlugins
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.TabsPanel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState

interface SettingsDialogCallbacks {
    fun onSettingsUIUpdate(settings: SettingsUI)
    fun onSettingsLogsUpdate(settings: SettingsLogs)
    fun onOpenDefaultLogsFolderClicked()
    fun onOpenDefaultColorFiltersFolderClicked()

    companion object {
        val Stub = object : SettingsDialogCallbacks {
            override fun onSettingsUIUpdate(settings: SettingsUI) = Unit
            override fun onSettingsLogsUpdate(settings: SettingsLogs) = Unit
            override fun onOpenDefaultLogsFolderClicked() = Unit
            override fun onOpenDefaultColorFiltersFolderClicked() = Unit
        }
    }
}

interface SettingsPluginsCallbacks {
    fun onPluginClicked(plugin: DLTStudioPlugin)
    fun onUpdatePluginState(pluginState: PluginState)

    companion object {
        val Stub = object : SettingsPluginsCallbacks {
            override fun onPluginClicked(plugin: DLTStudioPlugin) = Unit
            override fun onUpdatePluginState(pluginState: PluginState) = Unit
        }
    }
}

@Composable
fun SettingsDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    settingsUI: SettingsUI,
    settingsLogs: SettingsLogs,
    callbacks: SettingsDialogCallbacks,
    pluginsCallbacks: SettingsPluginsCallbacks,
    settingsPlugins: SettingsPlugins,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Settings",
        state = rememberDialogState(width = 800.dp, height = 600.dp)
    ) {
        ThemeManager.AppTheme {
            SettingsPanel(
                callbacks, settingsUI, settingsLogs,
                pluginsCallbacks,
                settingsPlugins
            )
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun SettingsPanel(
    callbacks: SettingsDialogCallbacks,
    settingsUI: SettingsUI,
    settingsLogs: SettingsLogs,
    pluginsCallbacks: SettingsPluginsCallbacks,
    settingsPlugins: SettingsPlugins,
) {
    val tabs = mutableStateListOf("Appearance", "Logs", "Plugins")

    @OptIn(ExperimentalSplitPaneApi::class)
    val vSplitterState = SplitPaneState(0.8f, true)

    var tabIndex by remember { mutableStateOf(0) }
    Row(modifier = Modifier.padding(4.dp)) {
        Column(Modifier.width(140.dp)) {
            TabsPanel(tabIndex, tabs, { i -> tabIndex = i }, vertical = true)
        }
        VerticalDivider()
        Column(Modifier.weight(1f)) {
            when (tabIndex) {
                0 -> AppearancePanel(callbacks, settingsUI)
                1 -> LogsPanel(callbacks, settingsLogs)
                2 -> PluginsPanel(settingsPlugins, pluginsCallbacks, vSplitterState)
                else -> Unit
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsDialog() {
    SettingsPanel(
        callbacks = SettingsDialogCallbacks.Stub,
        settingsUI = SettingsUI(12, FontFamily.Serif),
        settingsLogs = SettingsLogs(
            backendType = PayloadStorageType.Binary,
            defaultLogsFolderPath = "",
            defaultColorFiltersFolderPath = ""
        ),
        pluginsCallbacks = SettingsPluginsCallbacks.Stub,
        settingsPlugins = SettingsPlugins(pluginsState = emptyList()),
    )
}