package com.alekso.dltstudio.com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.model.SupportedFontFamilies
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown
import com.alekso.dltstudio.uicomponents.CustomEditText

@Composable
fun AppearancePanel(
    callbacks: SettingsDialogCallbacks,
    settingsUI: SettingsUI
) {
    println("Recompose AppearancePanel $settingsUI")
    var fontSize by remember { mutableStateOf(settingsUI.fontSize.toString()) }
    var fontFamily by remember { mutableStateOf(settingsUI.fontFamily) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Font size: ${settingsUI.fontSize}")
            CustomEditText(
                modifier = Modifier.width(100.dp),
                value = fontSize, onValueChange = {
                    fontSize = it
                }
            )
        }
        Row {
            Text("Font type: $fontFamily")
            CustomDropDown(
                modifier = Modifier.width(150.dp).padding(horizontal = 4.dp),
                items = SupportedFontFamilies.entries.map { it.name },
                initialSelectedIndex = SupportedFontFamilies.getIdByFontFamily(fontFamily),
                onItemsSelected = { index ->
                    fontFamily = SupportedFontFamilies.getFontFamilyById(index)
                }
            )
        }
        CustomButton(onClick = {
            callbacks.onSettingsUIUpdate(SettingsUI(fontSize.toInt(), fontFamily))
        }) {
            Text("Apply")
        }
    }
}

@Preview
@Composable
fun PreviewAppearancePanel() {
    Column {
        AppearancePanel(
            settingsUI = SettingsUI.Default,
            callbacks = SettingsDialogCallbacks.Stub,
        )
    }
}