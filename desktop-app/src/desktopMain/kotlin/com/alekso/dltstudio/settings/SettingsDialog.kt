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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.uicomponents.TabsPanel

interface SettingsDialogCallbacks {
}

@Composable
fun SettingsDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    callbacks: SettingsDialogCallbacks,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Settings",
        state = rememberDialogState(width = 500.dp, height = 500.dp)
    ) {
        SettingsPanel(callbacks)
    }
}

@Composable
fun SettingsPanel(callbacks: SettingsDialogCallbacks) {
    val tabs = mutableStateListOf("Appearance", "Logs")
    var tabIndex by remember { mutableStateOf(0) }
    Row(modifier = Modifier.padding(4.dp)) {
        Column(Modifier.width(200.dp)) {
            TabsPanel(tabIndex, tabs, { i -> tabIndex = i }, vertical = true)
        }
        Column {

        }
    }
}

@Preview
@Composable
fun PreviewSettingsDialog() {
    SettingsPanel(callbacks = object : SettingsDialogCallbacks {

    })
}