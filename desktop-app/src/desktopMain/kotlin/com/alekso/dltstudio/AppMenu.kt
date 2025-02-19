package com.alekso.dltstudio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

data class MainMenuItem(
    val title: String,
    val children: SnapshotStateList<ChildMenuItem> = mutableStateListOf(),
)

data class ChildMenuItem(
    val title: String,
    val callback: () -> Unit,
)

@Composable
fun FrameWindowScope.AppMenu(menuItems: SnapshotStateList<MainMenuItem>) {
    MenuBar {
        menuItems.forEach { menu ->
            Menu(menu.title) {
                menu.children.forEach { child ->
                    Item(child.title, onClick = child.callback)
                }
            }
        }
    }
}