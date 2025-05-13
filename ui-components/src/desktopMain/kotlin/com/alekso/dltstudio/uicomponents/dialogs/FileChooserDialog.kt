package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.runtime.Composable
import com.alekso.logger.Log
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView

@Deprecated("Use FileDialogState instead")
data class FileChooserDialogState(
    val visibility: Boolean = false,
    val dialogContext: DialogContext = DialogContext.UNKNOWN
) {
    enum class DialogContext {
        OPEN_FILTER_FILE,
        SAVE_FILTER_FILE,
        SAVE_FILE,
        UNKNOWN
    }
}

@Deprecated("Use FileDialog instead")
@Composable
fun FileChooserDialog(
    title: String,
    onFileSelected: (File?) -> Unit,
    fileName: String? = null,
    dialogContext: FileChooserDialogState.DialogContext,
) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    fileChooser.isAcceptAllFileFilterUsed = true
    fileChooser.selectedFile = File(fileName ?: "")
    fileChooser.currentDirectory = null

    if (dialogContext == FileChooserDialogState.DialogContext.SAVE_FILTER_FILE ||
        dialogContext == FileChooserDialogState.DialogContext.SAVE_FILE
    ) {
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