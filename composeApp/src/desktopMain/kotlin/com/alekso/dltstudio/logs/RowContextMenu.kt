package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.RowContextMenuCallbacks

@Composable
fun RowContextMenu(
    i: Int,
    message: DLTMessage,
    rowContent: String,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val menuItems = mutableListOf(
        ContextMenuItem("Copy") {
            rowContextMenuCallbacks.onCopyClicked(AnnotatedString(rowContent))
        },
        ContextMenuItem("Mark") { rowContextMenuCallbacks.onMarkClicked(i, message) },
        )

    message.extendedHeader?.let {
        menuItems.add(ContextMenuItem("Remove Context ${it.contextId} from logs") {
            rowContextMenuCallbacks.onRemoveClicked("context", it.contextId)
        })
    }


    message.extendedHeader?.let {
        menuItems.add(ContextMenuItem("Remove Application ${it.applicationId} from logs") {
            rowContextMenuCallbacks.onRemoveClicked("app", it.applicationId)
        })
    }
    ContextMenuArea(items = { menuItems }) {
        content()
    }
}