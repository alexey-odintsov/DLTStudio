package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExternalDragValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.logs.LogsPanel
import com.alekso.dltstudio.logs.LogsToolbarState
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.timeline.TimeLinePanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import java.net.URI


@OptIn(ExperimentalComposeUiApi::class, ExperimentalSplitPaneApi::class)
@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var progress by remember { mutableStateOf(0f) }
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

    // todo: User color filters goes here.
    val colorFilters by remember {
        mutableStateOf(
            mutableListOf(
                ColorFilter(
                    "SIP",
                    mapOf(FilterParameter.ContextId to "TC"),
                    CellStyle(backgroundColor = Color.Green, textColor = Color.White),
                    enabled = false,
                ),
                ColorFilter(
                    "Logcat",
                    mapOf(FilterParameter.AppId to "ALD", FilterParameter.ContextId to "LCAT"),
                    CellStyle(backgroundColor = Color.Magenta, textColor = Color.White),
                    enabled = true,
                ),
            )
        )
    }

    var searchUseRegex by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }
    val statusBarProgressCallback: (Float) -> Unit = { i -> progress = i }
    val newSessionCallback: (ParseSession) -> Unit = { newSession -> dltSession = newSession }
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset = newOffset }
    val scaleUpdateCallback: (Float) -> Unit =
        { newScale -> scale = if (newScale > 0f) newScale else 1f }

    // Logs toolbar
    var searchText by remember { mutableStateOf("") }
    val updateSearchText: (String) -> Unit = { text ->
        searchText = text
        dltSession?.searchResult?.clear()
        dltSession?.searchIndexes?.clear()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                println("Searching for $text..")
                dltSession?.let {
                    it.dltMessages.forEachIndexed { i, dltMessage ->
                        val payload = dltMessage.payload

                        if (payload != null) {
                            if ((searchUseRegex && searchText.toRegex()
                                    .containsMatchIn(payload.asText()))
                                || (payload.asText().contains(searchText))
                            ) {
                                it.searchResult.add(dltMessage)
                                it.searchIndexes.add(i)
                            }
                            statusBarProgressCallback.invoke(i.toFloat() / it.dltMessages.size)
                        }
                    }
                }
            }
        }
    }
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
    val updateSearchUseRegexCheck: (Boolean) -> Unit =
        { checked -> searchUseRegex = checked }

    val onDropCallback: (ExternalDragValue) -> Unit = {
        if (it.dragData is DragData.FilesList) {
            val filesList = it.dragData as DragData.FilesList
            val pathList = filesList.readFiles()
            println(pathList)
            if (pathList.isNotEmpty()) {
                dltSession = ParseSession(
                    statusBarProgressCallback,
                    pathList.map { path -> File(URI.create(path.substring(5)).path) }
                )
                newSessionCallback.invoke(dltSession!!)
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        dltSession?.start()
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.onExternalDrag(onDrop = onDropCallback)) {
        TabsPanel(tabIndex, listOf("Logs", "Timeline"), tabClickListener)

        when (tabIndex) {
            0 -> {
                LogsPanel(
                    modifier = Modifier.weight(1f),
                    searchText,
                    dltSession,
                    colorFilters,
                    searchUseRegex,
                    logsToolbarState,
                    updateSearchText,
                    updateToolbarFatalCheck,
                    updateToolbarErrorCheck,
                    updateToolbarWarningCheck,
                    updateSearchUseRegexCheck,
                    vSplitterState,
                    hSplitterState,
                    onFilterUpdate = { index, updatedFilter ->
                        if (index < 0 || index > colorFilters.size) {
                            colorFilters.add(updatedFilter)
                        } else colorFilters[index] = updatedFilter
                    }
                )
            }

            1 -> TimeLinePanel(
                modifier = Modifier.weight(1f),
                dltSession,
                statusBarProgressCallback,
                offset,
                offsetUpdateCallback,
                scale,
                scaleUpdateCallback
            )
        }
        Divider()
        StatusBar(modifier = Modifier.fillMaxWidth(), progress, dltSession)
    }
}

@Preview
@Composable
fun PreviewMainWindow() {
    Box(modifier = Modifier.width(400.dp).height(500.dp).background(Color.Yellow)) {
        MainWindow()
    }
}