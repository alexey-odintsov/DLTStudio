package com.alekso.dltstudio.plugins.contract

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage

interface MessagesRepository {
    fun clearMessages()
    fun clearSearchResults()
    fun storeMessages(logMessages: List<LogMessage>)
    fun addSearchResult(logMessages: LogMessage, index: Int)
    fun getMessages(): SnapshotStateList<LogMessage>
    fun getSearchResults(): SnapshotStateList<LogMessage>
    fun getSearchIndexes(): SnapshotStateList<Int>
    fun getMessageByIndex(index: Int): LogMessage
    fun updateLogComment(key: String, comment: String?)
    fun toggleMark(key: String)
}