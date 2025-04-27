package com.alekso.dltstudio.plugins.contract

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage

interface MessagesRepository {
    fun clearMessages()
    fun storeMessages(logMessages: List<LogMessage>)
    fun getMessages(): SnapshotStateList<LogMessage>
    fun getMessageByIndex(index: Int): LogMessage
    fun updateLogComment(key: String, comment: String?)
}