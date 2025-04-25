package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository

class MessagesRepositoryImpl: MessagesRepository {

    private val _logMessages = mutableStateListOf<LogMessage>()
    private val logMessages: SnapshotStateList<LogMessage>
        get() = _logMessages


    override fun clearMessages() {
        _logMessages.clear()
    }

    override fun storeMessages(logMessages: List<LogMessage>) {
        _logMessages.clear()
        _logMessages.addAll(logMessages)
    }

    override fun getMessages(): SnapshotStateList<LogMessage> {
        return logMessages
    }

    override fun getMessageByIndex(index: Int): LogMessage {
        return _logMessages[index]
    }

}