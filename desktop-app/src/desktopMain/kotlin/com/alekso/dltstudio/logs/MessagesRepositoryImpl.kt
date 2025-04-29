package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository

class MessagesRepositoryImpl : MessagesRepository {

    private val _logMessages = mutableStateListOf<LogMessage>()
    private var _searchResults = mutableStateListOf<LogMessage>()
    private val _searchIndexes = mutableStateListOf<Int>()


    override fun clearMessages() {
        _logMessages.clear()
        clearSearchResults()
    }

    override fun clearSearchResults() {
        _searchResults.clear()
        _searchIndexes.clear()
    }

    override fun storeMessages(logMessages: List<LogMessage>) {
        _logMessages.clear()
        _logMessages.addAll(logMessages)
    }

    override fun addSearchResult(logMessages: LogMessage, index: Int) {
        _searchResults.add(logMessages)
        _searchIndexes.add(index)
    }

    override fun getMessages(): SnapshotStateList<LogMessage> {
        return _logMessages
    }

    override fun getSearchResults(): SnapshotStateList<LogMessage> {
        return _searchResults
    }

    override fun getSearchIndexes(): SnapshotStateList<Int> {
        return _searchIndexes
    }

    override fun getMessageByIndex(index: Int): LogMessage {
        return _logMessages[index]
    }

    override fun updateLogComment(key: String, comment: String?) {
        val index = _logMessages.indexOfFirst { it.key == key }
        if (index > -1) {
            val updatedMessage = _logMessages[index].copy(comment = comment)
            _logMessages[index] = updatedMessage
            val searchIndex = _searchResults.indexOfFirst { it.key == key }
            if (searchIndex > -1) {
                _searchResults[searchIndex] = updatedMessage
            }
        }
    }

    override fun toggleMark(key: String) {
        val index = _logMessages.indexOfFirst { it.key == key }
        if (index > -1) {
            val currentMark = _logMessages[index].marked
            val updatedMessage = _logMessages[index].copy(marked = !currentMark)
            _logMessages[index] = updatedMessage
            val searchIndex = _searchResults.indexOfFirst { it.key == key }
            if (searchIndex > -1) {
                _searchResults[searchIndex] = updatedMessage
            }
        }
    }

}