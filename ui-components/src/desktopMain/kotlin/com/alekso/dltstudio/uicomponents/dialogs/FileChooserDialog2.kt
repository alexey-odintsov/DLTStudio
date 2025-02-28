package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView

@Composable
fun FileChooserDialog2(
    dialogState: FileDialogState,
    onFilesSelected: (List<File>?) -> Unit
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
                onFilesSelected(files.asList())
            } else {
                val files = fileChooser.selectedFile
                onFilesSelected(listOf(files))
            }
        } else {
            println("Files null")
            onFilesSelected(null)
        }
    }
}