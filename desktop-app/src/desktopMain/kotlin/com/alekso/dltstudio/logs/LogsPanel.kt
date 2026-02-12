package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.LogSelection
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterError
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal
import com.alekso.dltstudio.logs.colorfilters.ColorFilterWarn
import com.alekso.dltstudio.logs.infopanel.LogPreviewPanel
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.logs.toolbar.LogsToolbar
import com.alekso.dltstudio.logs.toolbar.LogsToolbarCallbacks
import com.alekso.dltstudio.logs.toolbar.LogsToolbarState
import com.alekso.dltstudio.model.ColumnParams
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.PluginLogPreview
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.skiko.Cursor


private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

private fun Modifier.cursorForVerticalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun LogsPanel(
    modifier: Modifier = Modifier,
    columnParams: List<ColumnParams>,
    logMessages: List<LogMessage>,
    previewPanels: List<PluginLogPreview>,
    searchState: SearchState,
    searchResult: List<LogMessage>,
    searchAutoComplete: List<String>,
    colorFilters: List<ColorFilter>,
    logsToolbarState: LogsToolbarState,
    logsToolbarCallbacks: LogsToolbarCallbacks,
    vSplitterState: SplitPaneState,
    hSplitterState: SplitPaneState,
    logsListState: LazyListState,
    searchListState: LazyListState,
    onLogsRowSelected: (Int, Int) -> Unit,
    onSearchRowSelected: (Int, Int) -> Unit,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    onColumnResized: (String, Float) -> Unit,
    logSelection: LogSelection,
    selectedMessage: LogMessage?,
    markedIds: List<Int>,
    focusedBookmarkId: Int?,
    comments: Map<Int, String>,
) {
    Column(modifier = modifier) {
        LogsToolbar(
            logsToolbarState,
            searchState,
            searchAutoComplete,
            callbacks = logsToolbarCallbacks,
            focusedBookmarkId = focusedBookmarkId,
            markedIds = markedIds,
        )

        HorizontalDivider()
        // TODO: Move to viewModel
        val mergedFilters = mutableStateListOf<ColorFilter>()
        mergedFilters.addAll(colorFilters)
        if (logsToolbarState.toolbarWarningChecked) {
            mergedFilters.add(ColorFilterWarn)
        }
        if (logsToolbarState.toolbarErrorChecked) {
            mergedFilters.add(ColorFilterError)
        }
        if (logsToolbarState.toolbarFatalChecked) {
            mergedFilters.add(ColorFilterFatal)
        }


        VerticalSplitPane(splitPaneState = vSplitterState) {
            first(50.dp) {
                HorizontalSplitPane(
                    splitPaneState = hSplitterState
                ) {
                    first(20.dp) {
                        LogsListPanel(
                            modifier = Modifier.fillMaxSize(),
                            columnParams = columnParams,
                            messages = logMessages,
                            markedIds = markedIds,
                            colorFilters = mergedFilters,
                            selectedRow = logSelection.logsIndex,
                            logsListState = logsListState,
                            onLogsRowSelected = onLogsRowSelected,
                            wrapContent = logsToolbarState.toolbarWrapContentChecked,
                            showComments = logsToolbarState.toolbarCommentsChecked,
                            rowContextMenuCallbacks = rowContextMenuCallbacks,
                            columnsContextMenuCallbacks = columnsContextMenuCallbacks,
                            onColumnResized = onColumnResized,
                            comments = comments,
                        )
                    }
                    second(20.dp) {
                        LogPreviewPanel(
                            Modifier.fillMaxSize(),
                            logMessage = selectedMessage,
                            previewPanels = previewPanels,
                        )
                    }
                    splitter {
                        visiblePart {
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.background)
                            )
                        }
                        handle {
                            Box(
                                Modifier
                                    .markAsHandle()
                                    .cursorForHorizontalResize()
                                    .background(SolidColor(Color.Gray), alpha = 0.50f)
                                    .width(4.dp)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
            second(20.dp) {
                SearchResultsPanel(
                    Modifier.fillMaxSize(),
                    columnParams = columnParams,
                    searchResult =searchResult,
                    colorFilters = mergedFilters,
                    searchResultSelectedRow = logSelection.searchIndex,
                    searchListState = searchListState,
                    onSearchRowSelected = onSearchRowSelected,
                    wrapContent = logsToolbarState.toolbarWrapContentChecked,
                    showComments = logsToolbarState.toolbarCommentsChecked,
                    rowContextMenuCallbacks = rowContextMenuCallbacks,
                    columnsContextMenuCallbacks = columnsContextMenuCallbacks,
                    onColumnResized = onColumnResized,
                    markedIds = markedIds,
                    comments = comments,
                )
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                handle {
                    Box(
                        Modifier
                            .markAsHandle()
                            .cursorForVerticalResize()
                            .background(SolidColor(Color.Gray), alpha = 0.50f)
                            .height(4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Preview
@Composable
fun PreviewLogsPanel() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(SampleData.getSampleDltMessages(20).map { LogMessage(it) })
    LogsPanel(
        Modifier.fillMaxSize(),
        columnParams = ColumnParams.DefaultParams,
        logMessages = list,
        previewPanels = mutableStateListOf(),
        searchState = SearchState(searchText = "Search text"),
        searchResult = SnapshotStateList(),
        searchAutoComplete = mutableStateListOf(),
        colorFilters = SnapshotStateList(),
        logsToolbarState = LogsToolbarState(
            toolbarFatalChecked = true,
            toolbarErrorChecked = true,
            toolbarWarningChecked = true,
            toolbarSearchWithMarkedChecked = false,
            toolbarWrapContentChecked = true,
            toolbarCommentsChecked = false,
        ),
        logsToolbarCallbacks = LogsToolbarCallbacks.Stub,
        vSplitterState = SplitPaneState(0.8f, true),
        hSplitterState = SplitPaneState(0.8f, true),
        logsListState = LazyListState(),
        searchListState = LazyListState(),
        onLogsRowSelected = { _, _ -> },
        onSearchRowSelected = { _, _ -> },
        rowContextMenuCallbacks = RowContextMenuCallbacks.Stub,
        columnsContextMenuCallbacks = ColumnsContextMenuCallbacks.Stub,
        onColumnResized = { _, _ -> },
        logSelection = LogSelection(0, 0),
        selectedMessage = null,
        markedIds = mutableStateListOf(),
        focusedBookmarkId = null,
        comments = mutableStateMapOf(),
    )
}