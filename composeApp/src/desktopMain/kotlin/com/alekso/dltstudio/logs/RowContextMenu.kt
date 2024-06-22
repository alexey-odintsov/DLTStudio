package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.LogRemoveContext
import com.alekso.dltstudio.RowContextMenuCallbacks

@Composable
fun RowContextMenu(
    i: Int,
    message: LogMessage,
    rowContent: String,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val menuItems = mutableListOf(
        ContextMenuItem("Copy") {
            rowContextMenuCallbacks.onCopyClicked(AnnotatedString(rowContent))
        },
        ContextMenuItem(if (message.marked) "Unmark" else "Mark") {
            rowContextMenuCallbacks.onMarkClicked(
                i,
                message
            )
        },
        )

    message.dltMessage.extendedHeader?.let {
        menuItems.add(ContextMenuItem("Remove context '${it.contextId}'") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.ContextId, it.contextId)
        })
    }

    message.dltMessage.extendedHeader?.let {
        menuItems.add(ContextMenuItem("Remove application '${it.applicationId}'") {
            rowContextMenuCallbacks.onRemoveClicked(
                LogRemoveContext.ApplicationId,
                it.applicationId
            )
        })
    }

    message.dltMessage.standardHeader.ecuId?.let {
        menuItems.add(ContextMenuItem("Remove ecu '$it'") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.EcuId, it)
        })
    }

    message.dltMessage.standardHeader.sessionId?.let {
        menuItems.add(ContextMenuItem("Remove session '$it'") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.SessionId, it.toString())
        })
    }

    message.dltMessage.timeStampNano.let {
        menuItems.add(ContextMenuItem("Remove rows before") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.BeforeTimestamp, it.toString())
        })
        menuItems.add(ContextMenuItem("Remove rows after") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.AfterTimestamp, it.toString())
        })
    }

    ContextMenuArea(items = { menuItems }) {
        content()
    }
}