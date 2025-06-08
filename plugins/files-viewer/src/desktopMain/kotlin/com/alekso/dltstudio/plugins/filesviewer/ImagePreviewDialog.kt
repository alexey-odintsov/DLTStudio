package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.theme.ThemeManager

@Composable
fun ImagePreviewDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    fileEntry: FileEntry,
    imageBitmap: ImageBitmap,
) {
    DialogWindow(
        visible = visible,
        onCloseRequest = onDialogClosed,
        title = fileEntry.name,
        state = DialogState(width = 600.dp, height = 520.dp)
    ) {
        ThemeManager.AppTheme {
            Column(Modifier.fillMaxSize()) {
                Text(fileEntry.name)
                Image(bitmap = imageBitmap, contentDescription = "")
            }
        }
    }
}