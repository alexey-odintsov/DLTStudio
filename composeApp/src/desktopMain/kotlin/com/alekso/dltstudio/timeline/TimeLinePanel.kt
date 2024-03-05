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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.timeline.filters.TimelineFiltersDialog

private val LEGEND_WIDTH_DP = 250.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    timelineViewModel: TimelineViewModel,
    dltMessages: List<DLTMessage>,
    offsetSec: Float,
    offsetUpdate: (Float) -> Unit,
    scale: Float,
    scaleUpdate: (Float) -> Unit,
) {
    var cursorPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var secSizePx by remember { mutableStateOf(1f) }

    val dragCallback = { pe: PointerEvent, width: Int ->
        if (dltMessages.isNotEmpty()) {
            val secSize: Float = width / (timelineViewModel.totalSeconds.toFloat())
            val dragAmount = pe.changes.first().position.x - pe.changes.first().previousPosition.x
            offsetUpdate(offsetSec + (dragAmount / secSize / scale))
        }
    }
    val dialogState = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TimelineToolbar(
            leftClick = { offsetUpdate(offsetSec + 1f) },
            rightClick = { offsetUpdate(offsetSec - 1f) },
            zoomInClick = { scaleUpdate(scale + 1f) },
            zoomOutClick = { scaleUpdate(scale - 1f) },
            zoomFitClick = {
                scaleUpdate(1f)
                offsetUpdate(0f)
            },
            analyzeState = timelineViewModel.analyzeState.value,
            onAnalyzeClick = { timelineViewModel.analyzeTimeline(dltMessages) },
            onTimelineFiltersClicked = { dialogState.value = true },
        )

        if (dialogState.value) {
            TimelineFiltersDialog(
                visible = dialogState.value,
                onDialogClosed = { dialogState.value = false },
                timelineFilters = timelineViewModel.timelineFilters,
                onTimelineFilterUpdate = { i, f -> timelineViewModel.onTimelineFilterUpdate(i, f) },
                onTimelineFilterDelete = { timelineViewModel.onTimelineFilterDelete(it) },
                onTimelineFilterMove = { i, o -> timelineViewModel.onTimelineFilterMove(i, o) },
            )
        }

        Divider()

        if (dltMessages.isNotEmpty()) {
            val timeFrame = TimeFrame(
                timestampStart = timelineViewModel.timeStart,
                timestampEnd = timelineViewModel.timeEnd,
                scale = scale,
                offsetSeconds = offsetSec
            )

            Text(
                "Time range: ${TimeFormatter.formatDateTime(timelineViewModel.timeStart)} .. ${
                    TimeFormatter.formatDateTime(timelineViewModel.timeEnd)
                }"
            )
            Text("Offset: ${"%.2f".format(offsetSec)}; scale: ${"%.2f".format(scale)}")

            Row {
                Box(modifier = Modifier.width(LEGEND_WIDTH_DP))
                TimeRuler(
                    Modifier.fillMaxWidth(1f),
                    offsetSec,
                    scale,
                    timeStart = timelineViewModel.timeStart,
                    timeEnd = timelineViewModel.timeEnd,
                    totalSeconds = timelineViewModel.totalSeconds
                )
            }

            val state = rememberLazyListState()
            val panels = mutableStateListOf<@Composable () -> Unit>()
//                {
//                    Row {
//                        UserStateLegend(
//                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(100.dp),
//                            map = timelineViewModel.userStateEntries
//                        )
//                        UserStateView(
//                            offset = offsetSec,
//                            scale = scale,
//                            modifier = Modifier.height(100.dp).fillMaxWidth()
//                                .onPointerEvent(
//                                    PointerEventType.Move,
//                                    onEvent = { dragCallback(it, size.width) }),
//                            map = timelineViewModel.userStateEntries,
//                            timeStart = timelineViewModel.timeStart,
//                            timeEnd = timelineViewModel.timeEnd,
//                            totalSeconds = timelineViewModel.totalSeconds
//                        )
//                    }
//                },
//                {
//                    Row {
//                        CPUCLegend(
//                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
//                            items = timelineViewModel.cpuUsage
//                        )
//                        CPUUsageView(
//                            offset = offsetSec,
//                            scale = scale,
//                            modifier = Modifier.height(300.dp).fillMaxWidth()
//                                .onPointerEvent(
//                                    PointerEventType.Move,
//                                    onEvent = { dragCallback(it, size.width) }),
//                            items = timelineViewModel.cpuUsage,
//                            timeStart = timelineViewModel.timeStart,
//                            timeEnd = timelineViewModel.timeEnd,
//                            totalSeconds = timelineViewModel.totalSeconds
//                        )
//                    }
//                },
//                {
//                    Row {
//                        CPUSLegend(
//                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
//                            items = timelineViewModel.cpus
//                        )
//                        CPUSView(
//                            offset = offsetSec,
//                            scale = scale,
//                            modifier = Modifier.height(300.dp).fillMaxWidth()
//                                .onPointerEvent(
//                                    PointerEventType.Move,
//                                    onEvent = { dragCallback(it, size.width) }),
//                            items = timelineViewModel.cpus,
//                            timeStart = timelineViewModel.timeStart,
//                            timeEnd = timelineViewModel.timeEnd,
//                            totalSeconds = timelineViewModel.totalSeconds
//                        )
//                    }
//                },
            timelineViewModel.timelineFilters.forEachIndexed { index, timelineFilter ->
                panels.add {
                    Row {
                        TimelineLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(200.dp),
                            title = timelineFilter.name,
                            entries = timelineViewModel.userEntries.getOrNull(index),
                            { key ->
                                if (index in 0..<timelineViewModel.highlightedKeys.size) {
                                    timelineViewModel.highlightedKeys[index] = key
                                }
                            },
                            highlightedKey = timelineViewModel.highlightedKeys.getOrNull(index)
                        )
                        // TODO: distinguish diagram type
                        TimelinePercentageView(
                            modifier = Modifier.height(200.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            entries = timelineViewModel.userEntries.getOrNull(index) as TimelinePercentageEntries?,
                            timeFrame = timeFrame,
                            highlightedKey = timelineViewModel.highlightedKeys.getOrNull(index)
                        )
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
                        ((size.width - LEGEND_WIDTH_DP.toPx()) * scale) / timelineViewModel.totalSeconds.toFloat()

                    drawLine(
                        Color(0xFFFFFFc0),
                        Offset(cursorPosition.x, 0f),
                        Offset(cursorPosition.x, size.height)
                    )
                    val cursorOffsetSec: Float =
                        ((cursorPosition.x - LEGEND_WIDTH_DP.toPx()) / secSizePx) - offsetSec
                    val cursorTimestamp: Long =
                        (1000000L * cursorOffsetSec).toLong() + timelineViewModel.timeStart

                    drawText(
                        textMeasurer,
                        text = "${TimeFormatter.formatTime(cursorTimestamp)} (${
                            "%+.2f".format(
                                cursorOffsetSec
                            )
                        })",
                        topLeft = Offset(cursorPosition.x + 4.dp.toPx(), 4.dp.toPx()),
                        style = TextStyle(
                            color = Color.Yellow,
                            fontSize = 10.sp,
                            background = Color(0x80808080)
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTimeline() {
    TimeLinePanel(
        Modifier.fillMaxWidth().height(600.dp),
        dltMessages = SampleData.getSampleDltMessages(20),
        timelineViewModel = TimelineViewModel({}),
        offsetSec = 0f,
        offsetUpdate = {},
        scale = 1f,
        scaleUpdate = {},
    )
}