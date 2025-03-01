package com.alekso.dltstudio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

data class MainMenuItem(
    val title: String,
    val children: SnapshotStateList<AppChildMenuItem> = mutableStateListOf(),
)

interface AppChildMenuItem

class AppChildMenuSeparator : AppChildMenuItem

data class ChildMenuItem(
    val title: String,
    val callback: () -> Unit,
) : AppChildMenuItem

@Composable
fun FrameWindowScope.AppMenu(menuItems: SnapshotStateList<MainMenuItem>) {
    MenuBar {
        menuItems.forEach { menu ->
            Menu(menu.title) {
                menu.children.forEach { child ->
                    when (child) {
                        is ChildMenuItem -> {
                            Item(child.title, onClick = child.callback)
                        }

                        is AppChildMenuSeparator -> {
                            Separator()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}