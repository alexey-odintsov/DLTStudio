package com.alekso.dltstudio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.alekso.dltstudio.db.preferences.RecentColorFilterFileEntry
import java.io.File

interface MainMenuCallbacks {
    fun onClearColorFiltersClicked()
    fun onSettingsClicked()
    fun onOpenFileClicked()
    fun onOpenFiltersClicked()
    fun onSaveColorFilterClicked()
    fun onRecentColorFilterClicked(file: File)
    fun onRemoveAllMarksClicked()
}

@Composable
fun FrameWindowScope.MainMenu(
    callbacks: MainMenuCallbacks,
    recentColorFiltersFiles: SnapshotStateList<RecentColorFilterFileEntry>,
) {
    MenuBar {
        Menu("File") {
            Item("Open", onClick = {
                callbacks.onOpenFileClicked()
            })
            Separator()
            Item("Settings", onClick = {
                callbacks.onSettingsClicked()
            })
        }
        Menu("Color filters") {
            recentColorFiltersFiles.forEach {
                Item(it.fileName, onClick = {
                    callbacks.onRecentColorFilterClicked(File(it.path))
                })
            }
            if (recentColorFiltersFiles.isNotEmpty()) {
                Separator()
            }

            Item("Open", onClick = {
                callbacks.onOpenFiltersClicked()
            })
            Item("Save", onClick = {
                callbacks.onSaveColorFilterClicked()
            })
            Item("Clear", onClick = {
                callbacks.onClearColorFiltersClicked()
            })
        }
        Menu("Logs") {
            Item("Remove all marks ", onClick = {
                callbacks.onRemoveAllMarksClicked()
            })
        }
    }
}