package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    val markedItemsIds = mutableStateListOf<Int>()
    var focusedMarkedIdIndex = mutableStateOf<Int?>(null)


    override suspend fun clearMessages() {
        withContext(Main) {
            logMessages.clear()
            clearSearchResults()
        }
    }

    private suspend fun clearSearchResults() {
        withContext(Main) {
            searchResults.clear()
        }
    }

    override suspend fun storeMessages(messages: List<LogMessage>) {
        withContext(Main) {
            logMessages.clear()
            logMessages.addAll(messages)
        }
    }

    private suspend fun addSearchResult(logMessages: LogMessage, index: Int) {
        withContext(Main) {
            searchResults.add(logMessages)
        }
    }

    override fun getMessages(): SnapshotStateList<LogMessage> {
        return logMessages
    }

    override fun getMarkedIds(): SnapshotStateList<Int> {
        return markedItemsIds
    }

    override fun getFocusedMarkedIdIndex(): State<Int?> {
        return focusedMarkedIdIndex
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
            } else {
                markedItemsIds.remove(logMessage.id)
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

    override suspend fun removeMessage(logMessage: LogMessage) {
        logMessages.removeIf { it.id == logMessage.id }
        searchResults.removeIf { it.id == logMessage.id }
        markedItemsIds.remove(logMessage.id)
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

    override fun selectMessage(id: Int) {
        val message = logMessages.firstOrNull { it.id == id }
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
        if (markedItemsIds.contains(id)) {
            markedItemsIds.remove(id)
        } else {
            markedItemsIds.add(id)
            markedItemsIds.sort()
        }
    }

    override fun clearMarks() {
        markedItemsIds.clear()
    }

    override fun selectPrevMarkedLog() {
        if (markedItemsIds.isEmpty()) return
        var index = focusedMarkedIdIndex.value
        focusedMarkedIdIndex.value = if (index == null) {
            0
        } else {
            if (index > 0) {
                index - 1
            } else {
                markedItemsIds.size - 1
            }
        }
        index = focusedMarkedIdIndex.value
        if (index != null) {
            selectMessage(markedItemsIds[index])
        }
    }

    override fun selectNextMarkedLog() {
        if (markedItemsIds.isEmpty()) return
        var index = focusedMarkedIdIndex.value
        focusedMarkedIdIndex.value = if (index == null) {
            0
        } else {
            if (index < markedItemsIds.size - 1) {
                index + 1
            } else {
                0
            }
        }
        index = focusedMarkedIdIndex.value
        if (index != null) {
            selectMessage(markedItemsIds[index])
        }
    }

}