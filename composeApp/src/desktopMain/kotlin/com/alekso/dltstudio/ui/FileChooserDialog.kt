package com.alekso.dltstudio.ui

import androidx.compose.runtime.Composable
import com.alekso.logger.Log
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView


data class FileChooserDialogState(
    val visibility: Boolean = false,
    val dialogContext: DialogContext = DialogContext.UNKNOWN
) {
    enum class DialogContext {
        OPEN_DLT_FILE,
        OPEN_FILTER_FILE,
        SAVE_FILTER_FILE,
        OPEN_TIMELINE_FILTER_FILE,
        SAVE_TIMELINE_FILTER_FILE,
        UNKNOWN
    }
}

@Composable
fun FileChooserDialog(
    title: String,
    onFileSelected: (File?) -> Unit,
    dialogContext: Any
) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
    fileChooser.isAcceptAllFileFilterUsed = true
    fileChooser.selectedFile = null
    fileChooser.currentDirectory = null

    if (dialogContext == FileChooserDialogState.DialogContext.SAVE_FILTER_FILE ||
        dialogContext == FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE ) {
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            Log.d("choose file or folder is: $file")
            onFileSelected(file)
        } else {
            Log.d("No Selection ")
            onFileSelected(null)
        }
    } else {
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            Log.d("choose file or folder is: $file")
            onFileSelected(file)
        } else {
            Log.d("No Selection ")
            onFileSelected(null)
        }
    }
}