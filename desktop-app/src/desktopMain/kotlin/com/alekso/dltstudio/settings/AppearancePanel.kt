package com.alekso.dltstudio.com.alekso.dltstudio.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomEditText

@Composable
fun AppearancePanel(
    callbacks: SettingsDialogCallbacks,
    settingsUI: SettingsUI
) {
    var fontSize by remember { mutableStateOf(settingsUI.fontSize.toString()) }
    var fontType by remember { mutableStateOf(settingsUI.fontType.toString()) }
    Row {
        Text("Font size: ${settingsUI.fontSize}")
        CustomEditText(
            modifier = Modifier.width(100.dp),
            value = fontSize, onValueChange = {
                fontSize = it
            }
        )
    }
    Row {
        Text("Font type: $fontType")
    }
    CustomButton(onClick = {
        callbacks.onSettingsUIUpdate(SettingsUI(fontSize.toInt(), fontType.toInt()))
    }) {
        Text("Apply")
    }
}