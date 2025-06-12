package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.extraction.forEachWithProgress
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class MessagesRepositoryImpl : MessagesRepository {

    private val logMessages = mutableStateListOf<LogMessage>()
    private var searchResults = mutableStateListOf<LogMessage>()
    private val selectedMessage = mutableStateOf<LogMessage?>(null)

    override fun clearMessages() {
        logMessages.clear()
        clearSearchResults()
    }

    private fun clearSearchResults() {
        searchResults.clear()
    }

    override fun storeMessages(logMessages: List<LogMessage>) {
        this.logMessages.clear()
        this.logMessages.addAll(logMessages)
    }

    private fun addSearchResult(logMessages: LogMessage, index: Int) {
        searchResults.add(logMessages)
    }

    override fun getMessages(): SnapshotStateList<LogMessage> {
        return logMessages
    }

    override suspend fun removeMessages(
        progress: (Float) -> Unit,
        predicate: (LogMessage) -> Boolean
    ): Long {
        val filtered = mutableListOf<LogMessage>()
        val duration = forEachWithProgress(logMessages, progress) { i, logMessage ->
            val shouldRemove = predicate(logMessage)
            if (!shouldRemove) {
                filtered.add(logMessage)
            }
        }
        withContext(Main) {
            storeMessages(filtered)
        }
        val searchFiltered = mutableListOf<LogMessage>()
        val searchDuration = forEachWithProgress(searchResults, progress) { i, logMessage ->
            val shouldRemove = predicate(logMessage)
            if (!shouldRemove) {
                searchFiltered.add(logMessage)
            }
        }
        withContext(Main) {
            searchResults.clear()
            searchResults.addAll(searchFiltered)
        }
        return duration + searchDuration
    }


    override suspend fun searchMessages(
        progress: (Float) -> Unit,
        predicate: (LogMessage) -> Boolean
    ): Long {
        clearSearchResults()

        val duration = forEachWithProgress(logMessages, progress) { i, logMessage ->
            currentCoroutineContext().ensureActive()

            val match = predicate(logMessage)
            if (match) {
                withContext(Main) {
                    addSearchResult(logMessage, i)
                }
            }
        }
        return duration
    }

    override fun selectMessage(key: Int) {
        val message = logMessages.first { it.id == key }
        selectedMessage.value = message
    }


    override fun getSearchResults(): SnapshotStateList<LogMessage> {
        return searchResults
    }

    override fun getSelectedMessage(): State<LogMessage?> {
        return selectedMessage
    }

    override fun updateLogComment(id: Int, comment: String?) {
        val index = logMessages.indexOfFirst { it.id == id }
        if (index > -1) {
            val updatedMessage = logMessages[index].copy(comment = comment)
            logMessages[index] = updatedMessage
            if (selectedMessage.value?.id == id) {
                selectedMessage.value = updatedMessage
            }
            val searchIndex = searchResults.indexOfFirst { it.id == id }
            if (searchIndex > -1) {
                searchResults[searchIndex] = updatedMessage
            }
        }
    }

    override fun toggleMark(id: Int) {
        val index = logMessages.indexOfFirst { it.id == id }
        if (index > -1) {
            val currentMark = logMessages[index].marked
            val updatedMessage = logMessages[index].copy(marked = !currentMark)
            logMessages[index] = updatedMessage
            if (selectedMessage.value?.id == id) {
                selectedMessage.value = updatedMessage
            }
            val searchIndex = searchResults.indexOfFirst { it.id == id }
            if (searchIndex > -1) {
                searchResults[searchIndex] = updatedMessage
            }
        }
    }

}