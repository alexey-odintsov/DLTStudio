package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltparser.dlt.DLTMessage

// TODO: Implement menu
@Composable
fun RowContextMenu(
    i: Int,
    message: DLTMessage,
    rowContent: String,
    content: @Composable () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Copy") { clipboardManager.setText(AnnotatedString(rowContent)) },
            ContextMenuItem("Mark") {/*do something else*/ }
        )
    }) {
        content()
    }
}