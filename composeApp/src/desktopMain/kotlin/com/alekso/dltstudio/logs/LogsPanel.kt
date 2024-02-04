package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.ui.HorizontalDivider
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
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
    toolbarFatalChecked: Boolean,
    toolbarErrorChecked: Boolean,
    toolbarWarningChecked: Boolean,
    updateSearchText: (String) -> Unit,
    updateToolbarFatalCheck: (Boolean) -> Unit,
    updateToolbarErrorCheck: (Boolean) -> Unit,
    updateToolbarWarningCheck: (Boolean) -> Unit,
    updateToolbarLogPreviewCheck: (Boolean) -> Unit,
) {
    var selectedRow by remember { mutableStateOf(0) }
    val dltMessages = dltSession?.dltMessages ?: emptyList()
    val searchResult = mutableListOf<DLTMessage>()

    val vSplitterState = rememberSplitPaneState()
    val hSplitterState = rememberSplitPaneState()

    Column(modifier = modifier) {
        LogsToolbar(
            searchText,
            toolbarFatalChecked,
            toolbarErrorChecked,
            toolbarWarningChecked,
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
                    first(200.dp) {
                        Box(Modifier.background(Color.Red).fillMaxSize())
                    }
                    second(200.dp) {
                        Box(Modifier.background(Color.Blue).fillMaxSize())
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
            second(200.dp) {
                Box(Modifier.background(Color.Green).fillMaxSize())
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

//        VerticalSplitPane(modifier = modifier.fillMaxWidth(), splitPaneState = vSplitterState) {
//            first(300.dp) {
//                Row(modifier = modifier.fillMaxSize()) {
//                    LazyScrollable(
//                        Modifier.weight(1f).fillMaxHeight().background(Color.LightGray),
//                        dltMessages,
//                        colorFilters,
//                        selectedRow = selectedRow,
//                    ) { i -> selectedRow = i }
//
//                    if (logPreviewVisibility) {
//                        HorizontalDivider()
//                        LogPreview(
//                            Modifier.fillMaxHeight().width(300.dp),
//                            dltSession?.dltMessages?.getOrNull(selectedRow),
//                            selectedRow
//                        )
//                    }
//                }
//            }
//            second(200.dp) {
//                Column(modifier = Modifier.fillMaxSize()) {
//                    Divider()
//                    LazyScrollable(
//                        Modifier.height(200.dp).fillMaxWidth().background(Color.LightGray),
//                        searchResult,
//                        colorFilters,
//                        selectedRow = selectedRow,
//                    ) { i -> selectedRow = i }
//                }
//            }
//            splitter {
//                visiblePart {
//                    Box(
//                        Modifier
//                            .width(1.dp)
//                            .fillMaxHeight()
//                            .background(MaterialTheme.colors.background)
//                    )
//                }
//                handle {
//                    Box(
//                        Modifier
//                            .markAsHandle()
//                            .cursorForHorizontalResize()
//                            .background(SolidColor(Color.Gray), alpha = 0.50f)
//                            .width(9.dp)
//                            .fillMaxHeight()
//                    )
//                }
//            }
//        }

    }
}

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
        toolbarFatalChecked = true,
        toolbarErrorChecked = true,
        toolbarWarningChecked = true,
        updateSearchText = { },
        updateToolbarFatalCheck = { },
        updateToolbarErrorCheck = { },
        updateToolbarWarningCheck = { },
        updateToolbarLogPreviewCheck = { }
    )
}