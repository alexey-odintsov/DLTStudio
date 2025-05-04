package com.alekso.dltstudio.plugins.contract

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage

interface MessagesRepository {
    fun clearMessages()
    fun storeMessages(logMessages: List<LogMessage>)
    fun getMessages(): SnapshotStateList<LogMessage>
    fun getSearchResults(): SnapshotStateList<LogMessage>

    /**
     * Sets comment for a message
     * @param key - ket of the message
     * @param comment - comment to set. null to remove comment.
     */
    fun updateLogComment(key: String, comment: String?)

    /**
     * Toggle bookmark
     * @param key - key of the message to toggle
     */
    fun toggleMark(key: String)

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
}