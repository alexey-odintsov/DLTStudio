package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExternalDragValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.DLTParserV1
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.logs.LogsPanel
import com.alekso.dltstudio.logs.LogsToolbarState
import com.alekso.dltstudio.timeline.TimeLinePanel
import com.alekso.dltstudio.timeline.TimelineViewModel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import java.net.URI


@OptIn(ExperimentalComposeUiApi::class, ExperimentalSplitPaneApi::class)
@Composable
@Preview
fun MainWindow(
    mainViewModel: MainViewModel,
    timelineViewModel: TimelineViewModel,
    progress: Float,
    onProgressUpdate: (Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var tabIndex by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    val vSplitterState = rememberSplitPaneState(0.8f)
    val hSplitterState = rememberSplitPaneState(0.78f)

    var logsToolbarState by remember {
        mutableStateOf(
            LogsToolbarState(
                toolbarFatalChecked = true,
                toolbarErrorChecked = true,
                toolbarWarningChecked = true,
                toolbarWrapContentChecked = false,
            )
        )
    }

    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset = newOffset }
    val scaleUpdateCallback: (Float) -> Unit =
        { newScale -> scale = if (newScale > 0f) newScale else 1f }

    // Logs toolbar
    val searchState by mainViewModel.searchState.collectAsState()

    val updateToolbarFatalCheck: (Boolean) -> Unit =
        { checked ->
            logsToolbarState = LogsToolbarState.updateToolbarFatalCheck(logsToolbarState, checked)
        }
    val updateToolbarErrorCheck: (Boolean) -> Unit = { checked ->
        logsToolbarState = LogsToolbarState.updateToolbarErrorCheck(logsToolbarState, checked)
    }
    val updateToolbarWarningCheck: (Boolean) -> Unit =
        { checked ->
            logsToolbarState = LogsToolbarState.updateToolbarWarnCheck(logsToolbarState, checked)
        }
    val updateToolbarWrapContentCheck: (Boolean) -> Unit =
        { checked ->
            logsToolbarState = LogsToolbarState.updateToolbarWrapContentCheck(logsToolbarState, checked)
        }

    val onDropCallback: (ExternalDragValue) -> Unit = {
        if (it.dragData is DragData.FilesList) {
            val dragFilesList = it.dragData as DragData.FilesList
            val pathList = dragFilesList.readFiles()

            if (pathList.isNotEmpty()) {
                val filesList = pathList.map { path -> File(URI.create(path.substring(5)).path) }
                mainViewModel.parseFile(filesList)
            }
        }
    }

    Column(modifier = Modifier.onExternalDrag(onDrop = onDropCallback)) {
        TabsPanel(tabIndex, listOf("Logs", "Timeline"), tabClickListener)

        when (tabIndex) {
            0 -> {
                LogsPanel(
                    modifier = Modifier.weight(1f),
                    searchState = searchState,
                    searchAutoComplete = mainViewModel.searchAutocomplete,
                    dltMessages = mainViewModel.dltMessages,
                    searchResult = mainViewModel.searchResult,
                    searchIndexes = mainViewModel.searchIndexes,
                    colorFilters = mainViewModel.colorFilters,
                    logsToolbarState = logsToolbarState,
                    onSearchButtonClicked = { mainViewModel.onSearchClicked(it) },
                    updateToolbarFatalCheck = updateToolbarFatalCheck,
                    updateToolbarErrorCheck = updateToolbarErrorCheck,
                    updateToolbarWarningCheck = updateToolbarWarningCheck,
                    updateToolbarWrapContentCheck = updateToolbarWrapContentCheck,
                    onSearchUseRegexChanged = { mainViewModel.onSearchUseRegexChanged(it) },
                    vSplitterState = vSplitterState,
                    hSplitterState = hSplitterState,
                    onColorFilterDelete = { mainViewModel.onColorFilterDelete(it) },
                    onColorFilterUpdate = { i, f -> mainViewModel.onColorFilterUpdate(i, f) },
                    onColorFilterMove = { i, o -> mainViewModel.onColorFilterMove(i, o) },
                    logsListState = mainViewModel.logsListState,
                    logsListSelectedRow = mainViewModel.logsListSelectedRow.value,
                    searchListSelectedRow = mainViewModel.searchListSelectedRow.value,
                    searchListState = mainViewModel.searchListState,
                    onLogsRowSelected = { i, r -> mainViewModel.onLogsRowSelected(coroutineScope, i, r) },
                    onSearchRowSelected = { i, r -> mainViewModel.onSearchRowSelected(coroutineScope, i, r) },
                    rowContextMenuCallbacks = object : RowContextMenuCallbacks {
                        override fun onCopyClicked(text: AnnotatedString) {
                            clipboardManager.setText(text)
                        }

                        override fun onMarkClicked(i: Int, message: DLTMessage) {
                            // TODO: Mark row
                        }

                        override fun onRemoveClicked(type: String, filter: String) {
                            mainViewModel.removeMessages(type, filter)
                        }

                    }
                )
            }

            1 -> TimeLinePanel(
                modifier = Modifier.weight(1f),
                timelineViewModel = timelineViewModel,
                mainViewModel.dltMessages,
                offset,
                offsetUpdateCallback,
                scale,
                scaleUpdateCallback
            )
        }
        Divider()
        val statusText = if (mainViewModel.dltMessages.isNotEmpty()) {
            "Messages: ${"%,d".format(mainViewModel.dltMessages.size)}"
        } else {
            "No file loaded"
        }
        StatusBar(modifier = Modifier.fillMaxWidth(), progress, statusText)
    }
}

@Preview
@Composable
fun PreviewMainWindow() {
    Box(modifier = Modifier.width(400.dp).height(500.dp)) {
        MainWindow(MainViewModel(DLTParserV1(), {}), TimelineViewModel({}), 1f, {})
    }
}