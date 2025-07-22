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

enum class DialogOperation {
    OPEN,
    SAVE,
}

data class FileDialogState(
    val operation: DialogOperation,
    val title: String,
    val visible: Boolean = false,
    val file: File? = null,
    val directory: File? = null,
    val isMultiSelectionEnabled: Boolean = false,
    val fileCallback: (List<File>) -> Unit,
    val cancelCallback: () -> Unit,
)

@Composable
fun FileDialog(dialogState: FileDialogState) {
    LaunchedEffect(dialogState.visible) {
        if (dialogState.visible) {
            launch(Dispatchers.IO) {
                SwingUtilities.invokeLater {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                    } catch (_: Exception) { }

                    val fileChooser = JFileChooser(FileSystemView.getFileSystemView()).apply {
                        currentDirectory = dialogState.directory
                        dialogTitle = dialogState.title
                        fileSelectionMode = JFileChooser.FILES_ONLY
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