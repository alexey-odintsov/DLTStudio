package com.alekso.dltstudio.com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState

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
    Row(modifier = Modifier.padding(4.dp)) {
        Column(Modifier.width(200.dp)) {
            Text("Appearance")
            Text("Logs")
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