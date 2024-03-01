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
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterError
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal
import com.alekso.dltstudio.logs.colorfilters.ColorFilterWarn
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialog
import com.alekso.dltstudio.logs.infopanel.LogPreviewPanel
import com.alekso.dltstudio.logs.search.SearchState
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
    dltMessages: List<DLTMessage>,
    // search
    searchState: SearchState,
    searchResult: List<DLTMessage>,
    searchIndexes: List<Int>,
    onSearchButtonClicked: (String) -> Unit,
    onSearchUseRegexChanged: (Boolean) -> Unit,
    // color filters
    colorFilters: List<ColorFilter>,
    onColorFilterUpdate: (Int, ColorFilter) -> Unit,
    onColorFilterDelete: (Int) -> Unit,
    // toolbar
    logsToolbarState: LogsToolbarState,
    updateToolbarFatalCheck: (Boolean) -> Unit,
    updateToolbarErrorCheck: (Boolean) -> Unit,
    updateToolbarWarningCheck: (Boolean) -> Unit,
    // split bar
    vSplitterState: SplitPaneState,
    hSplitterState: SplitPaneState,
) {
    var selectedRow by remember { mutableStateOf(0) }
    var searchResultSelectedRow by remember { mutableStateOf(0) }
    val dialogState = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        LogsToolbar(
            logsToolbarState,
            searchState,
            onSearchButtonClicked,
            updateToolbarFatalCheck,
            updateToolbarErrorCheck,
            updateToolbarWarningCheck,
            onSearchUseRegexChanged,
            onColorFiltersClicked = { dialogState.value = true }
        )

        if (dialogState.value) {
            ColorFiltersDialog(
                visible = dialogState.value,
                onDialogClosed = { dialogState.value = false },
                colorFilters = colorFilters,
                onColorFilterUpdate = onColorFilterUpdate,
                onColorFilterDelete = onColorFilterDelete,
            )
        }

        Divider()
        val mergedFilters = mutableListOf<ColorFilter>()
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
            first(300.dp) {
                HorizontalSplitPane(
                    splitPaneState = hSplitterState
                ) {
                    first(20.dp) {
                        LogsListPanel(
                            Modifier.fillMaxSize(),
                            dltMessages,
                            mergedFilters,
                            selectedRow,
                            selectedRowCallback = { i, messageIndex -> selectedRow = i }
                        )
                    }
                    second(20.dp) {
                        LogPreviewPanel(
                            Modifier.fillMaxSize(),
                            dltMessages.getOrNull(selectedRow),
                            selectedRow
                        )
                    }
                    splitter {
                        visiblePart {
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colors.background)
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
                    searchResult,
                    searchIndexes,
                    mergedFilters,
                    searchResultSelectedRow,
                    selectedRowCallback = { i, messageIndex ->
                        if (searchResultSelectedRow == i) {
                            selectedRow = messageIndex
                        } else {
                            searchResultSelectedRow = i
                        }
                    },
                )
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
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
    LogsPanel(
        Modifier.fillMaxSize(),
        searchState = SearchState(searchText = "Search text"),
        dltMessages = SampleData.getSampleDltMessages(20),
        searchResult = emptyList(),
        searchIndexes = emptyList(),
        logsToolbarState = LogsToolbarState(
            toolbarFatalChecked = true,
            toolbarErrorChecked = true,
            toolbarWarningChecked = true,
        ),
        onSearchButtonClicked = { },
        updateToolbarFatalCheck = { },
        updateToolbarErrorCheck = { },
        updateToolbarWarningCheck = { },
        onSearchUseRegexChanged = { },
        vSplitterState = SplitPaneState(0.8f, true),
        hSplitterState = SplitPaneState(0.8f, true),
        colorFilters = emptyList(),
        onColorFilterDelete = { i -> },
        onColorFilterUpdate = { i, f -> }
    )
}