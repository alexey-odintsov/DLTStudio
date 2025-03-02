package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
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
fun FileDialog(
    dialogState: FileDialogState,
) {
    if (dialogState.visible) {
        val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        fileChooser.currentDirectory = dialogState.directory
        fileChooser.dialogTitle = dialogState.title
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.isAcceptAllFileFilterUsed = true
        fileChooser.selectedFile = dialogState.file
        fileChooser.isMultiSelectionEnabled = dialogState.isMultiSelectionEnabled

        val result = when (dialogState.operation) {
            DialogOperation.OPEN -> fileChooser.showOpenDialog(null)
            DialogOperation.SAVE -> fileChooser.showSaveDialog(null)
        }
        if (result == JFileChooser.APPROVE_OPTION) {
            if (dialogState.isMultiSelectionEnabled) {
                val files = fileChooser.selectedFiles
                dialogState.fileCallback(files.asList())
            } else {
                val files = fileChooser.selectedFile
                dialogState.fileCallback(listOf(files))
            }
        } else {
            dialogState.cancelCallback()
        }
    }
}