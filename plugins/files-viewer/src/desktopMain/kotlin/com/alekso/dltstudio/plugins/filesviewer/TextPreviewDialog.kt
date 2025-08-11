package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.ImageButton
import dltstudio.resources.Res
import dltstudio.resources.icon_copy

@Composable
fun TextPreviewDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    fileEntry: FileEntry,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = fileEntry.name,
        state = DialogState(width = 600.dp, height = 520.dp)
    ) {
        ThemeManager.AppTheme {
            TextContent(String(fileEntry.getContent() ?: byteArrayOf()))
        }
    }
}

@Composable
fun TextContent(text: String) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val clipboardManager = LocalClipboardManager.current
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_copy,
                title = "Copy text",
                onClick = {
                    clipboardManager.setText(AnnotatedString(text))
                }
            )
        }
        HorizontalDivider(Modifier.fillMaxWidth().height(1.dp))

        val scrollState = rememberScrollState(0)
        SelectionContainer {
            Box(Modifier) {
                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 4.dp)
                        .verticalScroll(scrollState)
                        .fillMaxSize(),
                    text = text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                )
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = scrollState
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTextContent() {
    ThemeManager.CustomTheme(theme = SystemTheme(true)) {
        Box(Modifier) {
            TextContent("This is a text file content.. This is a text file content.. This is a text file content.. ")
        }
    }
}