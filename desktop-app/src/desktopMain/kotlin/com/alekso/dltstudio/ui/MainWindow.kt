package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.uicomponents.TabsPanel
import java.io.File
import java.net.URI


@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
@Preview
fun MainWindow(
    mainViewModel: MainViewModel,
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }

    Column(
        modifier = Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = {
                true
            },
            target = object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    if (event.dragData() is DragData.FilesList) {
                        val dragFilesList = event.dragData() as DragData.FilesList
                        val pathList = dragFilesList.readFiles()

                        if (pathList.isNotEmpty()) {
                            val filesList =
                                pathList.map { path -> File(URI.create(path.substring(5)).path) }
                            mainViewModel.parseFile(filesList)
                        }
                        return true
                    }
                    return false
                }
            })
    ) {
        TabsPanel(tabIndex, mainViewModel.panels.map { it.getPanelName() }.toMutableStateList(), tabClickListener)

        Row(Modifier.weight(1f)) {
            // PluginPanel as this parameter to renderPanel is unstable, so we marked PluginPanel as Stable
            (mainViewModel.panels[tabIndex]).renderPanel(modifier = Modifier.weight(1f))
        }
        Divider()
        val messagesSize = DependencyManager.provideMessageRepository().getMessages().size
        val statusText = if (messagesSize > 0) {
            "Messages: ${"%,d".format(messagesSize)}"
        } else {
            "No file loaded"
        }
        StatusBar(modifier = Modifier.fillMaxWidth(), DependencyManager.progress.value, statusText)
    }
}

@Preview
@Composable
fun PreviewMainWindow() {
    Box(modifier = Modifier.width(400.dp).height(500.dp)) {
        MainWindow(DependencyManager.provideMainViewModel())
    }
}