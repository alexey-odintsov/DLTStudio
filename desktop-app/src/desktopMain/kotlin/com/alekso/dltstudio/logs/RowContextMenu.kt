package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltstudio.LogRemoveContext
import com.alekso.dltstudio.model.contract.LogMessage

interface RowContextMenuCallbacks {
    fun onMarkClicked(i: Int, message: LogMessage)
    fun onRemoveClicked(context: LogRemoveContext, filter: String)
    fun onRemoveDialogClicked(message: LogMessage)
    fun onRemoveMessageClicked(message: LogMessage)

    companion object {
        val Stub = object : RowContextMenuCallbacks {
            override fun onMarkClicked(i: Int, message: LogMessage) = Unit
            override fun onRemoveClicked(context: LogRemoveContext, filter: String) = Unit
            override fun onRemoveDialogClicked(message: LogMessage) = Unit
            override fun onRemoveMessageClicked(message: LogMessage) = Unit
        }
    }
}

@Composable
fun RowContextMenu(
    i: Int,
    message: LogMessage,
    marked: Boolean,
    rowContent: String,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val menuItems = mutableListOf(
        ContextMenuItem("Copy") {
            clipboardManager.setText(AnnotatedString(rowContent))
        },
        ContextMenuItem(if (marked) "Unmark" else "Mark") {
            rowContextMenuCallbacks.onMarkClicked(
                i,
                message
            )
        },
    )

    message.dltMessage.extendedHeader?.contextId?.let {
        menuItems.add(ContextMenuItem("Remove context '${it}'") {
            rowContextMenuCallbacks.onRemoveClicked(LogRemoveContext.ContextId, it)
        })
    }

    message.dltMessage.extendedHeader?.applicationId?.let {
        menuItems.add(ContextMenuItem("Remove application '${it}'") {
            rowContextMenuCallbacks.onRemoveClicked(
                LogRemoveContext.ApplicationId,
                it
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

    message.dltMessage.payloadText().let {
        menuItems.add(
            ContextMenuItem("Remove by custom attributes") {
                rowContextMenuCallbacks.onRemoveDialogClicked(message)
            })
    }

    menuItems.add(
        ContextMenuItem("Remove this message") {
            rowContextMenuCallbacks.onRemoveMessageClicked(message)
        }
    )

    message.dltMessage.timeStampUs.let {
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