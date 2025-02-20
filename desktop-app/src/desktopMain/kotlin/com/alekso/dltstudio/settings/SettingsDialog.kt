package com.alekso.dltstudio.com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.uicomponents.TabsPanel

interface SettingsDialogCallbacks {
    fun onSettingsUIUpdate(settings: SettingsUI)

    companion object {
        val Stub = object : SettingsDialogCallbacks {
            override fun onSettingsUIUpdate(settings: SettingsUI) = Unit
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
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Settings",
        state = rememberDialogState(width = 500.dp, height = 500.dp)
    ) {
        SettingsPanel(callbacks, settingsUI, settingsLogs)
    }
}

@Composable
fun SettingsPanel(
    callbacks: SettingsDialogCallbacks,
    settingsUI: SettingsUI,
    settingsLogs: SettingsLogs
) {
    val tabs = mutableStateListOf("Appearance", "Logs", "Plugins")
    var tabIndex by remember { mutableStateOf(0) }
    Row(modifier = Modifier.padding(4.dp)) {
        Column(Modifier.width(140.dp)) {
            TabsPanel(tabIndex, tabs, { i -> tabIndex = i }, vertical = true)
        }
        Column(Modifier.weight(1f)) {
            when (tabIndex) {
                0 -> AppearancePanel(callbacks, settingsUI)
                1 -> LogsPanel(callbacks, settingsLogs)
                2 -> PluginsPanel(callbacks)
                else -> Unit
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsDialog() {
    SettingsPanel(callbacks = object : SettingsDialogCallbacks {
        override fun onSettingsUIUpdate(settings: SettingsUI) = Unit

    },
        settingsUI = SettingsUI(12, FontFamily.Serif),
        settingsLogs = SettingsLogs(backendType = PayloadStorageType.Binary)
    )
}