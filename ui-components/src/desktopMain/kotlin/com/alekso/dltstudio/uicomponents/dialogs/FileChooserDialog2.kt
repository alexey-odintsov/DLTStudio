package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView

@Composable
fun FileChooserDialog2(
    dialogState: FileChooserDialogState,
    onFilesSelected: (List<File>?) -> Unit
) {
    if (dialogState.visibility) {
        val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        fileChooser.currentDirectory = File(System.getProperty("user.dir"))
//        fileChooser.dialogTitle = title
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.isAcceptAllFileFilterUsed = true
//        fileChooser.selectedFile = File(fileName ?: "")
        fileChooser.currentDirectory = null

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val files = fileChooser.selectedFiles
            println("Files $files")
            onFilesSelected(files.asList())
        } else {
            println("Files null")
            onFilesSelected(null)
        }
    }
}