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
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.logs.infopanel.LogPreviewPanel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.skiko.Cursor
import java.io.File


private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

private fun Modifier.cursorForVerticalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun LogsPanel(
    modifier: Modifier = Modifier,
    searchText: String,
    dltSession: ParseSession?,
    colorFilters: List<CellColorFilter> = emptyList(),
    logPreviewVisibility: Boolean,
    logsToolbarState: LogsToolbarState,
    updateSearchText: (String) -> Unit,
    updateToolbarFatalCheck: (Boolean) -> Unit,
    updateToolbarErrorCheck: (Boolean) -> Unit,
    updateToolbarWarningCheck: (Boolean) -> Unit,
    updateToolbarLogPreviewCheck: (Boolean) -> Unit,
    vSplitterState: SplitPaneState,
    hSplitterState: SplitPaneState,
) {
    var selectedRow by remember { mutableStateOf(0) }
    var searchResultSelectedRow by remember { mutableStateOf(0) }
    val dltMessages = dltSession?.dltMessages ?: emptyList()
    val searchResult = dltSession?.searchResult ?: emptyList()
    val searchIndexes = dltSession?.searchIndexes ?: emptyList()

    Column(modifier = modifier) {
        LogsToolbar(
            logsToolbarState,
            searchText,
            logPreviewVisibility,
            updateSearchText,
            updateToolbarFatalCheck,
            updateToolbarErrorCheck,
            updateToolbarWarningCheck,
            updateToolbarLogPreviewCheck,
        )
        Divider()

        VerticalSplitPane(splitPaneState = vSplitterState) {
            first(300.dp) {
                HorizontalSplitPane(
                    splitPaneState = hSplitterState
                ) {
                    first(20.dp) {
                        LogsListPanel(
                            Modifier.fillMaxSize(),
                            dltMessages,
                            colorFilters,
                            selectedRow,
                            selectedRowCallback = { i, messageIndex -> selectedRow = i }
                        )
                    }
                    second(20.dp) {
                        if (logPreviewVisibility) {
                            LogPreviewPanel(
                                Modifier.fillMaxSize(),
                                dltSession?.dltMessages?.getOrNull(selectedRow),
                                selectedRow
                            )
                        }
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
                    colorFilters,
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
    val dltSession = ParseSession({}, listOf(File("")))
    dltSession.dltMessages.addAll(SampleData.getSampleDltMessages(20))

    LogsPanel(
        Modifier.fillMaxSize(),
        "Search text",
        dltSession = dltSession,
        logPreviewVisibility = true,
        logsToolbarState = LogsToolbarState(
            toolbarFatalChecked = true,
            toolbarErrorChecked = true,
            toolbarWarningChecked = true,
        ),
        updateSearchText = { },
        updateToolbarFatalCheck = { },
        updateToolbarErrorCheck = { },
        updateToolbarWarningCheck = { },
        updateToolbarLogPreviewCheck = { },
        vSplitterState = SplitPaneState(0.8f, true),
        hSplitterState = SplitPaneState(0.8f, true),
    )
}