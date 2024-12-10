package com.alekso.dltstudio.files

import androidx.compose.runtime.mutableStateMapOf
import com.alekso.dltstudio.model.LogMessage
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

enum class FilesState {
    IDLE,
    ANALYZING
}

private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class FilesViewModel(
    private val onProgressChanged: (Float) -> Unit
) {

    private var analyzeJob: Job? = null

    var filesEntriesMap = mutableStateMapOf<String, FileEntry>()
    var filesEntriesFLSTMap = mutableStateMapOf<String, FileEntryFLST>()

    private var _analyzeState: MutableStateFlow<FilesState> = MutableStateFlow(FilesState.IDLE)
    val analyzeState: StateFlow<FilesState> = _analyzeState

    private fun cleanup() {
        filesEntriesMap.clear()
        filesEntriesFLSTMap.clear()
    }

    fun startFilesSearch(logMessages: List<LogMessage>) {
        when (_analyzeState.value) {
            FilesState.IDLE -> startAnalyzing(logMessages)
            FilesState.ANALYZING -> stopAnalyzing()
        }

    }

    private fun stopAnalyzing() {
        analyzeJob?.cancel()
        _analyzeState.value = FilesState.IDLE    }

    fun startAnalyzing(dltMessages: List<LogMessage>) {
        cleanup()
        _analyzeState.value = FilesState.ANALYZING
        analyzeJob = CoroutineScope(Dispatchers.IO).launch {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val fileExtractor = FileExtractor()

                Log.d("Start Files analyzing .. ${dltMessages.size} messages")

                var prevTs  = System.currentTimeMillis()
                dltMessages.forEachIndexed { index, message ->
                    yield()

                    fileExtractor.searchForFiles(message.dltMessage.payload)
                    val nowTs = System.currentTimeMillis()
                    if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                        prevTs = nowTs
                        onProgressChanged(index.toFloat() / dltMessages.size)
                    }
                }

                withContext(Dispatchers.Default) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    filesEntriesMap.clear()
                    filesEntriesMap.putAll(fileExtractor.filesEntriesMap)
                    filesEntriesFLSTMap.clear()
                    filesEntriesFLSTMap.putAll(fileExtractor.filesFLSTEntriesMap)
                    _analyzeState.value = FilesState.IDLE
                }
                onProgressChanged(1f)
            }
            Log.d("Done analyzing files ${System.currentTimeMillis() - start}ms")
        }
    }
}
