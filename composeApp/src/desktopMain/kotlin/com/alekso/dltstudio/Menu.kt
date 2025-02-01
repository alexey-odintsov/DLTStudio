package com.alekso.dltstudio.com.alekso.dltstudio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.ui.FileChooserDialog
import com.alekso.dltstudio.ui.FileChooserDialogState
import java.io.File

interface MainMenuCallbacks {
    fun onOpenDLTFiles(files: List<File>)
    fun onLoadColorFiltersFile(file: File)
    fun onSaveColorFiltersFile(file: File)
    fun onLoadTimelineFiltersFile(file: File)
    fun onSaveTimelineFiltersFile(file: File)
    fun onClearColorFilters()
    fun onClearTimelineFilters()
}

@Composable
fun FrameWindowScope.MainMenu(callbacks: MainMenuCallbacks) {
    var stateIOpenFileDialog by remember { mutableStateOf(FileChooserDialogState()) }

    if (stateIOpenFileDialog.visibility) {
        FileChooserDialog(
            dialogContext = stateIOpenFileDialog.dialogContext,
            title = when (stateIOpenFileDialog.dialogContext) {
                FileChooserDialogState.DialogContext.OPEN_DLT_FILE -> "Open DLT file"
                FileChooserDialogState.DialogContext.OPEN_FILTER_FILE -> "Open filters"
                FileChooserDialogState.DialogContext.UNKNOWN -> "Open file"
                FileChooserDialogState.DialogContext.SAVE_FILTER_FILE -> "Save filter"
                FileChooserDialogState.DialogContext.OPEN_TIMELINE_FILTER_FILE -> "Open TimeLine filters"
                FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE -> "Save TimeLine filters"
                FileChooserDialogState.DialogContext.SAVE_FILE -> "Save file"
            },
            onFileSelected = { file ->
                when (stateIOpenFileDialog.dialogContext) {
                    FileChooserDialogState.DialogContext.OPEN_DLT_FILE -> {
                        file?.let {
                            callbacks.onOpenDLTFiles(listOf(it))
                        }
                    }

                    FileChooserDialogState.DialogContext.OPEN_FILTER_FILE -> {
                        file?.let {
                            callbacks.onLoadColorFiltersFile(it)
                        }
                    }

                    FileChooserDialogState.DialogContext.SAVE_FILTER_FILE -> {
                        file?.let {
                            callbacks.onSaveColorFiltersFile(it)
                        }
                    }

                    FileChooserDialogState.DialogContext.OPEN_TIMELINE_FILTER_FILE -> {
                        file?.let {
                            callbacks.onLoadTimelineFiltersFile(it)
                        }
                    }

                    FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE -> {
                        file?.let {
                            callbacks.onSaveTimelineFiltersFile(it)
                        }
                    }

                    FileChooserDialogState.DialogContext.UNKNOWN -> {

                    }

                    else -> {}
                }
                stateIOpenFileDialog = stateIOpenFileDialog.copy(visibility = false)
            },
        )
    }

    MenuBar {
        Menu("File") {
            Item("Open", onClick = {
                stateIOpenFileDialog = FileChooserDialogState(
                    true, FileChooserDialogState.DialogContext.OPEN_DLT_FILE
                )
            })
        }
        Menu("Color filters") {
            Preferences.recentColorFilters().forEach {
                Item(it.fileName, onClick = {
                    callbacks.onLoadColorFiltersFile(File(it.absolutePath))
                })
            }
            if (Preferences.recentColorFilters().isNotEmpty()) {
                Separator()
            }

            Item("Open", onClick = {
                stateIOpenFileDialog = FileChooserDialogState(
                    true, FileChooserDialogState.DialogContext.OPEN_FILTER_FILE
                )
            })
            Item("Save", onClick = {
                stateIOpenFileDialog = FileChooserDialogState(
                    true, FileChooserDialogState.DialogContext.SAVE_FILTER_FILE
                )
            })
            Item("Clear", onClick = {
                callbacks.onClearColorFilters()
            })
        }
        Menu("Timeline") {
            Menu("Filters") {
                Preferences.recentTimelineFilters().forEach {
                    Item(it.fileName, onClick = {
                        callbacks.onLoadTimelineFiltersFile(File(it.absolutePath))
                    })
                }
                if (Preferences.recentTimelineFilters().isNotEmpty()) {
                    Separator()
                }
                Item("Open", onClick = {
                    stateIOpenFileDialog = FileChooserDialogState(
                        true,
                        FileChooserDialogState.DialogContext.OPEN_TIMELINE_FILTER_FILE
                    )
                })
                Item("Save", onClick = {
                    stateIOpenFileDialog = FileChooserDialogState(
                        true,
                        FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE
                    )
                })
                Item("Clear", onClick = {
                    callbacks.onClearTimelineFilters()
                })
            }
        }
    }
}