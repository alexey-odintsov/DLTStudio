package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.timeline.filters.TimelineFiltersDialog
import com.alekso.dltstudio.timeline.graph.TimelineDurationView
import com.alekso.dltstudio.timeline.graph.TimelineEventView
import com.alekso.dltstudio.timeline.graph.TimelineMinMaxValueView
import com.alekso.dltstudio.timeline.graph.TimelinePercentageView
import com.alekso.dltstudio.timeline.graph.TimelineSingleStateView
import com.alekso.dltstudio.timeline.graph.TimelineStateView
import com.alekso.dltstudio.utils.SampleData

private val LEGEND_WIDTH_DP = 250.dp
private val TIME_MARKER_WIDTH_DP = 140.dp
private val TIME_MARKER_HEIGHT_DP = 12.dp
private const val MOVE_TIMELINE_STEP_PX = 10

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    logMessages: SnapshotStateList<LogMessage>,
    offsetSec: Float,
    offsetUpdate: (Float) -> Unit,
    scale: Float,
    scaleUpdate: (Float) -> Unit,
    analyzeState: AnalyzeState,
    totalSeconds: Float,
    timelineFilters: SnapshotStateList<TimelineFilter>,
    timeStart: Long,
    timeEnd: Long,
    entriesMap: SnapshotStateMap<String, TimeLineEntries<*>>,
    onAnalyzeClicked: (SnapshotStateList<LogMessage>) -> Unit,
    highlightedKeysMap: SnapshotStateMap<String, String?>,
    onTimelineFilterUpdate: (Int, TimelineFilter) -> Unit,
    onTimelineFilterDelete: (Int) -> Unit,
    onTimelineFilterMove: (Int, Int) -> Unit,
    retrieveEntriesForFilter: (filter: TimelineFilter) -> TimeLineEntries<*>?,
) {
    var cursorPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var secSizePx by remember { mutableStateOf(1f) }

    val dragCallback = { pe: PointerEvent, width: Int ->
        if (logMessages.isNotEmpty()) {
            val secSize: Float = width / (totalSeconds)
            val dragAmount = pe.changes.first().position.x - pe.changes.first().previousPosition.x
            offsetUpdate(offsetSec + (dragAmount / secSize / scale))
        }
    }
    val dialogState = remember { mutableStateOf(false) }

    Column(modifier = modifier.onKeyEvent { e ->
        if (e.type == KeyEventType.KeyDown) {
            when (e.key) {
                Key.A -> {
                    offsetUpdate(offsetSec + MOVE_TIMELINE_STEP_PX / secSizePx)
                    true
                }

                Key.D -> {
                    offsetUpdate(offsetSec - MOVE_TIMELINE_STEP_PX / secSizePx)
                    true
                }

                Key.W -> {
                    scaleUpdate(scale + 1f)
                    true
                }

                Key.S -> {
                    scaleUpdate(scale - 1f)
                    true
                }

                else -> {
                    false
                }
            }
        } else false
    }) {

        TimelineToolbar(
            leftClick = { offsetUpdate(offsetSec + MOVE_TIMELINE_STEP_PX / secSizePx) },
            rightClick = { offsetUpdate(offsetSec - MOVE_TIMELINE_STEP_PX / secSizePx) },
            zoomInClick = { scaleUpdate(scale + 1f) },
            zoomOutClick = { scaleUpdate(scale - 1f) },
            zoomFitClick = {
                scaleUpdate(1f)
                offsetUpdate(0f)
            },
            analyzeState = analyzeState,
            onAnalyzeClick = { onAnalyzeClicked(logMessages) },
            onTimelineFiltersClicked = { dialogState.value = true },
        )

        if (dialogState.value) {
            TimelineFiltersDialog(
                visible = dialogState.value,
                onDialogClosed = { dialogState.value = false },
                timelineFilters = timelineFilters,
                onTimelineFilterUpdate = { i, f -> onTimelineFilterUpdate(i, f) },
                onTimelineFilterDelete = { onTimelineFilterDelete(it) },
                onTimelineFilterMove = { i, o -> onTimelineFilterMove(i, o) },
            )
        }

        Divider()

        if (logMessages.isNotEmpty()) {
            val timeFrame = TimeFrame(
                timestampStart = timeStart,
                timestampEnd = timeEnd,
                scale = scale,
                offsetSeconds = offsetSec
            )

            Text(
                "Time range: ${TimeFormatter.formatDateTime(timeStart)} .. ${
                    TimeFormatter.formatDateTime(timeEnd)
                }"
            )
            Text("Offset: ${"%.2f".format(offsetSec)}; scale: ${"%.2f".format(scale)}")

            Row {
                Box(modifier = Modifier.width(LEGEND_WIDTH_DP))
                TimeRuler(
                    Modifier.fillMaxWidth(1f),
                    offsetSec,
                    scale,
                    timeStart = timeStart,
                    timeEnd = timeEnd,
                    totalSeconds = totalSeconds.toInt()
                )
            }

            val state = rememberLazyListState()
            val panels = mutableStateListOf<@Composable () -> Unit>()

            timelineFilters.forEachIndexed { index, timelineFilter ->
                if (timelineFilter.enabled) {
                    panels.add {
                        Row {
                            TimelineLegend(
                                modifier = Modifier.width(LEGEND_WIDTH_DP).height(200.dp),
                                title = timelineFilter.name,
                                entries = entriesMap[timelineFilter.key],
                                { key ->
                                    highlightedKeysMap[timelineFilter.key] = key
                                },
                                highlightedKey = highlightedKeysMap[timelineFilter.key]
                            )
                            when (timelineFilter.diagramType) {
                                DiagramType.Percentage -> {
                                    TimelinePercentageView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = retrieveEntriesForFilter(timelineFilter) as TimeLinePercentageEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.MinMaxValue -> {
                                    TimelineMinMaxValueView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = retrieveEntriesForFilter(timelineFilter) as TimeLineMinMaxEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.State -> {
                                    TimelineStateView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = retrieveEntriesForFilter(timelineFilter) as TimeLineStateEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.SingleState -> {
                                    TimelineSingleStateView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = retrieveEntriesForFilter(timelineFilter) as TimeLineSingleStateEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.Duration -> {
                                    TimelineDurationView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = retrieveEntriesForFilter(timelineFilter) as TimeLineDurationEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.Events -> TimelineEventView(
                                    modifier = Modifier.height(200.dp).fillMaxWidth()
                                        .onPointerEvent(
                                            PointerEventType.Move,
                                            onEvent = { dragCallback(it, size.width) }),
                                    entries = retrieveEntriesForFilter(timelineFilter) as TimeLineEventEntries?,
                                    timeFrame = timeFrame,
                                    highlightedKey = highlightedKeysMap[timelineFilter.key]
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.onPointerEvent(
                        eventType = PointerEventType.Move,
                        onEvent = { event ->
                            cursorPosition = event.changes[0].position
                        }), state
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
                        scrollState = state
                    )
                )
                val textMeasurer = rememberTextMeasurer()
                Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
                    if (cursorPosition.x < LEGEND_WIDTH_DP.toPx()) return@Canvas
                    secSizePx =
                        ((size.width - LEGEND_WIDTH_DP.toPx()) * scale) / totalSeconds

                    val doesMarkerFit =
                        (size.width - cursorPosition.x) > TIME_MARKER_WIDTH_DP.toPx()

                    drawLine(
                        Color(0xFFFFFFc0),
                        Offset(cursorPosition.x, 0f),
                        Offset(cursorPosition.x, size.height)
                    )
                    val cursorOffsetSec: Float =
                        ((cursorPosition.x - LEGEND_WIDTH_DP.toPx()) / secSizePx) - offsetSec
                    val cursorTimestamp: Long =
                        (1000000L * cursorOffsetSec).toLong() + timeStart

                    drawText(
                        size = Size(TIME_MARKER_WIDTH_DP.toPx(), TIME_MARKER_HEIGHT_DP.toPx()),
                        textMeasurer = textMeasurer,
                        text = "${TimeFormatter.formatTime(cursorTimestamp)} (${
                            "%+.2f".format(
                                cursorOffsetSec
                            )
                        })",
                        topLeft = Offset(
                            if (doesMarkerFit) cursorPosition.x + 4.dp.toPx() else cursorPosition.x - TIME_MARKER_WIDTH_DP.toPx() - 4.dp.toPx(),
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
}

@Preview
@Composable
fun PreviewTimeline() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(SampleData.getSampleDltMessages(20).map { LogMessage(it) })
    TimeLinePanel(
        Modifier.fillMaxWidth().height(600.dp),
        logMessages = list,
        offsetSec = 0f,
        offsetUpdate = {},
        scale = 1f,
        scaleUpdate = { f -> },
        analyzeState = AnalyzeState.IDLE,
        totalSeconds = 2000f,
        timelineFilters = mutableStateListOf(),
        timeStart = 20000L,
        timeEnd = 300000L,
        entriesMap = mutableStateMapOf(),
        onAnalyzeClicked = {},
        highlightedKeysMap = mutableStateMapOf(),
        onTimelineFilterUpdate = { i, k -> },
        onTimelineFilterDelete = {},
        onTimelineFilterMove = { i, k -> },
        retrieveEntriesForFilter = { i -> TimeLineStateEntries() }
    )
}