package com.alekso.dltstudio.ui

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView

@Composable
fun FileChooserDialog(
    title: String,
    onFileSelected: (File?) -> Unit
) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
    fileChooser.isAcceptAllFileFilterUsed = true
    fileChooser.selectedFile = null
    fileChooser.currentDirectory = null
    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        println("choose file or folder is: $file")
        onFileSelected(file)
    } else {
        println("No Selection ")
        onFileSelected(null)
    }
}