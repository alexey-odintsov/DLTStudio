package com.alekso.dltstudio.files

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow

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
        TextContent(String(fileEntry.getContent() ?: byteArrayOf()))
    }
}

@Composable
fun TextContent(text: String) {
    val scrollState = rememberScrollState(0)
    SelectionContainer {
        Box(Modifier.background(Color.White)) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
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

@Preview
@Composable
fun PreviewTextContent() {
    Box(Modifier.size(160.dp, 20.dp).background(Color.LightGray)) {
        TextContent("This is a text file content.. This is a text file content.. This is a text file content.. ")
    }
}