package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository

class MessagesRepositoryImpl : MessagesRepository {

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

    override fun updateLogComment(key: String, comment: String?) {
        val index = _logMessages.indexOfFirst { it.key == key }
        if (index > -1) {
            _logMessages[index] = _logMessages[index].copy(comment = comment)
        }
    }

    override fun toggleMark(key: String) {
        val index = _logMessages.indexOfFirst { it.key == key }
        if (index > -1) {
            val currentMark = _logMessages[index].marked
            _logMessages[index] = _logMessages[index].copy(marked = !currentMark)
        }
    }

}