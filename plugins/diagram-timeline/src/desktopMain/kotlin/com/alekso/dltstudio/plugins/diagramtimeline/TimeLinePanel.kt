package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartType
import com.alekso.dltstudio.charts.ui.calculateTimestamp
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

private val TIME_MARKER_WIDTH_DP = 140.dp
private val TIME_MARKER_HEIGHT_DP = 12.dp

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
    entriesMap: SnapshotStateMap<String, ChartData>,
    highlightedKeysMap: SnapshotStateMap<String, ChartKey?>,
    filtersDialogCallbacks: TimelineFiltersDialogCallbacks,
    retrieveEntriesForFilter: (filter: TimelineFilter) -> ChartData?,
    onLegendResized: (Float) -> Unit = { _ -> },
    legendSize: Float,
    recentFiltersFiles: SnapshotStateList<RecentTimelineFilterFileEntry>,
    currentFilterFile: RecentTimelineFilterFileEntry?,
    toolbarCallbacks: ToolbarCallbacks,
    onCloseFiltersDialog: () -> Unit,
    selectedEntry: ChartEntry? = null,
    onEntrySelected: ((ChartKey, ChartEntry) -> Unit)? = null,
    vSplitterState: SplitPaneState,
) {
    var cursorPosition by remember { mutableStateOf(Offset(0f, 0f)) }

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
                charts(
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
                    onEntrySelected,
                    cursorPosition,
                    listState,
                    modifier
                )
            }
            second(20.dp) {
                entryPreview(selectedEntry)
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
fun entryPreview(selectedEntry: ChartEntry?) {
    Text("${selectedEntry}")
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun charts(
    legendSize: Float,
    timeTotal: TimeFrame,
    timeFrame: TimeFrame,
    timelineFilters: SnapshotStateList<TimelineFilter>,
    entriesMap: SnapshotStateMap<String, ChartData>,
    highlightedKeysMap: SnapshotStateMap<String, ChartKey?>,
    onLegendResized: (Float) -> Unit,
    retrieveEntriesForFilter: (filter: TimelineFilter) -> ChartData?,
    toolbarCallbacks: ToolbarCallbacks,
    selectedEntry: ChartEntry?,
    onEntrySelected: ((ChartKey, ChartEntry) -> Unit)?,
    cursorPosition: Offset,
    listState: LazyListState,
    modifier: Modifier
) {
    var cursorPosition1 = cursorPosition
    Column {
        Row {
            Box(modifier = Modifier.width(legendSize.dp))
            TimeRuler(
                Modifier.fillMaxWidth(1f),
                timeTotal = timeTotal,
                timeFrame = timeFrame,
            )
        }

        val panels = mutableStateListOf<@Composable () -> Unit>()
        val viewModifier = remember { Modifier.height(200.dp).weight(1f) }

        timelineFilters.forEachIndexed { index, timelineFilter ->
            if (timelineFilter.enabled) {
                panels.add {
                    Row {
                        TimelineLegend(
                            modifier = Modifier.width(legendSize.dp).height(200.dp),
                            title = timelineFilter.name,
                            entries = entriesMap[timelineFilter.key],
                            { key ->
                                highlightedKeysMap[timelineFilter.key] = key
                            },
                            highlightedKey = highlightedKeysMap[timelineFilter.key]
                        )
                        LegendResizer(
                            Modifier.height(200.dp),
                            key = "$index",
                            onResized = onLegendResized
                        )
                        val chartType = when (timelineFilter.diagramType) {
                            DiagramType.Percentage -> ChartType.Percentage
                            DiagramType.MinMaxValue -> ChartType.MinMax
                            DiagramType.State -> ChartType.State
                            DiagramType.SingleState -> ChartType.SingleState
                            DiagramType.Duration -> ChartType.Duration
                            DiagramType.Events -> ChartType.Events
                        }
                        Chart(
                            modifier = viewModifier,
                            entries = retrieveEntriesForFilter(timelineFilter),
                            highlightedKey = highlightedKeysMap[timelineFilter.key],
                            onDragged = toolbarCallbacks::onDragTimeline,
                            totalTime = timeFrame,
                            timeFrame = timeFrame,
                            type = chartType,
                            selectedEntry = selectedEntry,
                            onEntrySelected = onEntrySelected,
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.Companion.weight(1f)) {
            LazyColumn(
                Modifier.onPointerEvent(
                    eventType = PointerEventType.Move,
                    onEvent = { event ->
                        cursorPosition1 = event.changes[0].position
                    }),
                listState
            ) {
                items(panels.size) { i ->
                    if (i > 0) {
                        Divider(Modifier.padding(2.dp))
                    }
                    panels[i].invoke()
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = listState
                )
            )
            val textMeasurer = rememberTextMeasurer()
            val formatter = LocalFormatter.current
            Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
                if (cursorPosition1.x < legendSize.dp.toPx()) return@Canvas

                val doesMarkerFit = (size.width - cursorPosition1.x) > TIME_MARKER_WIDTH_DP.toPx()

                drawLine(
                    Color(0xff8080ff),
                    Offset(cursorPosition1.x, 0f),
                    Offset(cursorPosition1.x, size.height)
                )
                val cursorTimestamp = calculateTimestamp(
                    cursorPosition1.x - legendSize.dp.toPx(),
                    timeFrame,
                    size.width - legendSize.dp.toPx()
                )
                drawText(
                    size = Size(TIME_MARKER_WIDTH_DP.toPx(), TIME_MARKER_HEIGHT_DP.toPx()),
                    textMeasurer = textMeasurer,
                    text = formatter.formatTime(cursorTimestamp),
                    topLeft = Offset(
                        if (doesMarkerFit) cursorPosition1.x + 4.dp.toPx() else cursorPosition1.x - TIME_MARKER_WIDTH_DP.toPx() - 4.dp.toPx(),
                        4.dp.toPx()
                    ),
                    style = TextStyle(
                        color = Color.Yellow,
                        fontSize = 10.sp,
                        background = Color(0x80808080),
                        textAlign = if (doesMarkerFit) TextAlign.Left else TextAlign.Right
                    ), overflow = TextOverflow.Ellipsis
                )
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