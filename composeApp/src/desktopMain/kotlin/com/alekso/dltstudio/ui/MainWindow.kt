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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExternalDragValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.DLTParserV1
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.logs.LogsPanel
import com.alekso.dltstudio.logs.LogsToolbarState
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.logs.colorfilters.TextCriteria
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
    progress: Float,
    onProgressUpdate: (Float) -> Unit
) {
    val timelineViewModel = remember { TimelineViewModel(onProgressUpdate) }

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
                toolbarWarningChecked = true
            )
        )
    }

    val colorFilters = mutableStateListOf<ColorFilter>()
    // todo: User color filters goes here.
    colorFilters.addAll(
        0, listOf(
            ColorFilter(
                "SIP",
                mapOf(FilterParameter.ContextId to FilterCriteria("TC", TextCriteria.PlainText)),
                CellStyle(backgroundColor = Color.Green, textColor = Color.White),
                enabled = false,
            ),
            ColorFilter(
                "Logcat",
                mapOf(
                    FilterParameter.AppId to FilterCriteria("ALD", TextCriteria.PlainText),
                    FilterParameter.ContextId to FilterCriteria("LCAT", TextCriteria.PlainText)
                ),
                CellStyle(backgroundColor = Color.Magenta, textColor = Color.White),
                enabled = true,
            ),
            ColorFilter(
                "Regex test",
                mapOf(
                    FilterParameter.Payload to FilterCriteria("(\\d+%)", TextCriteria.Regex)
                ),
                CellStyle(backgroundColor = Color.Blue, textColor = Color.White),
                enabled = true,
            ),
        )
    )


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
                    searchState,
                    dltMessages = mainViewModel.dltMessages,
                    searchResult = mainViewModel.searchResult,
                    searchIndexes = mainViewModel.searchIndexes,
                    colorFilters = colorFilters,
                    logsToolbarState = logsToolbarState,
                    onSearchButtonClicked = { mainViewModel.onSearchClicked(it) },
                    updateToolbarFatalCheck = updateToolbarFatalCheck,
                    updateToolbarErrorCheck = updateToolbarErrorCheck,
                    updateToolbarWarningCheck = updateToolbarWarningCheck,
                    onSearchUseRegexChanged = { mainViewModel.onSearchUseRegexChanged(it) },
                    vSplitterState = vSplitterState,
                    hSplitterState = hSplitterState,
                    onFilterUpdate = { index, updatedFilter ->
                        println("onFilterUpdate $index $updatedFilter")
                        if (index < 0 || index > colorFilters.size) {
                            colorFilters.add(updatedFilter)
                        } else colorFilters[index] = updatedFilter
                    }
                ) { index ->
                    colorFilters.removeAt(index)
                }
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
        MainWindow(MainViewModel(DLTParserV1(), {}), 1f, {})
    }
}