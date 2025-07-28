package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.theme.ThemeManager

@Composable
fun DesktopDialogWindow(
    onCloseRequest: () -> Unit,
    state: DialogState = rememberDialogState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    content: @Composable DialogWindowScope.() -> Unit
) {
    DialogWindow(
        visible = visible,
        onCloseRequest = onCloseRequest,
        icon = icon,
        title = title,
        state = state,
    ) {
        ThemeManager.AppTheme {
            Box(Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}