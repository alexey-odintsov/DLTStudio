package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.runtime.Composable
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView

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
                dialogState.callback(files.asList())
            } else {
                val files = fileChooser.selectedFile
                dialogState.callback(listOf(files))
            }
        }
    }
}