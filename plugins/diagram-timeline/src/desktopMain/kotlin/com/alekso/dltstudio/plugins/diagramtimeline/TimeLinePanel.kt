package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.db.RecentTimelineFilterFileEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.AnalyzeState
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFilter
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFiltersDialog
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFiltersDialogCallbacks
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import java.awt.Cursor


@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    timeTotal: TimeFrame,
    timeFrame: TimeFrame,
    listState: LazyListState,
    analyzeState: AnalyzeState,
    timelineFilters: SnapshotStateList<TimelineFilter>,
    filtersDialogState: Boolean,
    entriesMap: SnapshotStateMap<String, ChartData<LogMessage>>,
    highlightedKeysMap: SnapshotStateMap<String, ChartKey?>,
    filtersDialogCallbacks: TimelineFiltersDialogCallbacks,
    retrieveEntriesForFilter: (filter: TimelineFilter) -> ChartData<LogMessage>?,
    onLegendResized: (Float) -> Unit = { _ -> },
    legendSize: Float,
    recentFiltersFiles: SnapshotStateList<RecentTimelineFilterFileEntry>,
    currentFilterFile: RecentTimelineFilterFileEntry?,
    toolbarCallbacks: ToolbarCallbacks,
    onCloseFiltersDialog: () -> Unit,
    selectedEntry: ChartEntry<LogMessage>? = null,
    hoveredEntry: ChartEntry<LogMessage>? = null,
    onEntrySelected: ((ChartEntry<LogMessage>) -> Unit)? = null,
    onEntryHovered: ((ChartEntry<LogMessage>?) -> Unit)? = null,
    vSplitterState: SplitPaneState,
) {
    Column(modifier = modifier.onKeyEvent { e ->
        if (e.type == KeyEventType.KeyDown) {
            when (e.key) {
                Key.A -> {
                    toolbarCallbacks.onRightClicked()
                    true
                }

                Key.D -> {
                    toolbarCallbacks.onLeftClicked()
                    true
                }

                Key.W -> {
                    toolbarCallbacks.onZoomInClicked()
                    true
                }

                Key.S -> {
                    toolbarCallbacks.onZoomOutClicked()
                    true
                }

                else -> {
                    false
                }
            }
        } else false
    }) {
        TimelineToolbar(
            analyzeState = analyzeState,
            callbacks = toolbarCallbacks,
            recentFiltersFiles = recentFiltersFiles,
            currentFilterFile = currentFilterFile,
        )

        if (filtersDialogState) {
            TimelineFiltersDialog(
                visible = true,
                onDialogClosed = onCloseFiltersDialog,
                timelineFilters = timelineFilters,
                callbacks = filtersDialogCallbacks,
            )
        }

        Divider()
        VerticalSplitPane(splitPaneState = vSplitterState) {
            first(50.dp) {
                ChartsList(
                    legendSize,
                    timeTotal,
                    timeFrame,
                    timelineFilters,
                    entriesMap,
                    highlightedKeysMap,
                    onLegendResized,
                    retrieveEntriesForFilter,
                    toolbarCallbacks,
                    selectedEntry,
                    hoveredEntry,
                    onEntrySelected,
                    onEntryHovered,
                    listState,
                    modifier
                )
            }
            second(20.dp) {
                EntryPreview(selectedEntry)
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
                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))
                            .background(SolidColor(Color.Gray), alpha = 0.50f)
                            .height(4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}



@Composable
fun LegendResizer(
    modifier: Modifier = Modifier,
    onResized: (Float) -> Unit = { _ -> },
    key: String
) {
    var finalModifier = modifier.fillMaxHeight().width(2.dp).background(color = Color.LightGray)

    finalModifier = finalModifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
        .pointerInput("legend-divider-$key") {
            detectDragGestures { change, dragAmount ->
                change.consume()
                onResized(dragAmount.x / 2f)
            }
        }
    Box(finalModifier)
}

@OptIn(ExperimentalSplitPaneApi::class)
@Preview
@Composable
fun PreviewTimeline() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(SampleData.getSampleDltMessages(20).map { LogMessage(it) })
    val callbacks = object : TimelineFiltersDialogCallbacks {
        override fun onTimelineFilterUpdate(index: Int, filter: TimelineFilter) = Unit
        override fun onTimelineFilterDelete(index: Int) = Unit
        override fun onTimelineFilterMove(index: Int, offset: Int) = Unit
    }
    TimeLinePanel(
        Modifier.fillMaxWidth().height(600.dp),
        analyzeState = AnalyzeState.IDLE,
        listState = rememberLazyListState(),
        timelineFilters = mutableStateListOf(),
        timeTotal = TimeFrame(20000L, 300000L),
        timeFrame = TimeFrame(20000L, 300000L),
        entriesMap = mutableStateMapOf(),
        highlightedKeysMap = mutableStateMapOf(),
        filtersDialogCallbacks = callbacks,
        retrieveEntriesForFilter = { i -> EventsChartData() },
        legendSize = 250f,
        recentFiltersFiles = mutableStateListOf(),
        toolbarCallbacks = ToolbarCallbacks.Stub,
        filtersDialogState = false,
        onCloseFiltersDialog = {},
        currentFilterFile = null,
        vSplitterState = SplitPaneState(1f, false)
    )
}