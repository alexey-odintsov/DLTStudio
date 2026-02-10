package com.alekso.dltstudio.com.alekso.dltstudio.logs

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
    private var searchResults = MutableStateFlow<List<LogMessage>>(emptyList())
    private val selectedMessage = MutableStateFlow<LogMessage?>(null)
    val markedItemsIds = MutableStateFlow<List<Int>>(emptyList())
    var focusedMarkedIdIndex = MutableStateFlow<Int?>(null)
    private val comments = MutableStateFlow<Map<Int, String>>(emptyMap())


    override suspend fun clearMessages() {
        withContext(Main) {
            comments.value = emptyMap()
            markedItemsIds.value = emptyList()
            logMessages.value = emptyList()
            focusedMarkedIdIndex.value = null
            selectedMessage.value = null
            clearSearchResults()
        }
    }

    private suspend fun clearSearchResults() {
        searchResults.value = emptyList()
    }

    override suspend fun storeMessages(messages: List<LogMessage>) {
        logMessages.value = messages
    }


    override fun getMessages(): StateFlow<List<LogMessage>> {
        return logMessages
    }

    override fun getMarkedIds(): StateFlow<List<Int>> {
        return markedItemsIds
    }

    override fun getFocusedMarkedIdIndex(): StateFlow<Int?> {
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
                markedItemsIds.update {
                    it.toMutableList().apply { removeIf { it == logMessage.id } }
                }
                comments.update {
                    it.toMutableMap().apply {
                        remove(logMessage.id)
                    }
                }
            }
        }
        storeMessages(filtered)
        val searchFiltered = mutableListOf<LogMessage>()
        val searchDuration = forEachWithProgress(searchResults.value, progress) { i, logMessage ->
            val shouldRemove = predicate(logMessage)
            if (!shouldRemove) {
                searchFiltered.add(logMessage)
            }
        }
        searchResults.value = searchFiltered
        return duration + searchDuration
    }

    override suspend fun removeMessage(logMessage: LogMessage) {
        logMessages.update {
            it.toMutableList().apply { removeIf { it.id == logMessage.id } }
        }
        searchResults.update {
            it.toMutableList().apply { removeIf { it.id == logMessage.id } }
        }
        markedItemsIds.update {
            it.toMutableList().apply { removeIf { it == logMessage.id } }
        }
        comments.update {
            it.toMutableMap().apply {
                remove(logMessage.id)
            }
        }
    }


    override suspend fun searchMessages(
        progress: (Float) -> Unit,
        predicate: (LogMessage) -> Boolean
    ): Long {
        clearSearchResults()
        val newResults = mutableListOf<LogMessage>()

        val duration = forEachWithProgress(logMessages.value, progress) { i, logMessage ->
            currentCoroutineContext().ensureActive()

            val match = predicate(logMessage)
            if (match) {
                newResults.add(logMessage)
                // todo: emit search results with debouncing
            }
        }
        searchResults.value = newResults

        return duration
    }

    override fun selectMessage(id: Int) {
        val message = logMessages.value.firstOrNull { it.id == id }
        selectedMessage.value = message
    }


    override fun getSearchResults(): StateFlow<List<LogMessage>> {
        return searchResults
    }

    override fun getSelectedMessage(): StateFlow<LogMessage?> {
        return selectedMessage
    }

    override fun updateLogComment(id: Int, comment: String?) {
        if (comment?.isNotEmpty() == true) {
            comments.update {
                it.toMutableMap().apply {
                    put(id, comment)
                }
            }
            if (!markedItemsIds.value.contains(id)) {
                markedItemsIds.update {
                    it.toMutableList().apply {
                        add(id)
                        sort()
                    }
                }
            }
        } else {
            comments.update {
                it.toMutableMap().apply {
                    remove(id)
                }
            }
        }
    }

    override fun getComments(): StateFlow<Map<Int, String>> {
        return comments
    }

    override fun toggleMark(id: Int) {
        if (markedItemsIds.value.contains(id)) {
            markedItemsIds.update {
                it.toMutableList().apply {
                    remove(id)
                }
            }
        } else {
            markedItemsIds.update {
                it.toMutableList().apply {
                    add(id)
                    sort()
                }
            }
        }
    }

    override fun clearMarks() {
        markedItemsIds.value = emptyList()
    }

    override fun selectPrevMarkedLog() {
        if (markedItemsIds.value.isEmpty()) return
        var index = focusedMarkedIdIndex.value
        focusedMarkedIdIndex.value = if (index == null) {
            0
        } else {
            if (index > 0) {
                index - 1
            } else {
                markedItemsIds.value.size - 1
            }
        }
        index = focusedMarkedIdIndex.value
        if (index != null) {
            selectMessage(markedItemsIds.value[index])
        }
    }

    override fun selectNextMarkedLog() {
        if (markedItemsIds.value.isEmpty()) return
        var index = focusedMarkedIdIndex.value
        focusedMarkedIdIndex.value = if (index == null) {
            0
        } else {
            if (index < markedItemsIds.value.size - 1) {
                index + 1
            } else {
                0
            }
        }
        index = focusedMarkedIdIndex.value
        if (index != null) {
            selectMessage(markedItemsIds.value[index])
        }
    }

}