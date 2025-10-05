package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.alekso.dltstudio.extraction.forEachWithProgress
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.uicomponents.dialogs.DialogOperation
import com.alekso.dltstudio.uicomponents.dialogs.FileDialogState
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.io.File

enum class FilesState {
    IDLE,
    ANALYZING
}

abstract class PreviewState(
    open val type: Type = Type.None,
    open val entry: FileEntry? = null,
) {
    enum class Type {
        None,
        Text,
        Image,
    }
}

data class TextPreviewState(override val entry: FileEntry) : PreviewState(Type.Text, entry)
data class FilePreviewState(override val entry: FileEntry) : PreviewState(Type.None, entry)
data class ImagePreviewState(override val entry: FileEntry, val imageBitmap: ImageBitmap) :
    PreviewState(Type.Image, entry)


class FilesViewModel(
    private val onProgressChanged: (Float) -> Unit
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    private var _previewState: MutableState<PreviewState?> = mutableStateOf(null)
    val previewState: State<PreviewState?> = _previewState

    var fileDialogState by mutableStateOf(
        FileDialogState(
            title = "Save file",
            operation = DialogOperation.SAVE,
            file = previewState.value?.entry?.name?.let { File(it) },
            fileCallback = { saveFile(it[0]) },
            cancelCallback = ::closeFileDialog
        )
    )

    private fun closeFileDialog() {
        fileDialogState = fileDialogState.copy(visible = false)
    }

    private var analyzeJob: Job? = null

    var filesEntries = mutableStateListOf<FileEntry>()

    private var _analyzeState: MutableState<FilesState> = mutableStateOf(FilesState.IDLE)
    val analyzeState: State<FilesState> = _analyzeState

    private fun cleanup() {
        filesEntries.clear()
    }

    fun startFilesSearch(logMessages: List<LogMessage>) {
        when (_analyzeState.value) {
            FilesState.IDLE -> startAnalyzing(logMessages)
            FilesState.ANALYZING -> stopAnalyzing()
        }

    }

    private fun stopAnalyzing() {
        analyzeJob?.cancel()
        _analyzeState.value = FilesState.IDLE
    }

    private fun startAnalyzing(dltMessages: List<LogMessage>) {
        cleanup()
        _analyzeState.value = FilesState.ANALYZING
        analyzeJob = viewModelScope.launch(Default) {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val fileExtractor = FileExtractor()

                Log.d("Start Files analyzing .. ${dltMessages.size} messages")
                forEachWithProgress(dltMessages, onProgressChanged) { index, message ->
                    try {
                        fileExtractor.searchForFiles(message.dltMessage)
                    } catch (e: Exception) {
                        Log.e(e.toString())
                    }
                }

                withContext(Main) {
                    filesEntries.clear()
                    filesEntries.addAll(fileExtractor.filesMap.values.toList().sortedBy { it.name })
                    _analyzeState.value = FilesState.IDLE
                }
            }
            Log.d("Done analyzing files ${System.currentTimeMillis() - start}ms")
        }
    }

    fun onFileClicked(entry: FileEntry) {
        viewModelScope.launch {
            println("On file clicked ${entry.name}")

            when (entry.getExtension()) {
                "png" -> {
                    val bytes = entry.getContent()
                    if (bytes != null) {
                        _previewState.value = ImagePreviewState(
                            entry = entry,
                            imageBitmap = bytes.decodeToImageBitmap()
                        )
                    }
                }

                "txt" -> {
                    _previewState.value = TextPreviewState(entry = entry)
                }

                else -> {
                    _previewState.value = FilePreviewState(entry = entry)
//                    fileDialogState = FileDialogState(
//                        title = "Save file",
//                        visible = true,
//                        operation = DialogOperation.SAVE,
//                        file = File(entry.name),
//                        fileCallback = { saveFile(it[0]) },
//                        cancelCallback = { fileDialogState = fileDialogState.copy(visible = false) }
//                    )
                }
            }
        }
    }

    private fun saveFile(file: File) {
        fileDialogState = fileDialogState.copy(visible = false)
        viewModelScope.launch(IO) {
            try {
                val fileEntry = _previewState.value
                if (fileEntry is FilePreviewState) {
                    fileEntry.entry.getContent()?.let {
                        file.outputStream().write(it)
                    }
                    println("Saving entry to ${file.absolutePath}")
                }
            } catch (e: Exception) {
                Log.e("Failed to save file: $e")
            }
        }
    }

    fun clearState() {
        cleanup()
    }
}
