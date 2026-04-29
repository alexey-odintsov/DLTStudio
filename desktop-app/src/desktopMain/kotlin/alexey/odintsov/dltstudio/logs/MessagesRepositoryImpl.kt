package alexey.odintsov.dltstudio.logs

import alexey.odintsov.dltstudio.extraction.forEachWithProgress
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
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

    private fun clearSearchResults() {
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
        val removedIds = mutableSetOf<Int>()
        val duration = forEachWithProgress(logMessages.value, progress) { _, logMessage ->
            val shouldRemove = predicate(logMessage)
            if (shouldRemove) {
                removedIds.add(logMessage.id)
            }
        }

        if (removedIds.isNotEmpty()) {
            markedItemsIds.update { it.filter { id -> id !in removedIds } }
            comments.update { it.filterKeys { id -> id !in removedIds } }
        }

        logMessages.value = logMessages.value.filter { it.id !in removedIds }
        searchResults.value = searchResults.value.filter { it.id !in removedIds }
        return duration
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
        val start = System.currentTimeMillis()
        var prevTs = start

        val duration = forEachWithProgress(logMessages.value, progress) { _, logMessage ->
            currentCoroutineContext().ensureActive()

            val match = predicate(logMessage)
            if (match) {
                newResults.add(logMessage)
            }
            val nowTs = System.currentTimeMillis()
            if (nowTs - prevTs > 100) {
                prevTs = nowTs
                searchResults.value = newResults.toList()
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
        val ids = markedItemsIds.value
        if (ids.isEmpty()) return
        val currentIndex = focusedMarkedIdIndex.value
        val nextIndex = if (currentIndex == null || currentIndex >= ids.size || currentIndex <= 0) {
            ids.size - 1
        } else {
            currentIndex - 1
        }
        focusedMarkedIdIndex.value = nextIndex
        ids.getOrNull(nextIndex)?.let {
            selectMessage(it)
        }
    }

    override fun selectNextMarkedLog() {
        val ids = markedItemsIds.value
        if (ids.isEmpty()) return
        val currentIndex = focusedMarkedIdIndex.value
        val nextIndex = if (currentIndex == null || currentIndex >= ids.size - 1 || currentIndex < 0) {
            0
        } else {
            currentIndex + 1
        }
        focusedMarkedIdIndex.value = nextIndex
        ids.getOrNull(nextIndex)?.let {
            selectMessage(it)
        }
    }

}
