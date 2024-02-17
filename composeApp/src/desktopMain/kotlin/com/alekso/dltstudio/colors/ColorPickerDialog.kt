package com.alekso.dltstudio.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow

@Composable
fun ColorPickerDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    initialColor: Color,
    onColorUpdate: (Color) -> Unit,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Color Picker",
        state = DialogState(width = 300.dp, height = 320.dp)
    ) {
        ColorPicker(initialColor, onColorUpdate)
    }
}