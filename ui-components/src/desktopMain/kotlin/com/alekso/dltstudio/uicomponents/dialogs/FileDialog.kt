package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class DialogOperation {
    OPEN,
    SAVE,
}

enum class FileTypeSelection {
    FILES_ONLY,
    DIRECTORIES_ONLY,
    FILES_AND_DIRECTORIES
}

@OptIn(ExperimentalUuidApi::class)
data class FileDialogState(
    val operation: DialogOperation,
    val title: String,
    val fileTypeSelection: FileTypeSelection = FileTypeSelection.FILES_ONLY,
    val visible: Boolean = false,
    val file: File? = null,
    val directory: File? = null,
    val isMultiSelectionEnabled: Boolean = false,
    val fileCallback: (List<File>) -> Unit,
    val cancelCallback: () -> Unit,
)

@Composable
fun FileDialog(dialogState: FileDialogState) {
    LaunchedEffect(dialogState) {
        if (dialogState.visible) {
            launch(Dispatchers.IO) {
                SwingUtilities.invokeLater {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                    } catch (_: Exception) {
                    }

                    val fileChooser = JFileChooser(FileSystemView.getFileSystemView()).apply {
                        currentDirectory = dialogState.directory
                        dialogTitle = dialogState.title
                        fileSelectionMode = when (dialogState.fileTypeSelection) {
                            FileTypeSelection.FILES_ONLY -> JFileChooser.FILES_ONLY
                            FileTypeSelection.DIRECTORIES_ONLY -> JFileChooser.DIRECTORIES_ONLY
                            FileTypeSelection.FILES_AND_DIRECTORIES -> JFileChooser.FILES_AND_DIRECTORIES
                        }
                        isAcceptAllFileFilterUsed = true
                        selectedFile = dialogState.file
                        isMultiSelectionEnabled = dialogState.isMultiSelectionEnabled
                    }

                    val result = when (dialogState.operation) {
                        DialogOperation.OPEN -> fileChooser.showOpenDialog(null)
                        DialogOperation.SAVE -> fileChooser.showSaveDialog(null)
                    }

                    if (result == JFileChooser.APPROVE_OPTION) {
                        val selectedFiles = if (dialogState.isMultiSelectionEnabled) {
                            fileChooser.selectedFiles.asList()
                        } else {
                            listOf(fileChooser.selectedFile)
                        }
                        dialogState.fileCallback(selectedFiles)
                    } else {
                        dialogState.cancelCallback()
                    }
                }
            }
        }
    }
}