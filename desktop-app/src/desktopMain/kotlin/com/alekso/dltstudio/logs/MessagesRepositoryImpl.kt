package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.alekso.dltstudio.extraction.forEachWithProgress
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class MessagesRepositoryImpl : MessagesRepository {

    private val logMessages = MutableStateFlow<List<LogMessage>>(emptyList())
    private var searchResults = mutableStateListOf<LogMessage>()
    private val selectedMessage = mutableStateOf<LogMessage?>(null)
    val markedItemsIds = mutableStateListOf<Int>()
    var focusedMarkedIdIndex = mutableStateOf<Int?>(null)
    private val comments = mutableStateMapOf<Int, String>()


    override suspend fun clearMessages() {
        withContext(Main) {
            comments.clear()
            markedItemsIds.clear()
            logMessages.value = emptyList()
            focusedMarkedIdIndex.value = null
            selectedMessage.value = null
            clearSearchResults()
        }
    }

    private suspend fun clearSearchResults() {
        withContext(Main) {
            searchResults.clear()
        }
    }

    override suspend fun storeMessages(messages: List<LogMessage>) {
        logMessages.value = messages
    }

    private suspend fun addSearchResult(logMessages: LogMessage, index: Int) {
        withContext(Main) {
            searchResults.add(logMessages)
        }
    }

    override fun getMessages(): StateFlow<List<LogMessage>> {
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
        val duration = forEachWithProgress(logMessages.value, progress) { i, logMessage ->
            val shouldRemove = predicate(logMessage)
            if (!shouldRemove) {
                filtered.add(logMessage)
            } else {
                markedItemsIds.remove(logMessage.id)
                comments.remove(logMessage.id)
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
        logMessages.update {
            it.toMutableList().apply { removeIf { it.id == logMessage.id } }
        }
        searchResults.removeIf { it.id == logMessage.id }
        markedItemsIds.remove(logMessage.id)
        comments.remove(logMessage.id)
    }


    override suspend fun searchMessages(
        progress: (Float) -> Unit,
        predicate: (LogMessage) -> Boolean
    ): Long {
        clearSearchResults()

        val duration = forEachWithProgress(logMessages.value, progress) { i, logMessage ->
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
        val message = logMessages.value.firstOrNull { it.id == id }
        selectedMessage.value = message
    }


    override fun getSearchResults(): SnapshotStateList<LogMessage> {
        return searchResults
    }

    override fun getSelectedMessage(): State<LogMessage?> {
        return selectedMessage
    }

    override fun updateLogComment(id: Int, comment: String?) {
        if (comment?.isNotEmpty() == true) {
            comments[id] = comment
            if (!markedItemsIds.contains(id)) {
                markedItemsIds.add(id)
                markedItemsIds.sort()
            }
        } else {
            comments.remove(id)
        }
    }

    override fun getComments(): SnapshotStateMap<Int, String> {
        return comments
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