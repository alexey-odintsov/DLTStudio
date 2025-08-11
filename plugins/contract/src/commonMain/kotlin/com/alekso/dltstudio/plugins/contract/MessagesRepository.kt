package com.alekso.dltstudio.plugins.contract

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage

interface MessagesRepository {
    suspend fun clearMessages()
    suspend fun storeMessages(messages: List<LogMessage>)
    fun getMessages(): SnapshotStateList<LogMessage>
    fun getSearchResults(): SnapshotStateList<LogMessage>
    fun getSelectedMessage(): State<LogMessage?>

    /**
     * Sets comment for a message
     * @param id - id of the message
     * @param comment - comment to set. null to remove comment.
     */
    fun updateLogComment(id: Int, comment: String?)

    /**
     * Toggle bookmark
     * @param id - id of the message to toggle
     */
    fun toggleMark(id: Int)

    /**
     * Remove messages and search results by a predicate
     * @param progress - removing progress callback
     * @param predicate - predicate that asses removing condition
     * @return Duration of the operation
     */
    suspend fun removeMessages(progress: (Float) -> Unit, predicate: (LogMessage) -> Boolean): Long

    /**
     * Applies search filter
     * @param progress - search progress callback
     * @param predicate - predicate that asses search condition
     * @return Duration of the operation
     * */
    suspend fun searchMessages(progress: (Float) -> Unit, predicate: (LogMessage) -> Boolean): Long
    fun selectMessage(key: Int)
}