package com.alekso.dltstudio.uicomponents.dialogs

import java.io.File

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
)
