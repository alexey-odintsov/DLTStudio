package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.LogRemoveContext
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
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.ContextId, it.contextId)
        })
    }

    message.extendedHeader?.let {
        menuItems.add(ContextMenuItem("Remove Application ${it.applicationId} from logs") {
            rowContextMenuCallbacks.onRemoveClicked(
                LogRemoveContext.ApplicationId,
                it.applicationId
            )
        })
    }

    message.standardHeader.ecuId?.let {
        menuItems.add(ContextMenuItem("Remove Ecu $it from logs") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.EcuId, it)
        })
    }

    message.standardHeader.sessionId?.let {
        menuItems.add(ContextMenuItem("Remove Session $it from logs") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.SessionId, it.toString())
        })
    }

    ContextMenuArea(items = { menuItems }) {
        content()
    }
}