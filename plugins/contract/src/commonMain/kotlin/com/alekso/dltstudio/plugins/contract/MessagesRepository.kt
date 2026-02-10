package com.alekso.dltstudio.plugins.contract

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.alekso.dltstudio.model.contract.LogMessage
import kotlinx.coroutines.flow.StateFlow

interface MessagesRepository {
    suspend fun clearMessages()
    suspend fun storeMessages(messages: List<LogMessage>)
    fun getMessages(): StateFlow<List<LogMessage>>
    fun getMarkedIds(): StateFlow<List<Int>>
    fun getFocusedMarkedIdIndex(): State<Int?>
    fun getSearchResults(): StateFlow<List<LogMessage>>
    fun getSelectedMessage(): StateFlow<LogMessage?>

    /**
     * Sets comment for a message
     * @param id - id of the message
     * @param comment - comment to set. null to remove comment.
     */
    fun updateLogComment(id: Int, comment: String?)
    fun getComments(): SnapshotStateMap<Int, String>

    /**
     * Toggle bookmark
     * @param id - id of the message to toggle
     */
    fun toggleMark(id: Int)

    fun selectPrevMarkedLog()
    fun selectNextMarkedLog()

    /**
     * Remove messages and search results by a predicate
     * @param progress - removing progress callback
     * @param predicate - predicate that asses removing condition
     * @return Duration of the operation
     */
    suspend fun removeMessages(progress: (Float) -> Unit, predicate: (LogMessage) -> Boolean): Long
    suspend fun removeMessage(logMessage: LogMessage)

    /**
     * Applies search filter
     * @param progress - search progress callback
     * @param predicate - predicate that asses search condition
     * @return Duration of the operation
     * */
    suspend fun searchMessages(progress: (Float) -> Unit, predicate: (LogMessage) -> Boolean): Long
    fun selectMessage(id: Int)
    fun clearMarks()
}